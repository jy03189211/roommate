package fi.aalto_iot.tomato.activity.room;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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

    private Bundle bundle;
    private RoomHistoryCanvasView temperature_canvasView;
    private RoomHistoryCanvasView co2_canvasView;
    private RoomHistoryCanvasView humidity_canvasView;
    private RoomModel room;
    OkHttpClient client = new OkHttpClient();
    List<SensorData> temperature_sensor_data = new ArrayList<>();
    List<SensorData> co2_sensor_data = new ArrayList<>();
    List<SensorData> humidity_sensor_data = new ArrayList<>();

    public Room_history_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        Realm realm = Realm.getDefaultInstance();
        room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_history_fragment, container, false);
        temperature_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.temperature_canvasView);
        co2_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.co2_canvasView);
        humidity_canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.humidity_canvasView);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, -24);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        String time = df.format(c.getTime());
        fetchSensor("temperature", time);
        fetchSensor("humidity", time);
        fetchSensor("co2", time);

        return view;
    }

    private void fetchSensor(final String sensor, String filter) {

        Request req = new Request.Builder()
                .url(room.getUrl() + sensor + "?from=" + filter)
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                // TODO: failure handling
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //swipeContainer.setRefreshing(false);
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
                final String jsonString = response.body().string();
                JSONArray json = null;
                try {
                    json = new JSONArray(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                    try {
                        if (json != null) {
                            if (sensor == "temperature") {
                                temperature_sensor_data.clear();
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("temperature"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    temperature_sensor_data.add(data);
                                }
                            } else if (sensor == "co2") {
                                co2_sensor_data.clear();
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("concentration"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    co2_sensor_data.add(data);
                                }
                            } else if (sensor == "humidity") {
                                humidity_sensor_data.clear();
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject sensor_object = (JSONObject) json.get(i);
                                    SensorData data = new SensorData();
                                    data.setData(sensor_object.getInt("humidity"));
                                    data.setTime(sensor_object.getString("timestamp"));
                                    humidity_sensor_data.add(data);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Activity activity = getActivity();
                    Context context = null;
                    if (activity != null) {
                        context = activity.getApplicationContext();
                        if (context != null) {
                            BaseApplication app = (BaseApplication) context;
                            app.setLastFetchedDataMainActivity(android.os.SystemClock.elapsedRealtime());
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateContent(sensor);
                                //swipeContainer.setRefreshing(false);
                            }
                        });
                    }
            }
        });
    }

    private void updateContent(String sensor) {
        if (sensor == "temperature")
            temperature_canvasView.setData(temperature_sensor_data);
        else if (sensor == "humidity")
            humidity_canvasView.setData(humidity_sensor_data);
        else if (sensor == "co2")
            co2_canvasView.setData(co2_sensor_data);
    }
}
