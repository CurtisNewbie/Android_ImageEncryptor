package com.curtisnewbie.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Data Access Object Dao - defines methods for accessing image data
 * </p>
 */
@Dao
public interface ImageDao {

    @Insert
    void addImage(Image img);

    @Query("SELECT * FROM image where name = :imgName")
    Image getImage(String imgName);

    @Query("SELECT COUNT(*) FROM image where name = :imgName")
    int imageCount(String imgName);

    @Delete
    void deleteImage(Image image);

    @Query("SELECT * FROM image")
    List<Image> getImages();

    @Query("SELECT name FROM image")
    List<String> getImageNames();

    @Query("SELECT path FROM Image WHERE name = :imgName")
    String getImagePath(String imgName);

    @Query("SELECT thumbnail_path FROM Image WHERE name = :imgName")
    String getImageThumbnailPath(String imgName);
}
