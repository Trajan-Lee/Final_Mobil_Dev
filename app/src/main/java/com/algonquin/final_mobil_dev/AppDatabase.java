package com.algonquin.final_mobil_dev;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ImageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ImageDao imageDao();
}