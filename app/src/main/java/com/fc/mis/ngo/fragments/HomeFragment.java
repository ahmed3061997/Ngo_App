package com.fc.mis.ngo.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fc.mis.ngo.activities.CaseActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.activities.MainActivity;
import com.fc.mis.ngo.adapters.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout.ViewPagerOnTabSelectedListener;

public class HomeFragment extends Fragment {
    // view pager & sections pager adapter
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    // tab layout
    private TabLayout mTabLayout;

    private FloatingActionButton mActionBtn;

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

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_heart_hands_icon);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_event);

        mActionBtn = (FloatingActionButton) view.findViewById(R.id.home_action_fab_btn);
        mActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeAction();
            }
        });

        return view;
    }

    private void executeAction() {
        if (mTabLayout.getSelectedTabPosition() == 0) // Cases is selected
        {
            showCase();
        } else { // Events is selected
            showEvent();
        }
    }

    private void showCase() {
        Intent intent = new Intent(getActivity(), CaseActivity.class);
        startActivity(intent);
    }

    private void showEvent() {
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title bar
        ((MainActivity) getActivity()).setActionBarTitle("Home");
        ((MainActivity) getActivity()).setActionBarShadow(false);
    }
}
