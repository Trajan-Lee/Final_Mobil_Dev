package com.algonquin.final_mobil_dev;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarPickerFragment extends Fragment {

    private DatePicker datePicker;
    private Button buttonFetch;
    private NasaApiService nasaApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_picker, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        buttonFetch = view.findViewById(R.id.buttonFetch);

        // Load the last viewed date
        int[] lastViewedDate = loadLastViewedDate(getContext());
        if (lastViewedDate[0] != -1 && lastViewedDate[1] != -1) {
            datePicker.updateDate(lastViewedDate[1], lastViewedDate[0], 1);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nasaApiService = retrofit.create(NasaApiService.class);

        buttonFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                String date = String.format("%d-%02d-%02d", year, month + 1, day);

                // Save the last viewed date
                saveLastViewedDate(getContext(), month, year);

                fetchImage(date);
            }
        });

        return view;
    }

    // #10 SharedPrefs to save the last viewed calendar month and year
    // Save the last viewed calendar month and year
    public void saveLastViewedDate(Context context, int month, int year) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastViewedMonth", month);
        editor.putInt("lastViewedYear", year);
        editor.apply();
    }

    // Load the last viewed calendar month and year
    public int[] loadLastViewedDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        int month = sharedPreferences.getInt("lastViewedMonth", -1); // Default value -1 if not found
        int year = sharedPreferences.getInt("lastViewedYear", -1); // Default value -1 if not found
        return new int[]{month, year};
    }

    private void fetchImage(String date) {
        Call<NasaImage> call = nasaApiService.getImage("vb1yIZofZfGMAEga9s6bhYahuUAgJv8HJtHnD7X1", date);
        call.enqueue(new Callback<NasaImage>() {
            @Override
            public void onResponse(Call<NasaImage> call, Response<NasaImage> response) {
                if (response.isSuccessful()) {
                    NasaImage nasaImage = response.body();
                    String imageUrl = nasaImage.getUrl();
                    Toast.makeText(getActivity(), "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();
                    openImageViewerFragment(imageUrl, date);
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NasaImage> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageViewerFragment(String imageUrl, String date) {
        ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        args.putString("date", date);
        imageViewerFragment.setArguments(args);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.replaceFragment(imageViewerFragment, false);
        }
    }
}