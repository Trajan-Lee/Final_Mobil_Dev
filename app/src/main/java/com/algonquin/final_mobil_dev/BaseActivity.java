package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

/**
 * BaseActivity is the base class for activities in the application.
 * It handles common functionality such as setting up the navigation drawer and action bar.
 */
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //#6.1 help dialog
    /**
     * Handles action bar item clicks.
     *
     * @param item The menu item that was selected.
     * @return true if the item was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a help dialog with information based on the current activity or fragment.
     */
    private void showHelpDialog() {
        String helpMessage = getHelpMessage();
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_help)
                .setMessage(helpMessage)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    // #6.2 help message based on the current activity/fragment(s)
    /**
     * Generates a help message based on the current activity or fragment.
     *
     * @return A help message string.
     */
    private String getHelpMessage() {
        StringBuilder helpMessage = new StringBuilder();

        if (this instanceof HomeActivity) {
            helpMessage.append(getString(R.string.help_home));
        } else if (this instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) this;
            if (mainActivity.isTablet()) {
                Fragment topLeftFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_top_left);
                Fragment bottomLeftFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_bottom_left);
                Fragment rightFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_right);

                if (topLeftFragment instanceof CalendarPickerFragment || bottomLeftFragment instanceof CalendarPickerFragment) {
                    helpMessage.append(getString(R.string.help_calendar_picker));
                }
                if (topLeftFragment instanceof SavedUrlsFragment || bottomLeftFragment instanceof SavedUrlsFragment) {
                    helpMessage.append("\n\n").append(getString(R.string.help_saved_urls));
                }
                if (rightFragment instanceof ImageViewerFragment) {
                    helpMessage.append("\n\n").append(getString(R.string.help_image_viewer));
                }
            } else {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof CalendarPickerFragment) {
                    helpMessage.append(getString(R.string.help_calendar_picker));
                } else if (currentFragment instanceof SavedUrlsFragment) {
                    helpMessage.append(getString(R.string.help_saved_urls));
                } else if (currentFragment instanceof ImageViewerFragment) {
                    helpMessage.append(getString(R.string.help_image_viewer));
                }
            }

        } else if (this instanceof SlideShowActivity) {
            helpMessage.append(getString(R.string.help_slideshow));
        }

        return helpMessage.toString();
    }

    /**
     * Handles navigation item selections.
     *
     * @param item The selected menu item.
     * @return true if the item was handled, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //# 4.3 nav drawer logic
        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_saved_urls) { //Repeated for CalendarPickerFragment
            //Checks if it is main Activity
            if (this instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) this;
                //checks if it is a single pane or dual pane
                if (!mainActivity.isTablet()) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    //checks if the current fragment is CalendarPickerFragment
                    if (currentFragment instanceof CalendarPickerFragment) {
                        mainActivity.replaceFragment(new SavedUrlsFragment(), true);
                    } // no Else, because it means we are on SavedURLsFragment, then close the drawer at end
                }
            } else { // if it is not MainActivity then open MainActivity with intent.putExtra()
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fragmentName", "SavedUrlsFragment");
                startActivity(intent);
            }
        } else if (id == R.id.nav_calendar_picker) {
            if (this instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) this;
                if (!mainActivity.isTablet()) {
                    Fragment currentFragment = mainActivity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    if (currentFragment instanceof SavedUrlsFragment) {
                        mainActivity.replaceFragment(new CalendarPickerFragment(), true);
                    }
                }
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fragmentName", "CalendarPickerFragment");
                startActivity(intent);
            }
        } else if(id == R.id.nav_slideshow){
            Intent intent = new Intent(this, SlideShowActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            finishAffinity();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}