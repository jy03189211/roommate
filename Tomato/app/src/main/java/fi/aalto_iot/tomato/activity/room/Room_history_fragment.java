package fi.aalto_iot.tomato.activity.room;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import fi.aalto_iot.tomato.BaseApplication;
import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.room.view.RoomHistoryCanvasView;
import fi.aalto_iot.tomato.db.data.RoomModel;
import fi.aalto_iot.tomato.other.SensorData;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Room_history_fragment extends Fragment {

    private final static String TEMPERATURE = "temperature";
    private final static String HUMIDITY = "humidity";
    private final static String CO2 = "co2";

    private SharedPreferences sharedPreferences;
    private RoomHistoryCanvasView temperature_canvasView;
    private RoomHistoryCanvasView co2_canvasView;
    private RoomHistoryCanvasView humidity_canvasView;
    private SwipeRefreshLayout swipeContainer;

    private RoomModel room;
    private OkHttpClient client = new OkHttpClient();
    private int days;
    private int next_sample;

    private String TAG = "Room history fragment";

    public Room_history_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        days = bundle.getInt("days");
        next_sample = 1;
        if (days > 1) {
            next_sample = 10;
        } else if (days > 7) {
            next_sample = 100;
        }
        Realm realm = Realm.getDefaultInstance();
        room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getContext().getApplicationContext());
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_history_fragment, container, false);
        temperature_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.temperature_canvasView);
        TextView temperatureInfo = (TextView) view.findViewById(R.id.temperature_time_interval);
        co2_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.co2_canvasView);
        TextView co2Info = (TextView) view.findViewById(R.id.co2_time_interval);
        humidity_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.humidity_canvasView);
        TextView humidity_Info = (TextView) view.findViewById(R.id.humidity_time_interval);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerRoomHistory);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, - 24 * days);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
        String time = df.format(c.getTime());

        temperature_canvasView.setScale(0, 40);
        humidity_canvasView.setScale(0, 100);
        co2_canvasView.setScale(0, 2000);

        if (days > 1) {
            temperature_canvasView.showDates(true);
            co2_canvasView.showDates(true);
            humidity_canvasView.showDates(true);

            final String info = String.format(getResources().getString(R.string.room_fragment_history_info), days, getResources().getString(R.string.room_fragment_history_days));
            temperatureInfo.setText(info);
            humidity_Info.setText(info);
            co2Info.setText(info);
        } else {
            final String info = String.format(getResources().getString(R.string.room_fragment_history_info), days * 24, getResources().getString(R.string.room_fragment_history_hours));
            temperatureInfo.setText(info);
            humidity_Info.setText(info);
            co2Info.setText(info);
        }

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerRoomHistory);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR, - 24 * days);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
                String time = df.format(c.getTime());

                AtomicInteger counter = new AtomicInteger();
                counter.set(3);

                fetchSensor(TEMPERATURE, time, counter);
                fetchSensor(HUMIDITY, time, counter);
                fetchSensor(CO2, time, counter);
            }
        });

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                String temp = sharedPreferences.getString(TEMPERATURE + Integer.toString(days), "[]");
                String hum = sharedPreferences.getString(HUMIDITY + Integer.toString(days), "[]");
                String co2 = sharedPreferences.getString(CO2 + Integer.toString(days), "[]");

                final List<SensorData> temperature_sensor_data = new ArrayList<>();
                final List<SensorData> co2_sensor_data = new ArrayList<>();
                final List<SensorData> humidity_sensor_data = new ArrayList<>();

                JSONArray jsonTemp;
                try {
                        jsonTemp = new JSONArray(temp);
                        for (int i = 0; i < jsonTemp.length(); i += next_sample) {
                            JSONObject sensor_object = (JSONObject) jsonTemp.get(i);
                            SensorData data = new SensorData();
                            data.setData(sensor_object.getInt("temperature"));
                            data.setTime(sensor_object.getString("timestamp"));
                            temperature_sensor_data.add(data);
                        }
                        if (temperature_sensor_data.size() > 0)
                            updateContent("temperature", temperature_sensor_data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray jsonHum;
                try {
                    jsonHum = new JSONArray(hum);
                        for (int i = 0; i < jsonHum.length(); i+= next_sample) {
                            JSONObject sensor_object = (JSONObject) jsonHum.get(i);
                            SensorData data = new SensorData();
                            data.setData(sensor_object.getInt("humidity"));
                            data.setTime(sensor_object.getString("timestamp"));
                            humidity_sensor_data.add(data);
                        }
                        if (humidity_sensor_data.size() > 0)
                            updateContent("humidity", humidity_sensor_data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONArray jsonCo2;
                try {
                        jsonCo2 = new JSONArray(co2);
                        for (int i = 0; i < jsonCo2.length(); i+= next_sample) {
                            JSONObject sensor_object = (JSONObject) jsonCo2.get(i);
                            SensorData data = new SensorData();
                            data.setData(sensor_object.getInt("concentration"));
                            data.setTime(sensor_object.getString("timestamp"));
                            co2_sensor_data.add(data);
                        }
                        if (co2_sensor_data.size() > 0)
                            updateContent("co2", co2_sensor_data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        fetchSensor(TEMPERATURE, time);
        fetchSensor(HUMIDITY, time);
        fetchSensor(CO2, time);

        return view;
    }

    private void decrementOrSetRefreshFalse(SwipeRefreshLayout refresh, AtomicInteger counter) {
        if (counter != null) {
            int val = counter.decrementAndGet();
            if (val == 0) {
                refresh.setRefreshing(false);
            }
        }
    }

    private void fetchSensor(final String sensor, String filter) {
        fetchSensor(sensor, filter, null);
    }


    private void fetchSensor(final String sensor, String filter, final AtomicInteger counter) {

        Request req = new Request.Builder()
                .url(room.getUrl() + sensor + "?from=" + filter)
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //swipeContainer.setRefreshing(false);
                            decrementOrSetRefreshFalse(swipeContainer, counter);
                            Activity activity = getActivity();
                            if (activity != null) {
                                Context context = activity.getApplicationContext();
                                CharSequence text = getResources().getString(R.string.failed_to_download);
                                int duration = Toast.LENGTH_SHORT;
                                if (context != null) {
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws java.io.IOException {
                final List<SensorData> temperature_sensor_data = new ArrayList<>();
                final List<SensorData> co2_sensor_data = new ArrayList<>();
                final List<SensorData> humidity_sensor_data = new ArrayList<>();

                final String jsonString = response.body().string();
                JSONArray json = null;
                try {
                    json = new JSONArray(jsonString);
                } catch (Exception e) {
                    // no data
                    //e.printStackTrace();
                    Log.d(TAG, "No data");
                    Log.d(TAG, jsonString);
                }

                try {
                    if (json != null) {
                        switch (sensor) {
                            case TEMPERATURE:
                                sharedPreferences.edit().putString("temperature" + Integer.toString(days), jsonString).apply();
                                temperature_sensor_data.clear();
                                for (int i = 0; i < json.length(); i += next_sample) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("temperature"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    temperature_sensor_data.add(data);
                                }
                                break;
                            case HUMIDITY:
                                sharedPreferences.edit().putString("humidity" + Integer.toString(days), jsonString).apply();
                                humidity_sensor_data.clear();
                                for (int i = 0; i < json.length(); i += next_sample) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("humidity"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    humidity_sensor_data.add(data);
                                }
                                break;
                            case CO2:
                                sharedPreferences.edit().putString("co2" + Integer.toString(days), jsonString).apply();
                                co2_sensor_data.clear();
                                for (int i = 0; i < json.length(); i += next_sample) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("concentration"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    co2_sensor_data.add(data);
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Activity activity = getActivity();
                Context context;
                if (activity != null) {
                    context = activity.getApplicationContext();
                    if (context != null) {
                        BaseApplication app = (BaseApplication) context;
                        app.setLastFetchedDataMainActivity(android.os.SystemClock.elapsedRealtime());
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (sensor) {
                                case TEMPERATURE:
                                    updateContent(sensor, temperature_sensor_data);
                                    break;
                                case HUMIDITY:
                                    updateContent(sensor, humidity_sensor_data);
                                    break;
                                case CO2:
                                    updateContent(sensor, co2_sensor_data);
                                    break;
                            }
                            decrementOrSetRefreshFalse(swipeContainer, counter);
                        }
                    });
                }
            }
        });
    }

    private void updateContent(String sensor, List<SensorData> list) {
        switch (sensor) {
            case TEMPERATURE:
                temperature_canvasView.setData(list);
                break;
            case HUMIDITY:
                humidity_canvasView.setData(list);
                break;
            case CO2:
                co2_canvasView.setData(list);
                break;
        }
    }
}