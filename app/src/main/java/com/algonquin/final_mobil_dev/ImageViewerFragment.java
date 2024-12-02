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
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

// #5.1 ImageViewerFragment
/**
 * ImageViewerFragment is a Fragment subclass that displays an image in an ImageView.
 * It allows the user to save the image to external storage and add a name to the image.
 */
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
    private SharedViewModel sharedViewModel;
    private static final String TAG = "ImageViewerFragment";

    /**
     * Called when the fragment is first created.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        editTextImageName = view.findViewById(R.id.editTextImageName);
        imageView = view.findViewById(R.id.imageView);
        buttonSaveImage = view.findViewById(R.id.buttonSaveImage);
        buttonClose = view.findViewById(R.id.buttonClose);

        // #8.7 The database is retrieved or constructed here
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
                closeImageViewer();
            }
        });

        buttonClose.setOnClickListener(v -> {
            closeImageViewer();
        });

        return view;
    }

    /**
     * Closes the image viewer.
     * On tablets, it removes the fragment. On phones, it pops the back stack.
     */
    private void closeImageViewer() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null && mainActivity.isTablet()) {
            // On tablet, replace the ImageViewerFragment with an empty fragment or remove it
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .remove(this)
                    .commit();
        } else {
            // On phone, pop the back stack
            getParentFragmentManager().popBackStack();
        }
    }
    /**
     * Checks the database for an image with the specified date.
     * If found, loads the image from the file. Otherwise, loads the image from the web.
     *
     * @param date The date of the image to check.
     * @param imageUrl The URL of the image to load if not found in the database.
     */
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

    /**
     * Loads an image from the specified file path.
     *
     * @param imagePath The path of the image file to load.
     */
    private void loadImageFromFile(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            Glide.with(this)
                    .load(imageFile)
                    .into(imageView);
        } else {
            Toast.makeText(getContext(), "Image file not found", Toast.LENGTH_SHORT).show();
            closeImageViewer();
        }
    }

    /**
     * Adds a delete button to the fragment's view.
     * The delete button allows the user to delete the current image.
     */
    private void addDeleteButton() {
        LinearLayout buttonContainer = getView().findViewById(R.id.buttonContainer);
        buttonDelete = new Button(getContext());
        buttonDelete.setText("Delete");
        buttonDelete.setOnClickListener(v -> {
            deleteImage();
            closeImageViewer();
        });
        buttonContainer.addView(buttonDelete);
    }

    // #8.3 delete image from storage and DB
    /**
     * Deletes the current image from the storage and database.
     * Shows a Snackbar to confirm the deletion.
     */
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
                // Notify the ViewModel that an image has been saved
                sharedViewModel.setUpdated(true);
            }).start();
        });
        snackbar.show();
    }

    /**
     * Updates the current image's name in the database.
     *
     * @param imageName The new name for the image.
     */
    private void updateImage(String imageName) {
        new Thread(() -> {
            imageEntity.name = imageName;
            db.imageDao().update(imageEntity);
            // Notify the ViewModel that an image has been saved
            sharedViewModel.setUpdated(true);
        }).start();
    }

    /**
     * Loads an image from the web using the specified URL.
     *
     * @param imageUrl The URL of the image to load.
     */
    public void loadImageFromWeb(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }

    // #8.1 save image to external storage
    /**
     * Saves the current image to external storage and the database.
     *
     * @param selectedDate The date associated with the image.
     * @param imageName The name of the image.
     */
    private void saveImage(String selectedDate, String imageName) {
        Log.d(TAG, "saveImage called with date: " + selectedDate + " and name: " + imageName);
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            saveBitmapToFile(bitmap, selectedDate, imageName);
            // Notify the ViewModel that an image has been saved
            sharedViewModel.setUpdated(true);
        } else {
            Log.e(TAG, "Drawable is not an instance of BitmapDrawable");
            Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the specified bitmap to a file and adds the image details to the database.
     *
     * @param bitmap The bitmap to save.
     * @param selectedDate The date associated with the image.
     * @param imageName The name of the image.
     */
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
    /**
     * Adds the image details to the database.
     *
     * @param imageName The name of the image.
     * @param imageUrl The URL of the image.
     */
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