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
    private SwipeRefreshLayout swipeContainer;
    OkHttpClient client = new OkHttpClient();
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
        View view = inflater.inflate(R.layout.fragment_room_default_fragment, container, false);
        //mTemperature = (TextView) view.findViewById(R.id.room_temperature);
        //mCO2 = (TextView) view.findViewById(R.id.room_co2);
        //mHumidity = (TextView) view.findViewById(R.id.room_humidity);
        mOccupationImage = (ImageView) view.findViewById(R.id.room_status_indicator_circle);
        mOccupation = (TextView) view.findViewById(R.id.room_status_indicator_text);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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

    private void fetchRoom(final String roomUrl) {
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
                                swipeContainer.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }

    private void updateContent() {
        Realm realm = Realm.getDefaultInstance();
        room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();

        final String temperature = String.format(getResources().getString(R.string.room_temperature_value), room.getTemperature() < 0 ? '-' : '+', room.getTemperature());
        //mTemperature.setText(temperature);
        final String co2 = String.format(getResources().getString(R.string.room_co2_value), room.getCo2());
        //mCO2.setText(co2);
        final String humidity = String.format(getResources().getString(R.string.room_humidity_value), room.getHumidity());
        //mHumidity.setText(humidity);

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