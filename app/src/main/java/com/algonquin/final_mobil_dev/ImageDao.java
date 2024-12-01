package com.algonquin.final_mobil_dev;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert
    void insert(ImageEntity image);

    @Query("SELECT * FROM images")
    List<ImageEntity> getAllImages();

    @Query("SELECT * FROM images WHERE date = :date LIMIT 1")
    ImageEntity findByDate(String date);

    @Update
    void update(ImageEntity image);

    @Delete
    void delete(ImageEntity image);

}