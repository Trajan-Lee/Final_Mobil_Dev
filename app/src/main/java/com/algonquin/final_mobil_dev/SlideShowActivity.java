package com.algonquin.final_mobil_dev;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class SlideShowActivity extends BaseActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private List<ImageEntity> imageList;
    private int currentIndex = 0;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_slideshow, findViewById(R.id.content_frame));

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        // Load all saved images from the database
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "image-database").build();
            imageList = db.imageDao().getAllImages();
            runOnUiThread(() -> startSlideShow());
        }).start();

    }

    private void startSlideShow() {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentIndex >= imageList.size()) {
                    currentIndex = 0;
                }

                ImageEntity imageEntity = imageList.get(currentIndex);
                File imageFile = new File(imageEntity.url);
                if (imageFile.exists()) {
                    Glide.with(SlideShowActivity.this)
                            .load(imageFile)
                            .into(imageView);
                }

                currentIndex++;
                progressBar.setProgress(0);
                handler.postDelayed(this, 5000);
            }
        };

        handler.post(runnable);

        // Update progress bar every 100ms
        new Thread(() -> {
            while (true) {
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    runOnUiThread(() -> progressBar.setProgress(progress));
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}