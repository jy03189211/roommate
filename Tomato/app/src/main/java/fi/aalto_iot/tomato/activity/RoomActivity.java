package fi.aalto_iot.tomato.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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
    SwipeRefreshLayout mRefresh;

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
        final RoomModel room = realm.where(RoomModel.class).equalTo("id", bundle.getInt("id")).findFirst();
        getSupportActionBar().setTitle(room.getRoomName());

        roomTitleImage = (ImageView) findViewById(R.id.room_title_image);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeContainerRoom) ;

        // Use viewpager and tablayout to setup tabs
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        RoomFragmentPagerAdapter adapter = new RoomFragmentPagerAdapter(getSupportFragmentManager());

        // pass bundle forward to fragments
        final Room_default_fragment room_default_fragment = new Room_default_fragment();
        room_default_fragment.setArguments(bundle);
        final Room_history_fragment room_day_history_fragment = new Room_history_fragment();
        Bundle bundle_1_day = new Bundle(bundle);
        bundle_1_day.putInt("days", 1);
        room_day_history_fragment.setArguments(bundle_1_day);
        final Room_history_fragment room_week_history_fragment = new Room_history_fragment();
        Bundle bundle_7_days = new Bundle(bundle);
        bundle_7_days.putInt("days", 7);
        room_week_history_fragment.setArguments(bundle_7_days);
        final Room_history_fragment room_month_history_fragment = new Room_history_fragment();
        Bundle bundle_30_days = new Bundle(bundle);
        bundle_30_days.putInt("days", 30);
        room_month_history_fragment.setArguments(bundle_30_days);

        adapter.addFragment(getResources().getString(R.string.room_fragment_default_name), room_default_fragment);
        adapter.addFragment(getResources().getString(R.string.room_fragment_day_history_name), room_day_history_fragment);
        adapter.addFragment(getResources().getString(R.string.room_fragment_week_history_name), room_week_history_fragment);
        adapter.addFragment(getResources().getString(R.string.room_fragment_month_history_name), room_month_history_fragment);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                mRefresh.setEnabled( state == ViewPager.SCROLL_STATE_IDLE );

            }
        } );

        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                room_default_fragment.fetchRoom(room.getUrl(), mRefresh);
            }
        });

        // Set room picture to card header
        Picasso.with(this.getBaseContext())
                .load(room.getPicture())
                .fit().centerCrop()
                .into(roomTitleImage);

    }


}
