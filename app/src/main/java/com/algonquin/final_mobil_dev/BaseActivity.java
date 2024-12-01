package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //#4.1 top toolbar and nav View
    protected DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // #4.2 version number
        String version = "Version" + getString(R.string.version_number);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setSubtitle(version);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_saved_urls) { //Repeated for CalendarPickerFragment
            //Checks if it is main Activity
            if (this instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) this;
                //checks if it is a single pane or dual pane
                if (findViewById(R.id.fragment_container) != null) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    //checks if the current fragment is CalendarPickerFragment
                    if (currentFragment instanceof CalendarPickerFragment) {
                        mainActivity.replaceFragment(new SavedUrlsFragment(), true);
                    } // no Else, because it means we are on SavedURLsFragment, then close the drawer at end
                } else if (findViewById(R.id.fragment_container_left) != null && findViewById(R.id.fragment_container_right) != null) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container_left);
                    if (currentFragment instanceof CalendarPickerFragment) {
                        mainActivity.replaceFragment(new SavedUrlsFragment(), true);
                    } //Again, we skip else
                }
            } else { // if it is not MainActivity then open MainActivity with intent.putExtra()
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fragmentName", "SavedUrlsFragment");
                startActivity(intent);
            }
        } else if (id == R.id.nav_calendar_picker) {
            if (this instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) this;
                if (findViewById(R.id.fragment_container) != null) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof SavedUrlsFragment) {
                        mainActivity.replaceFragment(new CalendarPickerFragment(), true);
                    }
                } else if (findViewById(R.id.fragment_container_left) != null && findViewById(R.id.fragment_container_right) != null) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container_left);
                    if (currentFragment instanceof SavedUrlsFragment) {
                        mainActivity.replaceFragment(new CalendarPickerFragment(), true);
                    }
                }
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fragmentName", "CalendarPickerFragment");
                startActivity(intent);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}