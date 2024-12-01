package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.content_frame));
        if (findViewById(R.id.fragment_container) != null) {
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
        } else if (findViewById(R.id.fragment_container_left) != null && findViewById(R.id.fragment_container_right) != null) {
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
                        .add(R.id.fragment_container_left, fragment).commit();
            } else {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        }

    }

    public void replaceFragment(Fragment fragment, boolean isLeft) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}