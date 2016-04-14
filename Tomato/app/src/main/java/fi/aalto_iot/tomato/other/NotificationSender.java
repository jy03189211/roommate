package fi.aalto_iot.tomato.other;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.RoomActivity;

public class NotificationSender {
    static String TAG = "NotficationSender";

    public static void parseAndShowNotification(Bundle data, Context cont) {
        try {
            String notification_type = data.getString("notification_type");
            //JSONObject json = new JSONObject(json_s);
            int room_id = Integer.parseInt(data.getString("room_id"));
            //String notification_type = json.getString("notification_type");
            if (notification_type.equals("air_quality")) {
                sendNotification(cont.getResources().getString(R.string.air_quality_bad_notification), cont, room_id);
            } else if (notification_type.equals("room_availability")) {
                sendNotification(cont.getResources().getString(R.string.room_available_notification), cont, room_id);
            }

        } catch (Exception e) {
            Log.d(TAG, "Parsing notification failed");
        }

    }

    private static void sendNotification(String msg, Context cont, int room_id) {
        final Intent roomIntent = new Intent(cont, RoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", room_id);
        roomIntent.putExtras(bundle);

        PendingIntent contentIntent = PendingIntent.getActivity(cont,
                0, roomIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(cont);
        mBuilder.setContentTitle("Room Mate");
        mBuilder.setContentText(msg);
        mBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_disabled);
        mBuilder.setContentIntent(contentIntent);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        Notification notification = mBuilder.build();

        NotificationManager mNotificationManager = (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(9999, notification);
    }
}
