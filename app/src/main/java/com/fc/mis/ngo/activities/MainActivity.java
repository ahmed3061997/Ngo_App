package com.fc.mis.ngo.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.fc.mis.ngo.R;
import com.fc.mis.ngo.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {
    // toolbar
    private Toolbar mToolbar;
    // bottom navigation bar
    private BottomNavigationView mBottomNavigationView;

    // bottom navigation bar listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.nav_ngo:
                    //selectedFragment = new NGOsFragment();
                    break;

                case R.id.nav_chat:
                    //selectedFragment = new ChatFragment();
                    break;

                case R.id.nav_my_account:
                    //selectedFragment = new MyAccountFragment();
                    break;

                case R.id.nav_more:
                    //selectedFragment = new MoreFragment();
                    break;
            }

            if (selectedFragment != null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_app_bar);

        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle( "Home" );
        //getSupportActionBar().setLogo(R.drawable.charitable_small_logo2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // bottom navigation bar
        mBottomNavigationView = findViewById(R.id.main_bottom_nav_bar);
        mBottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new HomeFragment()).commit();

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }
}
