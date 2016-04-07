package fi.aalto_iot.tomato.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fi.aalto_iot.tomato.BaseApplication;
import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.main.RoomAdapter;
import fi.aalto_iot.tomato.db.data.RoomModel;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.internal.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RoomAdapter mAdapter;
    OkHttpClient client = new OkHttpClient();
    private Realm realm;

    private SwipeRefreshLayout swipeContainer;

    private String myTag = "mainActivity";
    private static long maxDataGetInterval = 120; // seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RoomAdapter();
        mRecyclerView.setAdapter(mAdapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchRooms();
            }
        });
        //Log.d(myTag, "oncreate called");
        if (shouldFetchNewData()) {
            fetchRooms();
        }
        else {
            updateContent();
        }
    }

    private void updateContent() {
        mAdapter.clear();
        RealmResults<RoomModel> realmResults = realm.where(RoomModel.class).findAll();
        for (RoomModel job : realmResults) {
            mAdapter.add(job);
        }
        mAdapter.notifyDataSetChanged();
    }

    // not used
    private boolean getOccupation(String url) {
        Request req = new Request.Builder()
                .url(url + "motion")
                .build();
        try {
            Response resp = client.newCall(req).execute();
            final String jsonString = resp.body().string();
            JSONArray json = new JSONArray(jsonString);
            JSONObject measurement = (JSONObject) json.get(0);

            return measurement.getBoolean("detected");
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void fetchRooms() throws IOException {
        Request req = new Request.Builder()
                .url(this.getResources().getString(R.string.rooms_url))
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                // TODO: failure handling
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                        Context context = getApplicationContext();
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
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO: remove deleted rooms locally too
                if (json != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    try {
                        //ArrayList<RoomModel> newRooms = {};
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject room = (JSONObject) json.get(i);
                            if (room != null) {
                                RoomModel thisRoom = new RoomModel();
                                thisRoom.setRoomName(room.getString("name"));
                                //thisRoom.setOccupation(getOccupation(room.getString("url"))); // for now TODO: proper occupation
                                thisRoom.setOccupation(!room.getBoolean("available"));
                                thisRoom.setOrganization(room.getString("organization"));
                                thisRoom.setLocation(room.getString("location"));
                                thisRoom.setSize(room.getInt("size"));
                                thisRoom.setCo2(room.getInt("co2"));
                                thisRoom.setTemperature(room.getInt("temperature"));
                                thisRoom.setHumidity(room.getInt("humidity"));

                                //newRooms.add(thisRoom);
                                realm.copyToRealmOrUpdate(thisRoom);

                            }
                        }

                        RealmResults<RoomModel> oldRooms = realm.where(RoomModel.class).findAll();
/*
                        for (RoomModel m: oldRooms) {
                            if (newRooms.contains(m))
                        }
*/
                        realm.commitTransaction();

                    } catch (Exception e) {
                        e.printStackTrace();
                        realm.cancelTransaction();
                    }
                    realm.close();

                    BaseApplication app = (BaseApplication)getApplicationContext();
                    app.setLastFetchedDataMainActivity(android.os.SystemClock.elapsedRealtime());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateContent();
                        swipeContainer.setRefreshing(false);
                    }
                });
            }
        });
    }

    private boolean shouldFetchNewData() {
        BaseApplication app = (BaseApplication)getApplicationContext();
        long elapsedMs = android.os.SystemClock.elapsedRealtime()
                - app.getLastFetchedDataMainActivity();
        Log.d(myTag, Long.toString(elapsedMs));
        return (elapsedMs > maxDataGetInterval*1000);

    }
}
