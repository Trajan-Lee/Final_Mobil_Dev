package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_home, findViewById(R.id.content_frame));

        // Set up button click listeners
        Button buttonCalendarPicker = findViewById(R.id.buttonCalendarPicker);
        Button buttonSavedUrls = findViewById(R.id.buttonSavedUrls);

        buttonCalendarPicker.setOnClickListener(v -> openMainActivity("CalendarPickerFragment"));
        buttonSavedUrls.setOnClickListener(v -> openMainActivity("SavedUrlsFragment"));


    }

    private void openMainActivity(String fragmentName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentName", fragmentName);
        startActivity(intent);
    }
}