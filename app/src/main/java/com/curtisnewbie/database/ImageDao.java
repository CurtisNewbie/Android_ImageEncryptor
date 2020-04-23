package com.curtisnewbie.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object Dao - defines methods for accessing image data
 */
@Dao
public interface ImageDao {

    @Insert
    void addImage(Image img);

    @Insert
    void addImages(List<Image> imgs);

    @Query("SELECT * FROM image")
    List<Image> getImages();

    @Query("SELECT name FROM image")
    List<String> getImageNames();

    @Query("SELECT path FROM Image WHERE name = :imgName")
    String getImagePath(String imgName);
}

