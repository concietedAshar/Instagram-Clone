package com.mrash.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mrash.instagramclone.Fragments.HomeFragment;
import com.mrash.instagramclone.Fragments.NotificationFragment;
import com.mrash.instagramclone.Fragments.ProfileFragment;
import com.mrash.instagramclone.Fragments.SearchFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setBottomNavigationView();
        // by default home fragment will start
        setHomeAfterLogin();
    }

    /**
     * Setup Bottom Navigation View
     *
     * set the fragment on th base of their id's
     */
    private void setBottomNavigationView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        Log.d(TAG, "onNavigationItemSelected: Setting Home Fragment");
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        Log.d(TAG, "onNavigationItemSelected: setting Search Fragment");
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        Log.d(TAG, "onNavigationItemSelected: Starting action intent post Activity");
                        startActivity(new Intent(MainActivity.this,com.mrash.instagramclone.PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectorFragment = new NotificationFragment();
                        Log.d(TAG, "onNavigationItemSelected: Setting Notification Fragment");
                        break;
                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        Log.d(TAG, "onNavigationItemSelected: setting ProfileFragment");
                        break;

                }
                //will set and show that fragment on main activity and replace that container
                //  layout with that clicked menu base fragment
                if(selectorFragment != null)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
                return true;
            }
        });
    }
    //after login directly start Home Activity
    private void setHomeAfterLogin()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }
}