package com.fc.mis.ngo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.fragments.CasesFragment;
import com.fc.mis.ngo.fragments.EventsFragment;
import com.fc.mis.ngo.models.Ngo;

public class NgoProfileExtentedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(R.id.main_fragment_container);
        frameLayout.setLayoutParams(layoutParams);


        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);

        Toolbar toolbar = new Toolbar(this);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setElevation(3);
        toolbar.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(toolbar);
        linearLayout.addView(frameLayout);

        setContentView(linearLayout);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        // Intent
        Intent intent = getIntent();

        if (!intent.hasExtra("ViewType"))
            finish();

        String viewType = intent.getStringExtra("ViewType");
        String ngoName = intent.getStringExtra("NgoName");
        String ngoId = intent.getStringExtra("NgoId");


        getSupportActionBar().setTitle(ngoName);

        if (viewType.equals("Cases")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new CasesFragment(ngoId)).commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, new EventsFragment(ngoId)).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
