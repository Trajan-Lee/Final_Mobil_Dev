package com.algonquin.final_mobil_dev;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarPicker extends BaseActivity {

    private DatePicker datePicker;
    private Button buttonFetch;
    private Button buttonViewSavedUrls;
    private NasaApiService nasaApiService;
    private ArrayList<String> urlList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.calandar_picker, findViewById(R.id.content_frame));

        datePicker = findViewById(R.id.datePicker);
        buttonFetch = findViewById(R.id.buttonFetch);
        buttonViewSavedUrls = findViewById(R.id.buttonViewSavedUrls);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nasaApiService = retrofit.create(NasaApiService.class);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                String date = String.format("%d-%02d-%02d", year, month, day);
                fetchImage(date);
            }
        });

        buttonViewSavedUrls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarPicker.this, SavedUrlsActivity.class);
                intent.putStringArrayListExtra("urlList", urlList);
                startActivity(intent);
            }
        });
    }

    private void fetchImage(String date) {
        Call<NasaImage> call = nasaApiService.getImage("YOUR_API_KEY", date);
        call.enqueue(new Callback<NasaImage>() {
            @Override
            public void onResponse(Call<NasaImage> call, Response<NasaImage> response) {
                if (response.isSuccessful()) {
                    NasaImage nasaImage = response.body();
                    String imageUrl = nasaImage.getUrl();
                    urlList.add(imageUrl);
                    Toast.makeText(CalendarPicker.this, "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CalendarPicker.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NasaImage> call, Throwable t) {
                Toast.makeText(CalendarPicker.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}