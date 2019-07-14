package com.curtisnewbie.database;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.curtisnewbie.ImgCrypto.Image;

import java.io.IOException;
import java.io.InputStream;

/**
 * Singleton Class for temp data storage and sharing. I will use db in the future.
 */
public class DataStorage  {

    private AppDatabase db = null;
    private static DataStorage dataStorage = null;

    public AppDatabase getDB() {
        return db;
    }

    private void iniDatabase(Context context){
        // create db for the first time
        this.db = Room.databaseBuilder(context, AppDatabase.class, "mydatabase.db").allowMainThreadQueries().build();

        // for testing
        ImageData testImg = getTestData(context);
        db.dao().addImageData(testImg);

        // for testing login
        Credential root = new Credential();
        root.setCred_name("admin");
        root.setCred_pw("password");
        db.dao().addCredential(root);
    }

    public static DataStorage getInstance(Context context) {
        if(dataStorage == null) {
            dataStorage = new DataStorage();
            dataStorage.iniDatabase(context);
            return dataStorage;
        } else{
            return dataStorage;
        }
    }

    // for testing
    public ImageData getTestData(Context context) {
        try {
            InputStream in = context.getAssets().open("d.PNG");

            byte[] data = new byte[in.available()];
            in.read(data);
            byte[] encryptedData = Image.encrypt(data, "password");

            ImageData img = new ImageData();
            img.setImage_data(encryptedData);
            img.setImage_name("Encrypted Image 1");
            return img;
        }catch(IOException e){
            Log.i("getTestData", e.getMessage());
            return null;
        }
    }


}
