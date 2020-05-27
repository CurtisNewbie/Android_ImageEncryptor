package com.curtisnewbie.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Representation of User model, it does not store the actual image data, only
 * the name of the image and the file path of it.
 * </p>
 */
@Entity(tableName = "image")
public class Image {

    @PrimaryKey
    @NonNull
    private String name;

    @ColumnInfo(name = "path")
    @NonNull
    private String path;

    @ColumnInfo(name = "thumbnail_path")
    @NonNull
    private String thumbnailPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
