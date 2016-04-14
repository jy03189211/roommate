package fi.aalto_iot.tomato.activity.room;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fi.aalto_iot.tomato.BaseApplication;
import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.room.view.RoomHistoryCanvasView;
import fi.aalto_iot.tomato.db.data.RoomModel;
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
    private RoomHistoryCanvasView canvasView;
    private RoomModel room;
    OkHttpClient client = new OkHttpClient();
    List<Integer> sensor_data = sensor_data = new ArrayList<Integer>();

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
        canvasView = (RoomHistoryCanvasView) view.findViewById(R.id.canvasView);

        int[] array = new int[100];
        Random r = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = r.nextInt(255);
        }

        fetchSensor("co2");

        return view;
    }

    private void fetchSensor(final String sensor) {

        Request req = new Request.Builder()
                .url(room.getUrl() + sensor + "/")
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                // TODO: failure handling
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //swipeContainer.setRefreshing(false);
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
                    sensor_data.clear();
                    try {
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject sensor_object = (JSONObject) json.get(i);
                            sensor_data.add(sensor_object.getInt("concentration"));
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
                                updateContent();
                                //swipeContainer.setRefreshing(false);
                            }
                        });
                    }
                }
            }
        });
    }

    private void updateContent() {
        canvasView.setData(sensor_data);
    }
}
