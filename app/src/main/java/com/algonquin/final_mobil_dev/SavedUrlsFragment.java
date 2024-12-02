package com.algonquin.final_mobil_dev;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

//#5.3 SavedUrlsFragment
/**
 * SavedUrlsFragment is responsible for displaying a list of saved image URLs.
 * It allows users to view and delete saved images.
 */
public class SavedUrlsFragment extends Fragment {

    private ListView listView;
    private List<ImageEntity> imageList;
    private AppDatabase db;
    private SharedViewModel sharedViewModel;

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
        View view = inflater.inflate(R.layout.fragment_saved_urls, container, false);
        listView = view.findViewById(R.id.listView);

        db = Room.databaseBuilder(getContext(), AppDatabase.class, "image-database").build();

        loadImagesFromDatabase();

        // Watch changes in the ViewModel
        sharedViewModel.getUpdated().observe(getViewLifecycleOwner(), isSaved -> {
            if (isSaved) {
                // Reload the images from the database
                loadImagesFromDatabase();
                // Reset the updated attribute to false
                sharedViewModel.setUpdated(false);
            }
        });
        return view;
    }

    /**
     * Loads images from the database in a background thread.
     * Updates the ListView adapter on the main thread.
     */
    private void loadImagesFromDatabase() {
        new Thread(() -> {
            imageList = db.imageDao().getAllImages();
            getActivity().runOnUiThread(() -> {
                listView.setAdapter(new ImageListAdapter());
            });
        }).start();
    }


    // #1 Contains List View
    /**
     * Adapter class for displaying images in a ListView.
     */
    private class ImageListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }
        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_image, parent, false);
            }

            TextView textViewName = convertView.findViewById(R.id.textViewName);
            TextView textViewDate = convertView.findViewById(R.id.textViewDate);
            Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

            ImageEntity imageEntity = imageList.get(position);
            textViewName.setText(imageEntity.name);
            textViewDate.setText(imageEntity.date);

            buttonDelete.setOnClickListener(v -> {
                deleteImage(imageEntity);
            });

            convertView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("date", imageEntity.date);
                ImageViewerFragment imageViewerFragment = new ImageViewerFragment();
                imageViewerFragment.setArguments(bundle);
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null) {
                    mainActivity.inflateImageFragment(imageViewerFragment);
                }
            });

            return convertView;
        }
    }

    /**
     * Deletes the specified image from the database and storage.
     * Shows a Snackbar to confirm the deletion.
     *
     * @param imageEntity The image entity to delete.
     */
    private void deleteImage(ImageEntity imageEntity) {
        Snackbar snackbar = Snackbar.make(getView(), "Are you sure you want to delete this image?", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Confirm", v -> {
            new Thread(() -> {
                File imageFile = new File(imageEntity.url);
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                db.imageDao().delete(imageEntity);
                loadImagesFromDatabase();
            }).start();
        });
        snackbar.show();
    }
}