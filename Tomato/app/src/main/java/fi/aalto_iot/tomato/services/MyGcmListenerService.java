package fi.aalto_iot.tomato.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.other.NotificationSender;


public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {

        // Send a proper User notification, not a generic one
        NotificationSender.parseAndShowNotification(data, this);
    }

    @Override
    public void onDeletedMessages() {
        sendNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        sendNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        sendNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    // Send generic notification when GCM message got
    private void sendNotification(String msg) {
        Log.d("NOTIFICATION", msg);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Room Mate");
        mBuilder.setContentText(msg);
        mBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_disabled);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        Notification notification = mBuilder.build();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(9999, notification);
    }
}