package fi.aalto_iot.tomato.activity.main;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.RoomActivity;
import fi.aalto_iot.tomato.db.data.RoomModel;
import fi.aalto_iot.tomato.other.Constants;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<RoomModel> roomList = new ArrayList<>();
    private String myTag = "RoomAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView mCardView;
        public TextView mRoomTitleView;
        public TextView mOccupationView;
        public TextView mRoomConditionView;
        public ImageView mImageView;
        public ImageView mStatusIndicatorView;
        public Button mFollowButton;
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
            cont = v.getContext();
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("adapterposition", Integer.toString(getAdapterPosition()));
                    int position = getAdapterPosition();
                    final Intent roomIntent = new Intent(cont, RoomActivity.class);
                    cont.startActivity(roomIntent);
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
        final String following = false ?
                res.getString(R.string.unfollow_text) :
                res.getString(R.string.follow_text);
        holder.mFollowButton.setText(following);

    }

    public void add(RoomModel job) {
        roomList.add(job);
        Log.d(myTag, "added room to adapter");
    }

    public void clear() {
        roomList.clear();
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
