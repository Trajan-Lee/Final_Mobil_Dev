package com.algonquin.final_mobil_dev;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// #8.6 AppDatabase
/**
 * The AppDatabase class serves as the main database configuration class for Room.
 * It defines the database configuration and serves as the main access point for the underlying connection to the app's persisted data.
 */
@Database(entities = {ImageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Provides access to the ImageDao for performing database operations on the ImageEntity.
     *
     * @return An instance of ImageDao.
     */
    public abstract ImageDao imageDao();
}