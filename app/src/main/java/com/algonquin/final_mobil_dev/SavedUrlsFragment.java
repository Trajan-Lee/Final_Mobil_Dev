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
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.List;

public class SavedUrlsFragment extends Fragment {

    private ListView listView;
    private List<ImageEntity> imageList;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_urls, container, false);
        listView = view.findViewById(R.id.listView);

        db = Room.databaseBuilder(getContext(), AppDatabase.class, "image-database").build();

        loadImagesFromDatabase();

        return view;
    }

    private void loadImagesFromDatabase() {
        new Thread(() -> {
            imageList = db.imageDao().getAllImages();
            getActivity().runOnUiThread(() -> {
                listView.setAdapter(new ImageListAdapter());
            });
        }).start();
    }


    // #1 Contains List View
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
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, imageViewerFragment)
                        .addToBackStack(null)
                        .commit();
            });

            return convertView;
        }
    }

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