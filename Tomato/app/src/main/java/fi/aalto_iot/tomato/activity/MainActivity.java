package fi.aalto_iot.tomato.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fi.aalto_iot.tomato.BaseApplication;
import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.main.RoomAdapter;
import fi.aalto_iot.tomato.db.data.RoomModel;
import fi.aalto_iot.tomato.other.Constants;
import fi.aalto_iot.tomato.services.Preferences;
import fi.aalto_iot.tomato.services.RegistrationIntentService;
import io.realm.Realm;
import io.realm.RealmChangeListener;
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
    private RealmChangeListener changeListener;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    private SwipeRefreshLayout swipeContainer;

    private String TAG = "mainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        sharedPreferences
                .edit()
                .putString("android_id", Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID))
                .apply();

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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = sharedPreferences
                        .getBoolean(Preferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.d(TAG, "Registration complete?");
                } else {
                    Log.d(TAG, "Registration error?");
                }
            }
        };
        /*
        changeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                mAdapter.notifyDataSetChanged();
            }
        };
        */


        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (shouldFetchNewData()) {
            fetchRooms();
        }
        else {
            updateContent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
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
                copyJsonToRealm(json);
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
        //Log.d(TAG, Long.toString(elapsedMs));
        return (elapsedMs > Constants.MAXDATAGETINTERVAL*1000);

    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Preferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private boolean copyJsonToRealm(JSONArray json) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        try {
            Set<Integer> new_ids = new HashSet<>();

            for (int i = 0; i < json.length(); i++) {
                JSONObject room = (JSONObject) json.get(i);
                if (room != null) {
                    RoomModel thisRoom = new RoomModel();
                    int id = room.getInt("id");
                    thisRoom.setId(id);
                    new_ids.add(id);

                    thisRoom.setRoomName(room.getString("name"));
                    thisRoom.setUrl(room.getString("url"));
                    thisRoom.setOccupation(!room.getBoolean("available"));
                    thisRoom.setOrganization(room.getString("organization"));
                    thisRoom.setLocation(room.getString("location"));
                    thisRoom.setSize(room.getInt("size"));
                    thisRoom.setCo2(room.getInt("co2"));
                    thisRoom.setTemperature(room.getInt("temperature"));
                    thisRoom.setHumidity(room.getInt("humidity"));

                    int isAlreadyInRealm = realm.where(RoomModel.class)
                            .equalTo("id", id).findAll().size();

                    if (isAlreadyInRealm == 0) {
                        //Log.d(TAG, room.getString("name") + " is not in realm already");
                        thisRoom.setFollowed(false);
                    } else {
                        thisRoom.setFollowed(realm.where(RoomModel.class)
                                .equalTo("id", id).findAll().first().isFollowed());
                        //Log.d(TAG, room.get("name") + "is already in realm " + Boolean.toString(realm.where(RoomModel.class)
                         //       .equalTo("id", id).findAll().first().isFollowed()));
                    }

                    realm.copyToRealmOrUpdate(thisRoom);
                }

            }

            Set<Integer> old_ids = new HashSet<>();

            RealmResults<RoomModel> results = realm.where(RoomModel.class).findAll();
            for (int i = 0; i < results.size(); i++) {
                RoomModel r = results.get(i);
                int r_id = r.getId();
                if (!new_ids.contains(r_id)) {
                    old_ids.add(r_id);
                }
            }

            for (int o_id : old_ids) {
                realm.where(RoomModel.class)
                        .equalTo("id", o_id)
                        .findAll()
                        .clear();
            }

            realm.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
        }
        realm.close();

        BaseApplication app = (BaseApplication)getApplicationContext();
        app.setLastFetchedDataMainActivity(android.os.SystemClock.elapsedRealtime());

        return true;
    }
}
