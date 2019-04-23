package com.fc.mis.ngo.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.activities.MainActivity;
import com.fc.mis.ngo.adapters.SectionsPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // view pager & sections pager adapter
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    // tab layout
    private TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // view pager
        mViewPager = (ViewPager) view.findViewById(R.id.home_tab_pager);

        // view pager adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // tab layout
        mTabLayout = (TabLayout) view.findViewById(R.id.home_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Home");
    }
}
