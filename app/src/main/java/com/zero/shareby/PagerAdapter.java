package com.zero.shareby;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    public PagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new DashboardFragment();

            case 1:
                return new PendingRequestsFragment();

            case 2:
                return new PostDashboard();

        }
        return new DashboardFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Dashboard";

            case 1:
                return "Requests";

            case 2:
                return "Your Posts";
        }
        return "Default";
    }
}
