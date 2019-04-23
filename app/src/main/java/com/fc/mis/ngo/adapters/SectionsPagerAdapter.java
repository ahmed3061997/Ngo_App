package com.fc.mis.ngo.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fc.mis.ngo.fragments.CasesFragment;
import com.fc.mis.ngo.fragments.EventsFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                CasesFragment casesFragment = new CasesFragment();
                return casesFragment;
            case 1:
                EventsFragment eventsFragment = new EventsFragment();
                return eventsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // We have 2 tabs .. cases and events
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Cases";
            case 1:
                return "Events";
            default:
                return null;
        }
    }
}
