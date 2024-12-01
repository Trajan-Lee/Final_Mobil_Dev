package com.algonquin.final_mobil_dev;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageViewerFragment extends Fragment {

    // #3.1 edit text
    private EditText editTextImageName;
    private ImageView imageView;
    private Button buttonSaveImage;
    private Button buttonClose;
    private Button buttonDelete;
    private AppDatabase db;
    private String selectedDate;
    private ImageEntity imageEntity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        editTextImageName = view.findViewById(R.id.editTextImageName);
        imageView = view.findViewById(R.id.imageView);
        buttonSaveImage = view.findViewById(R.id.buttonSaveImage);
        buttonClose = view.findViewById(R.id.buttonClose);

        db = Room.databaseBuilder(getContext(), AppDatabase.class, "image-database").build();

        if (getArguments() != null) {
            String imageUrl = getArguments().getString("imageUrl");
            selectedDate = getArguments().getString("date");
            if (selectedDate != null) {
                checkDatabaseForImage(selectedDate, imageUrl);
            }
        }

        buttonSaveImage.setOnClickListener(v -> {
            String imageName = editTextImageName.getText().toString().trim();
            if (TextUtils.isEmpty(imageName)) {
                // #3.2 toast
                Toast.makeText(getContext(), "Please enter a name for the image", Toast.LENGTH_SHORT).show();
            } else {
                if (imageEntity == null) {
                    saveImage(selectedDate, imageName);
                } else {
                    updateImage(imageName);
                }
                getParentFragmentManager().popBackStack();
            }
        });

        buttonClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void checkDatabaseForImage(String date, String imageUrl) {
        new Thread(() -> {
            imageEntity = db.imageDao().findByDate(date);
            getActivity().runOnUiThread(() -> {
                if (imageEntity != null) {
                    editTextImageName.setText(imageEntity.name);
                    buttonSaveImage.setText("Update");
                    addDeleteButton();
                    loadImageFromFile(imageEntity.url);
                } else {
                    loadImageFromWeb(imageUrl);
                }
            });
        }).start();
    }

    private void loadImageFromFile(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .into(imageView);
        } else {
            Toast.makeText(getContext(), "Image file not found", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void addDeleteButton() {
        LinearLayout buttonContainer = getView().findViewById(R.id.buttonContainer);
        buttonDelete = new Button(getContext());
        buttonDelete.setText("Delete");
        buttonDelete.setOnClickListener(v -> {
            deleteImage();
            getParentFragmentManager().popBackStack();
        });
        buttonContainer.addView(buttonDelete);
    }

    // #8.3 delete image from storage and DB
    private void deleteImage() {
        // #3.3 snackbar
        Snackbar snackbar = Snackbar.make(getView(), "Are you sure you want to delete this image?", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Confirm", v -> {
            new Thread(() -> {
                File imageFile = new File(imageEntity.url);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                db.imageDao().delete(imageEntity);
            }).start();
        });
        snackbar.show();
    }

    private void updateImage(String imageName) {
        new Thread(() -> {
            imageEntity.name = imageName;
            db.imageDao().update(imageEntity);
        }).start();
    }

    public void loadImageFromWeb(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    // #8.1 save image to external storage
    private static final String TAG = "ImageViewerFragment";

    private void saveImage(String selectedDate, String imageName) {
        Log.d(TAG, "saveImage called with date: " + selectedDate + " and name: " + imageName);
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            saveBitmapToFile(bitmap, selectedDate, imageName);
        } else {
            Log.e(TAG, "Drawable is not an instance of BitmapDrawable");
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveBitmapToFile(Bitmap bitmap, String selectedDate, String imageName) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "NasaApp");
        if (!storageDir.exists()) {
            boolean dirCreated = storageDir.mkdirs();
            Log.d(TAG, "Directory created: " + dirCreated);
        }

        File imageFile = new File(storageDir, selectedDate + ".jpg");
        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Log.d(TAG, "Image compressed: " + compressed);
            addImageToDatabase(imageName, imageFile.getAbsolutePath());
            Log.d(TAG, "Image saved to database with path: " + imageFile.getAbsolutePath());
            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save image", e);
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    // #8.2 add image details to DB
    private void addImageToDatabase(String imageName, String imageUrl) {
        new Thread(() -> {
            ImageEntity image = new ImageEntity();
            image.name = imageName;
            image.url = imageUrl;
            image.date = selectedDate;
            db.imageDao().insert(image);
        }).start();
    }
}