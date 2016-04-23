package fi.aalto_iot.tomato.activity.room;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import fi.aalto_iot.tomato.BaseApplication;
import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.db.data.RoomModel;
import fi.aalto_iot.tomato.other.Constants;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class Room_default_fragment extends Fragment {


    private RoomModel room;
    private Bundle bundle;
    private TextView mTemperature;
    private TextView mCO2;
    private TextView mHumidity;
    private TextView mOccupation;
    private ImageView mOccupationImage;
    private TextView mAirQuality;
    private ImageView mAirQualityImage;

    private ImageView mTemperature_bar_1;
    private ImageView mTemperature_bar_2;
    private ImageView mTemperature_bar_3;
    private ImageView mTemperature_bar_4;
    private ImageView mTemperature_bar_5;

    private ImageView mHumidity_bar_1;
    private ImageView mHumidity_bar_2;
    private ImageView mHumidity_bar_3;
    private ImageView mHumidity_bar_4;
    private ImageView mHumidity_bar_5;

    private ImageView mCo2_bar_1;
    private ImageView mCo2_bar_2;
    private ImageView mCo2_bar_3;
    private ImageView mCo2_bar_4;
    private ImageView mCo2_bar_5;

    private SwipeRefreshLayout swipeContainer;
    private OkHttpClient client = new OkHttpClient();
    private static final String TAG = "room default fragment";

    public Room_default_fragment() {
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
        View view = inflater.inflate(R.layout.fragment_room_default_fragment_2, container, false);
        mTemperature = (TextView) view.findViewById(R.id.temperature_text);
        mCO2 = (TextView) view.findViewById(R.id.co2_text);
        mHumidity = (TextView) view.findViewById(R.id.humidity_text);
        mOccupationImage = (ImageView) view.findViewById(R.id.room_status_indicator_circle);
        mOccupation = (TextView) view.findViewById(R.id.room_status_indicator_text);

        mAirQuality = (TextView) view.findViewById(R.id.room_air_indicator_text);
        mAirQualityImage = (ImageView) view.findViewById(R.id.room_air_indicator_circle);

        // set all the bars

        // temperature bar
        mTemperature_bar_1 = (ImageView)view.findViewById(R.id.temperature_bar_1);
        mTemperature_bar_2 = (ImageView)view.findViewById(R.id.temperature_bar_2);
        mTemperature_bar_3 = (ImageView)view.findViewById(R.id.temperature_bar_3);
        mTemperature_bar_4 = (ImageView)view.findViewById(R.id.temperature_bar_4);
        mTemperature_bar_5 = (ImageView)view.findViewById(R.id.temperature_bar_5);

        mHumidity_bar_1 = (ImageView)view.findViewById(R.id.humidity_bar_1);
        mHumidity_bar_2 = (ImageView)view.findViewById(R.id.humidity_bar_2);
        mHumidity_bar_3 = (ImageView)view.findViewById(R.id.humidity_bar_3);
        mHumidity_bar_4 = (ImageView)view.findViewById(R.id.humidity_bar_4);
        mHumidity_bar_5 = (ImageView)view.findViewById(R.id.humidity_bar_5);

        mCo2_bar_1 = (ImageView)view.findViewById(R.id.co2_bar_1);
        mCo2_bar_2 = (ImageView)view.findViewById(R.id.co2_bar_2);
        mCo2_bar_3 = (ImageView)view.findViewById(R.id.co2_bar_3);
        mCo2_bar_4 = (ImageView)view.findViewById(R.id.co2_bar_4);
        mCo2_bar_5 = (ImageView)view.findViewById(R.id.co2_bar_5);

        // humidity bar
        // co2 bar

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerRoomDefault);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRoom(room.getUrl());
            }
        });

        if (shouldFetchNewData()) {
            fetchRoom(room.getUrl());
        } else {
            updateContent();
        }

        return view;
    }

    public void fetchRoom(final String roomUrl) {
        Request req = new Request.Builder()
                .url(roomUrl)
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
                            swipeContainer.setRefreshing(false);
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
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (json != null) {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();
                        realm.beginTransaction();
                        room.setId(json.getInt("id"));
                        room.setRoomName(json.getString("name"));
                        room.setUrl(json.getString("url"));
                        room.setOccupation(!json.getBoolean("available"));
                        room.setOrganization(json.getString("organization"));
                        room.setLocation(json.getString("location"));
                        room.setSize(json.getInt("size"));
                        room.setCo2(json.getInt("co2"));
                        room.setTemperature(json.getInt("temperature"));
                        room.setHumidity(json.getInt("humidity"));
                        room.setPicture(json.getString("picture"));
                        realm.copyToRealmOrUpdate(room);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        e.printStackTrace();
                        realm.cancelTransaction();
                    }
                    realm.close();

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
                                updateContent();
                            }
                        });
                    }

                }
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private void updateContent() {
        Realm realm = Realm.getDefaultInstance();
        room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();

        final int roomTemp = room.getTemperature();
        final int roomHumidity = room.getHumidity();
        final int roomCo2 = room.getCo2();

        final String temperature = String.format(getResources().
                getString(R.string.room_temperature_value), roomTemp < 0 ? '-' : '+', roomTemp);
        mTemperature.setText(temperature);
        final String co2 = String.format(getResources().getString(R.string.room_co2_value), roomCo2);
        mCO2.setText(co2);
        final String humidity = String.format(getResources().getString(R.string.room_humidity_value), roomHumidity);
        mHumidity.setText(humidity);

        // Set correct status indicator ("traffic light") color
        final Drawable statusIndicator = room.getOccupation() ?
                ContextCompat.getDrawable(getContext(), R.drawable.room_status_indicator_occupied) :
                ContextCompat.getDrawable(getContext(), R.drawable.room_status_indicator_free);
        mOccupationImage.setImageDrawable(statusIndicator);

        // Set occupation status text
        final String occupationStatus = room.getOccupation() ?
                getResources().getString(R.string.status_occupied_text) :
                getResources().getString(R.string.status_free_text);
        mOccupation.setText(occupationStatus);

        int tempPoints = 0;
        // determine temperature points
        if (roomTemp < 19) {
            tempPoints = -2;
        } else if (roomTemp <= 20) {
            tempPoints = -1;
        } else if (roomTemp <= 22) {
            tempPoints = 0;
        } else if (roomTemp <= 24) {
            tempPoints = -1;
        } else {
            tempPoints = -2;
        }

        // determine humidity points
        int humidPoints = 0;
        if (roomHumidity < 25) {
            humidPoints = -2;
        } else if (roomHumidity <= 35) {
            humidPoints = -1;
        } else if (roomHumidity <= 50) {
            humidPoints = 0;
        } else if (roomHumidity <= 70) {
            humidPoints = -1;
        } else {
            humidPoints = -2;
        }

        // determine humidity points
        int co2Points = 0;
        if (roomCo2 < 600) {
            co2Points = 1;
        } else if (roomCo2 <= 700) {
            co2Points = 0;
        } else if (roomCo2 <= 900) {
            co2Points = -1;
        } else if (roomCo2 <= 1200) {
            co2Points = -2;
        } else {
            co2Points = -3;
        }

        int allPoints = tempPoints+humidPoints+co2Points;

        Context appCont = getContext().getApplicationContext();

        // great
        if (allPoints >= 0) {
            mAirQuality.setText(getResources().getString(R.string.room_condition_great));
            mAirQualityImage.setImageDrawable(
                    ContextCompat.getDrawable(appCont, R.drawable.ic_air_0_great_black_36dp));
        } else if (allPoints >= -1) { // good
            mAirQuality.setText(getResources().getString(R.string.room_condition_good));
            mAirQualityImage.setImageDrawable(
                    ContextCompat.getDrawable(appCont, R.drawable.ic_air_1_good_black_36dp));
        } else if (allPoints >= -2) { // ok
            mAirQuality.setText(getResources().getString(R.string.room_condition_ok));
            mAirQualityImage.setImageDrawable(
                    ContextCompat.getDrawable(appCont, R.drawable.ic_air_2_ok_black_36dp));
        } else if (allPoints >= -3) { // bad
            mAirQuality.setText(getResources().getString(R.string.room_condition_bad));
            mAirQualityImage.setImageDrawable(
                    ContextCompat.getDrawable(appCont, R.drawable.ic_air_3_bad_black_36dp));
        } else { // poor
            mAirQuality.setText(getResources().getString(R.string.room_condition_poor));
            mAirQualityImage.setImageDrawable(
                    ContextCompat.getDrawable(appCont, R.drawable.ic_air_4_poor_black_36dp));
        }


        // calculate correct bars
        int[] tempBounds = { 18, 19, 22, 25 };
        int[] humidityBounds = { 20, 30, 50, 70 };
        int[] co2Bounds = { 300, 500, 800, 1200 };

        int tempMainBar = 0;
        int humidMainbar = 0;
        int co2MainBar = 0;

        for (int idx = 0; idx < tempBounds.length; idx++) {
            if (roomTemp > tempBounds[idx]) {
                tempMainBar++;
            }
        }

        for (int idx = 0; idx < humidityBounds.length; idx++) {
            if (roomHumidity > humidityBounds[idx]) {
                humidMainbar++;
            }
        }

        for (int idx = 0; idx < co2Bounds.length; idx++) {
            if (roomCo2 > co2Bounds[idx]) {
                co2MainBar++;
            }
        }

        // get different drawables
        final Drawable onDr = ContextCompat
                .getDrawable(appCont, R.drawable.scale_dash_on);
        final Drawable mainDr = ContextCompat
                .getDrawable(appCont, R.drawable.scale_dash_main);

        ImageView[] tempBars = { mTemperature_bar_1, mTemperature_bar_2, mTemperature_bar_3, mTemperature_bar_4, mTemperature_bar_5 };
        ImageView[] humidBars = { mHumidity_bar_1, mHumidity_bar_2, mHumidity_bar_3, mHumidity_bar_4, mHumidity_bar_5 };
        ImageView[] co2Bars = { mCo2_bar_1, mCo2_bar_2, mCo2_bar_3, mCo2_bar_4, mCo2_bar_5 };

        // set correct bar styles
        int i = 0;
        for (; i < tempMainBar; i++) {
            tempBars[i].setImageDrawable(onDr);
        }
        tempBars[i].setImageDrawable(mainDr);

        i = 0;
        for (; i < humidMainbar; i++) {
            humidBars[i].setImageDrawable(onDr);
        }
        humidBars[i].setImageDrawable(mainDr);

        i = 0;
        for (; i < co2MainBar; i++) {
            co2Bars[i].setImageDrawable(onDr);
        }
        co2Bars[i].setImageDrawable(mainDr);
    }

    private boolean shouldFetchNewData() {
        Activity activity = getActivity();
        if (activity != null) {
            Context context = activity.getApplicationContext();
            if (context != null) {
                BaseApplication app = (BaseApplication) context;
                long elapsedMs = android.os.SystemClock.elapsedRealtime()
                        - app.getLastFetchedDataMainActivity();
                Log.d(TAG, Long.toString(elapsedMs));
                return (elapsedMs > Constants.MAXDATAGETINTERVAL * 1000);
            }
        }
        return true;
    }
}