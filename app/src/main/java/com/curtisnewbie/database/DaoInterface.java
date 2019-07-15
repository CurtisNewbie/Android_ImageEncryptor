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

    @Query("SELECT * FROM image_table")
    List<ImageData> getListOfImg();

    @Query("SELECT image_name FROM image_table")
    List<String> getListOfImgName();

    @Query("SELECT image_data FROM image_table WHERE image_name = :imgName")
    byte[] getImgData(String imgName);
}
