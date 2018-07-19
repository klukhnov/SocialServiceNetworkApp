package com.example.home.socialservicenetworkapp;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView recyclerView;
    private Toolbar mToolbar; // note that it is not a standard toolbar


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("home");

        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        // since we are using it for a drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header); // navigationView is now stored in
        // the variable navView

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this, "Friends List", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_find_friends:
                Toast.makeText(this, "Find Friends", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_messages:
                Toast.makeText(this, "Messages", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
