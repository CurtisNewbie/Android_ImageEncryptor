package com.curtisnewbie.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class ImageData {
    // not used
    public static final String IMG_TABLE_QUERY = "CREATE TABLE image_table (image_name TEXT PRIMARY KEY, image_data BLOB);";

    @PrimaryKey
    @NonNull
    private String image_name;

    @ColumnInfo(name = "path")
    private String path;


    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
