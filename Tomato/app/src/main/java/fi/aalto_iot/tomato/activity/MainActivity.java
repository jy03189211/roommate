package fi.aalto_iot.tomato.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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
        fetchRooms();
    }

    private void updateContent() {
        mAdapter.clear();
        RealmResults<RoomModel> realmResults = realm.where(RoomModel.class).findAll();
        for (RoomModel job : realmResults) {
            mAdapter.add(job);
        }
        mAdapter.notifyDataSetChanged();
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

                if (json != null) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    try {
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject room = (JSONObject) json.get(i);
                            if (room != null) {
                                RoomModel thisRoom = new RoomModel();
                                thisRoom.setRoomName(room.getString("name"));
                                thisRoom.setOccupation(false); // for now TODO: proper occupation
                                thisRoom.setOrganization(room.getString("organization"));
                                thisRoom.setLocation(room.getString("location"));
                                thisRoom.setSize(room.getInt("size"));

                                realm.copyToRealmOrUpdate(thisRoom);

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    realm.commitTransaction();
                    realm.close();
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
}
