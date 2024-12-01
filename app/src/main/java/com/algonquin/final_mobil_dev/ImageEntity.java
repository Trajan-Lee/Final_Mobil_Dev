package com.algonquin.final_mobil_dev;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "images")
public class ImageEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String url;
    public String date;
}