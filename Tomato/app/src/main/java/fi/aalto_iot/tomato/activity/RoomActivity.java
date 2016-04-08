package fi.aalto_iot.tomato.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.room.RoomFragmentPagerAdapter;
import fi.aalto_iot.tomato.activity.room.Room_default_fragment;
import fi.aalto_iot.tomato.activity.room.Room_history_fragment;

public class RoomActivity extends AppCompatActivity {

    ImageView roomTitleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        roomTitleImage = (ImageView) findViewById(R.id.room_title_image);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        RoomFragmentPagerAdapter adapter = new RoomFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(getResources().getString(R.string.room_fragment_default_name), new Room_default_fragment());
        adapter.addFragment(getResources().getString(R.string.room_fragment_history_name), new Room_history_fragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Set room picture to card header
        Picasso.with(this.getBaseContext())
                .load("http://i.imgur.com/We6zFAz.jpg")
                .fit().centerCrop()
                .into(roomTitleImage);

    }


}
