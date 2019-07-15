package com.curtisnewbie.database;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton Class for temp data storage and sharing. I will use db in the future.
 */
public class DataStorage {

    private AppDatabase db = null;
    private static DataStorage dataStorage = null;
    public static final String PW_TAG = "pw";

    public AppDatabase getDB() {
        return db;
    }

    private void iniDatabase(Context context) {
        // create db for the first time
        this.db = Room.databaseBuilder(context, AppDatabase.class, "mydatabase.db").allowMainThreadQueries().build();

        // for getting local encrypted images
        List<ImageData> localImg = getLocalEncryptedData(context);
        for (ImageData img : localImg)
            db.dao().addImageData(img);
    }

    public static DataStorage getInstance(Context context) {
        if (dataStorage == null) {

            // check if local one exists
            File tempDb = context.getDatabasePath("mydatabase.db");
            if (tempDb.exists()) {
                tempDb.delete();
            }
            dataStorage = new DataStorage();
            dataStorage.iniDatabase(context);
            return dataStorage;
        } else {
            return dataStorage;
        }
    }

    /**
     * Get local Encrypted Data
     *
     * @param context context
     * @return list of ImageData obj
     */
    private List<ImageData> getLocalEncryptedData(Context context) {
        // directory
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();

        List<ImageData> imgData = new LinkedList<>();

        for (File file : files) {
            if (!file.getName().equals("cred.txt")) {
                try {
                    // read each file
                    InputStream in = new FileInputStream(file);
                    byte[] data = new byte[in.available()];
                    in.read(data);
                    in.close();

                    // store each ImageData obj to the list.
                    ImageData img = new ImageData();
                    img.setImage_data(data);
                    img.setImage_name(file.getName());
                    imgData.add(img);
                } catch (IOException e) {
                    Log.i("getLocalEncryptedData", e.toString() + e.getMessage());
                }
            }
        }
        if (imgData != null)
            return imgData;
        else
            return null;
    }

    public static byte[] hashingCred(String name, String pw) {
        final String HASHING_ALGORITHM = "SHA-256";
        try {
            String cred = name + ":" + pw;
            byte[] credBytes = cred.getBytes("UTF-8");

            // digest the credBytes
            MessageDigest md = MessageDigest.getInstance(HASHING_ALGORITHM);
            md.update(credBytes, 0, credBytes.length);
            byte[] hashedCred = md.digest();
            return hashedCred;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
