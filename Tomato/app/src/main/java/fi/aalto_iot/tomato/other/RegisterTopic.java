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

/**
 * This class used to subscribe and unsubscribe to topics.
 */
public class RegisterTopic {

    private final Context mContext;

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
                    Log.d("TYUIO",  "Registered to " + topic);
                } catch (IOException | IllegalArgumentException e) {
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
                    Log.d("TYUIO",  "Unregistered to " + topic);

                } catch (IOException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}