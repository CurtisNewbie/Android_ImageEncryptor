package com.curtisnewbie.database;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object Dao - defines methods for accessing data
 */
public interface DaoInterface {
    @Insert // insert cred in db
    public abstract void addCredential(Credential cred);

    @Insert
    public abstract void addImageData(ImageData img);

    @Query("SELECT * FROM image_table")
    public abstract List<ImageData> getListOfImg();

    @Query("SELECT * FROM cred_table")
    public abstract List<Credential> getListOfCred();

    @Query("SELECT image_data FROM image_table WHERE image_name = :imgName")
    public abstract byte[] getImgData(String imgName);

    @Query("SELECT cred_pw FROM cred_table WHERE cred_name = :name")
    public abstract String getDecryPW(String name);
}
