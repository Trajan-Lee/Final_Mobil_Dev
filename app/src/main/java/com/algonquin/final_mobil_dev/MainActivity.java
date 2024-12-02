package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * MainActivity is the main entry point of the application.
 * It handles the initialization of fragments based on the device type (tablet or phone) and the provided intent extras.
 */
public class MainActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));
        if (isTablet()) {
            if (savedInstanceState != null) {
                return;
            }
            Fragment calendarFragment = new CalendarPickerFragment();
            Fragment savedUrlsFragment = new SavedUrlsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_top_left, calendarFragment)
                    .replace(R.id.fragment_container_bottom_left, savedUrlsFragment)
                    .commit();
        } else {
            if (savedInstanceState != null) {
                return;
            }

            String fragmentName = getIntent().getStringExtra("fragmentName");
            Fragment fragment = null;

            if ("CalendarPickerFragment".equals(fragmentName)) {
                fragment = new CalendarPickerFragment();
            } else if ("SavedUrlsFragment".equals(fragmentName)) {
                fragment = new SavedUrlsFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
            } else {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        }

    }

    /**
     * Checks if the device is a tablet.
     *
     * @return true if the device is a tablet, false otherwise.
     */
    public boolean isTablet(){
        return findViewById(R.id.fragment_container) == null;
    }

    /**
     * Inflates the ImageViewerFragment in the appropriate container based on the device type.
     *
     * @param frag The ImageViewerFragment to inflate.
     */
    public void inflateImageFragment(ImageViewerFragment frag){
        if (isTablet()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_right, frag)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Replaces the current fragment with the specified fragment.
     * Adds the transaction to the back stack.
     *
     * @param fragment The fragment to replace the current fragment with.
     * @param isLeft Indicates whether the fragment should be placed in the left container (for tablets).
     */
    public void replaceFragment(Fragment fragment, boolean isLeft) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        /* Code for selecting the correct fragment container based on the device type
           Currently unused, will need to be updated to account for top and bottom left frames

        if (findViewById(R.id.fragment_container_left) != null && findViewById(R.id.fragment_container_right) != null) {
            // Tablet device
            if (isLeft) {
                fragmentTransaction.replace(R.id.fragment_container_left, fragment);
            } else {
                fragmentTransaction.replace(R.id.fragment_container_right, fragment);
            }
        } else {
            // Phone device
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }
        */
    }
}