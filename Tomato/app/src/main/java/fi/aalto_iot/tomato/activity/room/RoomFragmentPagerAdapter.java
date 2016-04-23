package fi.aalto_iot.tomato.activity.room;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoomFragmentPagerAdapter extends FragmentPagerAdapter {
    // helper class
    private class FragmentPair {
        private String name;
        private Fragment fragment;

        public FragmentPair(String name, Fragment fragment) {
            this.name = name;
            this.fragment = fragment;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public void setFragment(Fragment fragment) {
            this.fragment = fragment;
        }
    }

    private final List<FragmentPair> mFragmentList = new ArrayList<FragmentPair>();

    public RoomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(String title, Fragment fragment) {
        mFragmentList.add(new FragmentPair(title, fragment));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentList.get(position).getName();
    }
}
