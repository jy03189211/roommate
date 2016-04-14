package fi.aalto_iot.tomato.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fi.aalto_iot.tomato.R;
import fi.aalto_iot.tomato.activity.room.RoomFragmentPagerAdapter;
import fi.aalto_iot.tomato.activity.room.Room_default_fragment;
import fi.aalto_iot.tomato.activity.room.Room_history_fragment;
import fi.aalto_iot.tomato.db.data.RoomModel;
import io.realm.Realm;

public class RoomActivity extends AppCompatActivity {

    ImageView roomTitleImage;

    public int statusBarHeight() {
        int res = 0;
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0)
            res = getResources().getDimensionPixelSize(resId);
        return res;
    }

    // React to back button press as we have only one item we don't need additional checks
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        //get passed variables
        final Bundle bundle = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding(0, statusBarHeight(), 0, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Realm realm = Realm.getDefaultInstance();
        RoomModel room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();
        getSupportActionBar().setTitle(room.getRoomName());

        roomTitleImage = (ImageView) findViewById(R.id.room_title_image);

        // Use viewpager and tablayout to setup tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        RoomFragmentPagerAdapter adapter = new RoomFragmentPagerAdapter(getSupportFragmentManager());

        // pass bundle forward to fragments
        final Room_default_fragment room_default_fragment = new Room_default_fragment();
        room_default_fragment.setArguments(bundle);
        final Room_history_fragment room_history_fragment = new Room_history_fragment();
        room_history_fragment.setArguments(bundle);

        adapter.addFragment(getResources().getString(R.string.room_fragment_default_name), room_default_fragment);
        adapter.addFragment(getResources().getString(R.string.room_fragment_history_name), room_history_fragment);
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
