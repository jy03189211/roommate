package fi.aalto_iot.tomato.activity.main;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.RoomActivity;
import fi.aalto_iot.tomato.db.data.RoomModel;
import fi.aalto_iot.tomato.other.Constants;
import fi.aalto_iot.tomato.other.RegisterTopic;
import fi.aalto_iot.tomato.services.RegistrationIntentService;
import io.realm.Realm;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<RoomModel> roomList = new ArrayList<>();
    private String myTag = "RoomAdapter";
    private SharedPreferences preferences;
    private Realm realm;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView mCardView;
        public TextView mRoomTitleView;
        public TextView mOccupationView;
        public TextView mRoomConditionView;
        public ImageView mImageView;
        public ImageView mStatusIndicatorView;
        public Button mFollowButton;
        public Button mDetailsButton;
        private Context cont;
        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView)v.findViewById(R.id.card_view);
            mRoomTitleView = (TextView)v.findViewById(R.id.room_title);
            mOccupationView = (TextView)v.findViewById(R.id.status_indicator_text);
            mRoomConditionView = (TextView)v.findViewById(R.id.room_condition_text);
            mImageView = (ImageView)v.findViewById(R.id.cardImage);
            mStatusIndicatorView = (ImageView)v.findViewById(R.id.status_indicator_circle);
            mFollowButton = (Button)v.findViewById(R.id.follow_button);
            mDetailsButton = (Button)v.findViewById(R.id.details_button);
            cont = v.getContext();
            realm = Realm.getDefaultInstance();

            preferences = PreferenceManager.getDefaultSharedPreferences(cont);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("adapterposition", Integer.toString(getAdapterPosition()));
                    final Intent roomIntent = new Intent(cont, RoomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", roomList.get(getAdapterPosition()).getRoomName());
                    bundle.putInt("id", roomList.get(getAdapterPosition()).getId());
                    roomIntent.putExtras(bundle);
                    cont.startActivity(roomIntent);
                }
            });
            mDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("adapterposition", Integer.toString(getAdapterPosition()));
                    final Intent roomIntent = new Intent(cont, RoomActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", roomList.get(getAdapterPosition()).getRoomName());
                    bundle.putInt("id", roomList.get(getAdapterPosition()).getId());
                    roomIntent.putExtras(bundle);
                    cont.startActivity(roomIntent);
                }
            });

            // test notification
            mFollowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomModel room = roomList.get(getAdapterPosition());

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(cont);
                    mBuilder.setContentTitle("Room Mate");

                    int room_id = room.getId();
                    if (!room.isFollowed()) {
                        new RegisterTopic(cont).subscribeTopic("/topics/" + Integer.toString(room_id));
                        new RegisterTopic(cont).subscribeTopic("/topics/" + Integer.toString(room_id) + "-quality");
                        realm.beginTransaction();
                        room.setFollowed(true);
                        realm.copyToRealmOrUpdate(room);
                        realm.commitTransaction();
                        mBuilder.setContentText("Subscribed to room " + room.getRoomName());
                        notifyDataSetChanged();
                    }
                    else {
                        new RegisterTopic(cont).unsubscribeTopic("/topics/" + Integer.toString(room_id));
                        new RegisterTopic(cont).unsubscribeTopic("/topics/" + Integer.toString(room_id) + "-quality");
                        realm.beginTransaction();
                        room.setFollowed(false);
                        realm.copyToRealmOrUpdate(room);
                        realm.commitTransaction();
                        mBuilder.setContentText("Unsubscribed to room " + room.getRoomName());
                        notifyDataSetChanged();
                    }
                    mBuilder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_disabled);
                    //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    //mBuilder.setSound(alarmSound);
                    Notification notification = mBuilder.build();

                    NotificationManager mNotificationManager =
                            (NotificationManager) cont.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(9999, notification);

                }

            });
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Actvity 1", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_card_main, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RoomModel room = roomList.get(position);
        Resources res = holder.cont.getResources();

        // Set room name as card title:
        final String roomName = room.getRoomName();
        holder.mRoomTitleView.setText(
                String.format(
                        res.getString(R.string.room_header), roomName));

        // Set room picture to card header
        Picasso.with(holder.cont)
                .load("http://i.imgur.com/We6zFAz.jpg")
                .fit().centerCrop()
                .into(holder.mImageView);

        final boolean isOccupied = room.getOccupation();

        // Set correct status indicator ("traffic light") color
        final Drawable statusIndicator = isOccupied ?
                ContextCompat.getDrawable(holder.cont, R.drawable.room_status_indicator_occupied) :
                ContextCompat.getDrawable(holder.cont, R.drawable.room_status_indicator_free);
        holder.mStatusIndicatorView.setImageDrawable(statusIndicator);

        // Set occupation status text
        final String occupationStatus = isOccupied ?
                res.getString(R.string.status_occupied_text) :
                res.getString(R.string.status_free_text);
        holder.mOccupationView.setText(occupationStatus);

        // Set room condition text
        final String co2_status;
        final int co2_level = room.getCo2();
        if (co2_level < Constants.CO2_FRESH)
            co2_status = res.getString(R.string.room_co2_fresh);
        else if (co2_level > Constants.CO2_DROWSY)
            co2_status = res.getString(R.string.room_co2_drowsy);
        else
            co2_status = res.getString(R.string.room_co2_ok);

        final String room_condition = String.format(res.getString(R.string.room_condition_text), room.getTemperature(), co2_status);
        holder.mRoomConditionView.setText(room_condition);

        // Set follow button text
        final String following = room.isFollowed() ?
                res.getString(R.string.unfollow_text) :
                res.getString(R.string.follow_text);
        holder.mFollowButton.setText(following);

    }

    public void add(RoomModel job) {
        roomList.add(job);
        //Log.d(myTag, "added room to adapter");
    }

    public void clear() {
        roomList.clear();
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
