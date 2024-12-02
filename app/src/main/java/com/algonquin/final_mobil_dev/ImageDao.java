package com.algonquin.final_mobil_dev;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// #8.4 ImageDao
/**
 * Data Access Object (DAO) for the ImageEntity.
 * Provides methods for performing database operations on the ImageEntity.
 */
@Dao
public interface ImageDao {

    /**
     * Inserts an image into the database.
     *
     * @param image The image entity to insert.
     */
    @Insert
    void insert(ImageEntity image);

    /**
     * Retrieves all images from the database.
     *
     * @return A list of all image entities.
     */
    @Query("SELECT * FROM images")
    List<ImageEntity> getAllImages();

    /**
     * Finds an image by its date.
     *
     * @param date The date of the image to find.
     * @return The image entity with the specified date.
     */
    @Query("SELECT * FROM images WHERE date = :date LIMIT 1")
    ImageEntity findByDate(String date);

    /**
     * Updates an existing image in the database.
     *
     * @param image The image entity to update.
     */
    @Update
    void update(ImageEntity image);

    /**
     * Deletes an image from the database.
     *
     * @param image The image entity to delete.
     */
    @Delete
    void delete(ImageEntity image);
}