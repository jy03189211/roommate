package fi.aalto_iot.tomato.activity.room;


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

import org.json.JSONArray;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_default_fragment, container, false);
        mTemperature = (TextView) view.findViewById(R.id.room_temperature);
        mCO2 = (TextView) view.findViewById(R.id.room_co2);
        mHumidity = (TextView) view.findViewById(R.id.room_humidity);
        mOccupationImage = (ImageView) view.findViewById(R.id.room_status_indicator_circle);
        mOccupation = (TextView) view.findViewById(R.id.room_status_indicator_text);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRoom(room.getRoomName());
            }
        });

        if (shouldFetchNewData()) {
            fetchRoom(room.getRoomName());
        } else {
            updateContent();
        }

        return view;
    }

    private void fetchRoom(final String roomName) {
        Request req = new Request.Builder()
                .url(this.getResources().getString(R.string.rooms_url))
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                // TODO: failure handling
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                        Context context = getActivity().getApplicationContext();
                        CharSequence text = getResources().getString(R.string.failed_to_download);
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
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

                if (json != null) {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject roomJSON = (JSONObject) json.get(i);
                            if (room != null) {
                                if (roomJSON.getString("name").equals(roomName)) {
                                    room = realm.where(RoomModel.class).equalTo("roomName", bundle.getString("name")).findFirst();
                                    realm.beginTransaction();
                                    room.setRoomName(roomJSON.getString("name"));
                                    room.setOccupation(!roomJSON.getBoolean("available"));
                                    room.setOrganization(roomJSON.getString("organization"));
                                    room.setLocation(roomJSON.getString("location"));
                                    room.setSize(roomJSON.getInt("size"));
                                    room.setCo2(roomJSON.getInt("co2"));
                                    room.setTemperature(roomJSON.getInt("temperature"));
                                    room.setHumidity(roomJSON.getInt("humidity"));
                                    realm.commitTransaction();
                                    break;
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        realm.cancelTransaction();
                    }
                    realm.close();

                    BaseApplication app = (BaseApplication) getActivity().getApplicationContext();
                    app.setLastFetchedDataMainActivity(android.os.SystemClock.elapsedRealtime());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateContent();
                            swipeContainer.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private void updateContent() {
        Realm realm = Realm.getDefaultInstance();
        room = realm.where(RoomModel.class).equalTo("roomName", bundle.getString("name")).findFirst();

        mTemperature.setText(Integer.toString(room.getTemperature()));
        mCO2.setText(Integer.toString(room.getCo2()));
        mHumidity.setText(Integer.toString(room.getHumidity()));

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
        BaseApplication app = (BaseApplication) getActivity().getApplicationContext();
        long elapsedMs = android.os.SystemClock.elapsedRealtime()
                - app.getLastFetchedDataMainActivity();
        Log.d(TAG, Long.toString(elapsedMs));
        return (elapsedMs > Constants.MAXDATAGETINTERVAL * 1000);

    }
}
