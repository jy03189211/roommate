package fi.aalto_iot.tomato.services;

/* From google samples, TODO: do own*/

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
    OkHttpClient client = new OkHttpClient();

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "Onhandleintent called");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            String device_id = sharedPreferences.getString("android_id", "None");

            sendRegistrationToServer(token, device_id);


            // Subscribe to topic channels
            // done only when user clicks "Follow"
            //subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit()
                    .putBoolean(Preferences.SENT_TOKEN_TO_SERVER, true)
                    .putString("gcm_token", token)
                    .apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(Preferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Preferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String device_id) {
        Log.d(TAG, device_id);
        JSONObject sendObj = new JSONObject();
        try {
            sendObj.put("dev_id", device_id);
            sendObj.put("reg_id", token);
            sendObj.put("name", "test device");
        } catch (Exception e) {

        }

        Log.d(TAG, "Sent json: " + sendObj.toString());

        Request req = new Request.Builder()
                .url(this.getResources().getString(R.string.gcm_registration_url))
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), sendObj.toString()))
                .build();
        try {
            Response resp = client.newCall(req).execute();
            final String jsonString = resp.body().string();
            Log.d(TAG, "Received message: " + jsonString);
        } catch (Exception e) {
            Log.d(TAG, "Could not register for notifications");
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        /*for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }*/
        pubSub.subscribe(token, "/topics/global", null);
    }
    // [END subscribe_topics]

}