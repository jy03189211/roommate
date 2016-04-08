package fi.aalto_iot.tomato.activity.room;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fi.aalto_iot.tomato.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Room_history_fragment extends Fragment {


    public Room_history_fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_history_fragment, container, false);
    }

}
