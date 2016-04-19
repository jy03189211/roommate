package fi.aalto_iot.tomato.other;

import android.content.Context;
import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import com.google.android.gms.iid.InstanceID;


import fi.aalto_iot.tomato.R;

// Subscribe or unsubscribe to some topic, to start/stop receiving notifications
public class RegisterTopic {

    private final Context mContext;
    private String TAG = "RegisterTopic";

    public RegisterTopic(Context context) {
        mContext = context;
    }

    public void subscribeTopic(final String topic) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(mContext);
                    String token = instanceID.getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    GcmPubSub.getInstance(mContext).subscribe(token, topic, null);
                    Log.d(TAG,  "Registered to " + topic);
                } catch (IOException | IllegalArgumentException e) {
                    Log.d(TAG, "Could not register to topic: " + topic);
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void unsubscribeTopic(final String topic) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InstanceID instanceID = InstanceID.getInstance(mContext);
                    String token = instanceID.getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    GcmPubSub.getInstance(mContext).unsubscribe(token, topic);
                    Log.d(TAG,  "Unregistered to " + topic);

                } catch (IOException | IllegalArgumentException e) {
                    Log.d(TAG, "Could not unregister to topic: " + topic);
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}