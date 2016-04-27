package fi.aalto_iot.tomato.services;

import android.app.IntentService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.provider.Settings.Secure;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import fi.aalto_iot.tomato.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private OkHttpClient client = new OkHttpClient();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        try {
            InstanceID instanceID = InstanceID.getInstance(this.getApplicationContext());
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            String device_id = sharedPreferences.getString("android_id", "None");

            sendRegistrationToServer(token, device_id);
            sharedPreferences.edit()
                    .putBoolean(Preferences.SENT_TOKEN_TO_SERVER, true)
                    .putString("gcm_token", token)
                    .apply();
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Preferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify that registration has completed
        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager
                .getInstance(this.getApplicationContext()).sendBroadcast(registrationComplete);
    }


    // Send registration token to own servers
    private void sendRegistrationToServer(String token, String device_id) {
        Log.d(TAG, device_id);
        JSONObject sendObj = new JSONObject();
        try {
            sendObj.put("dev_id", device_id);
            sendObj.put("reg_id", token);
            sendObj.put("name", android.os.Build.MODEL);
        } catch (Exception e) {
            Log.d(TAG, "couldn't put JSON to JSONObject");
        }

        Request req = new Request.Builder()
                .url(this.getApplicationContext().getResources().getString(R.string.gcm_registration_url))
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendObj.toString()))
                .build();
        try {
            Response resp = client.newCall(req).execute();
            final String jsonString = resp.body().string();
        } catch (Exception e) {
            Log.d(TAG, "Could not register for notifications");
        }
    }

    // Method not used
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this.getApplicationContext());
        pubSub.subscribe(token, "/topics/global", null);
    }

}