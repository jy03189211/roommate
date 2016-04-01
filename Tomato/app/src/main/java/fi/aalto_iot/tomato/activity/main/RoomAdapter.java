package fi.aalto_iot.tomato.activity.main;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.RoomActivity;
import fi.aalto_iot.tomato.db.data.RoomModel;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    // TODO: Create nice class for rooms (or use Realm or something similar)
    private List<RoomModel> roomList = new ArrayList<>();
    private String myTag = "RoomAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView mCardView;
        public TextView mTextView;
        private Context cont;
        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView)v.findViewById(R.id.card_view);
            mTextView = (TextView)v.findViewById(R.id.room_title);
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

    // Currently RoomAdapter gets strings as input data; TODO: use a room class or some Realm stuff
    /*public RoomAdapter(String[] myDataset) {
        mDataset = myDataset;
    }
    */

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

        final String roomName = room.getRoomName();
        final String occupationName = room.getOccupation() ? "Occupied" : "Free";

        //holder.mTextView.setText(roomName + ": " + occupationName);
        holder.mTextView.setText(
                String.format(
                        res.getString(R.string.room_availability_header), roomName, occupationName
                )
        );

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
