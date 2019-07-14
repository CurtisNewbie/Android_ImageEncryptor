package com.curtisnewbie.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class ImageData {

    public static final String IMG_TABLE_QUERY = "CREATE TABLE image_table (image_name TEXT PRIMARY KEY, image_data BLOB);";

    @PrimaryKey
    private String image_name;

    @ColumnInfo(name = "image_data")
    private byte[] image_data;


    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }
}
