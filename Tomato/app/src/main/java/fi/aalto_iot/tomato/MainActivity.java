package fi.aalto_iot.tomato;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fi.aalto_iot.tomato.db.models.RoomModel;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
/*
        // Just for testing, remove for actual app
        ArrayList<String> myDataset = new ArrayList<String>();

        for (int i = 0; i < 10000; i++) {
            myDataset.add("Number " + Integer.toString(i));
        }
        ////////////////
*/
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mAdapter = new RoomAdapter(myDataset.toArray(new String[myDataset.size()]));
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
        // TODO: proper url for rooms
        Request req = new Request.Builder()
                .url("http://tomato-1230.herokuapp.com/api/v1/motion/")
                .build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, java.io.IOException e) {
                // TODO: failure handling
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
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
                    RoomModel tomato = new RoomModel();
                    if (json.length() > 0) {
                        tomato.setOccupation(true);
                    }
                    else {
                        tomato.setOccupation(false);
                    }
                    tomato.setRoomName("Tomato");
                    realm.copyToRealmOrUpdate(tomato);
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
