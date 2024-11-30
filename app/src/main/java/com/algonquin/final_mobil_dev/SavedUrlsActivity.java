package com.algonquin.final_mobil_dev;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SavedUrlsActivity extends BaseActivity {

    private ListView listView;
    private ArrayList<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_saved_urls, findViewById(R.id.content_frame));

        listView = findViewById(R.id.listView);

        // Retrieve the list of URLs from the intent
        urlList = getIntent().getStringArrayListExtra("urlList");

        // Set up the ListView with the URLs
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, urlList);
        listView.setAdapter(adapter);
    }
}