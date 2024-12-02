package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * HomeActivity is the main entry point of the application.
 * It provides navigation to other parts of the app such as the CalendarPickerFragment and SavedUrlsFragment.
 */
public class HomeActivity extends BaseActivity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.content_frame));

        // Set up button click listeners
        Button buttonCalendarPicker = findViewById(R.id.buttonCalendarPicker);
        buttonCalendarPicker.setOnClickListener(v -> openMainActivity("CalendarPickerFragment"));

        // Check if the buttonSavedUrls exists (for non-sw720dp layouts)
        Button buttonSavedUrls = findViewById(R.id.buttonSavedUrls);
        if (buttonSavedUrls != null) {
            buttonSavedUrls.setOnClickListener(v -> openMainActivity("SavedUrlsFragment"));
        }
    }

    /**
     * Opens the MainActivity with the specified fragment.
     *
     * @param fragmentName The name of the fragment to open in MainActivity.
     */
    private void openMainActivity(String fragmentName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentName", fragmentName);
        startActivity(intent);
    }
}