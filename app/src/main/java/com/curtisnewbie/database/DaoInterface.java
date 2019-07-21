package com.curtisnewbie.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Data Access Object Dao - defines methods for accessing data
 */
@Dao
public interface DaoInterface {

    @Insert
    void addImageData(ImageData img);

    @Insert
    void addListOfImageData(List<ImageData> imgs);

    @Query("SELECT * FROM image_table")
    List<ImageData> getListOfImg();

    @Query("SELECT image_name FROM image_table")
    List<String> getListOfImgName();

    @Query("SELECT image_path FROM image_table WHERE image_name = :imgName")
    String getImgPath(String imgName);
}

