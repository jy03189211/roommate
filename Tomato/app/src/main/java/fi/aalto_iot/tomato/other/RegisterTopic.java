package fi.aalto_iot.tomato.other;

import android.content.Context;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import com.google.android.gms.iid.InstanceID;


import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.main.RoomAdapter;
import fi.aalto_iot.tomato.db.data.RoomModel;
import io.realm.Realm;

// Subscribe or unsubscribe to some topic, to start/stop receiving notifications
public class RegisterTopic {

    private final Context mContext;
    private final RoomAdapter mAdapter;
    private String TAG = "RegisterTopic";

    public RegisterTopic(Context context, RoomAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }

    public void subscribeTopic(final String topic, final int room_id) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean ret;
                try {
                    InstanceID instanceID = InstanceID.getInstance(mContext);
                    String token = instanceID.getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    GcmPubSub.getInstance(mContext).subscribe(token, topic, null);
                    Log.d(TAG,  "Registered to " + topic);
                    ret = true;
                } catch (IOException | IllegalArgumentException e) {
                    Log.d(TAG, "Could not register to topic: " + topic);
                    e.printStackTrace();
                    ret = false;
                }
                return ret;
            }

            @Override
            protected void onPostExecute(Boolean retval) {
                super.onPostExecute(retval);
                // failed subscribe
                if (!retval) {
                    Realm realm = Realm.getDefaultInstance();
                    RoomModel room = realm.where(RoomModel.class).equalTo("id", room_id).findFirst();
                    realm.beginTransaction();
                    room.setFollowed(false);
                    realm.commitTransaction();
                    realm.close();

                    final String text = "Could not follow room";
                    Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                    toast.show();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    public void unsubscribeTopic(final String topic, final int room_id) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean ret;
                try {
                    InstanceID instanceID = InstanceID.getInstance(mContext);
                    String token = instanceID.getToken(mContext.getResources().getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    GcmPubSub.getInstance(mContext).unsubscribe(token, topic);
                    Log.d(TAG,  "Unregistered to " + topic);
                    ret = true;

                } catch (IOException | IllegalArgumentException e) {
                    Log.d(TAG, "Could not unregister to topic: " + topic);
                    e.printStackTrace();
                    ret = false;
                }
                return ret;
            }

            @Override
            protected void onPostExecute(Boolean retval) {
                super.onPostExecute(retval);
                // failed unsubscribe
                if (!retval) {
                    Realm realm = Realm.getDefaultInstance();
                    RoomModel room = realm.where(RoomModel.class).equalTo("id", room_id).findFirst();
                    realm.beginTransaction();
                    room.setFollowed(true);
                    realm.commitTransaction();
                    realm.close();

                    final String text = "Could not unfollow room";
                    Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
                    toast.show();
                    mAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

}