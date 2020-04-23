package com.curtisnewbie.database;


import android.content.Context;

import androidx.room.Room;

import com.curtisnewbie.daoThread.AddImgThread;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton Class for the room database and local data reading.
 */
public class DataStorage {

    private AppDatabase db = null;
    private static DataStorage dataStorage = null;

    /**
     * Used for passing password with intent between activities
     */
    public static final String PW_TAG = "pw";

    public AppDatabase getDB() {
        return db;
    }

    /**
     * build a new database and load local encrypted data/images
     *
     * @param context
     */
    private void iniDatabase(Context context) {
        // create db for the first time
        this.db = Room.databaseBuilder(context, AppDatabase.class, "mydatabase.db").build();

        // for getting local encrypted images
        List<Image> localImg = getLocalEncryptedData(context);

        if (localImg != null) {
            // this is a thread
            new AddImgThread(localImg, db).start();
        }
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
     * Get local Encrypted Data (only file name and path). Actual data are not loaded
     * due to the memory issue.
     *
     * @param context context
     * @return list of ImageData obj (encrypted)
     */
    private List<Image> getLocalEncryptedData(Context context) {
        // directory
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();

        List<Image> imgData = new LinkedList<>();

        for (File file : files) {
            if (!file.getName().equals("cred.txt")) {

                // store each ImageData obj to the list.
                Image img = new Image();
                img.setPath(file.getPath());
                img.setName(file.getName());
                imgData.add(img);
            }
        }
        if (imgData != null && imgData.size() != 0)
            return imgData;
        else
            return null;
    }

    /**
     * Hashing the name as well as the password for authentication. It uses SHA-256 hashing algorithm.
     *
     * @param name
     * @param pw
     * @return the hashed credential
     */
    public static byte[] hashingCred(String name, String pw) {
        final String HASHING_ALGORITHM = "SHA-256";
        try {
            String cred = name + pw;
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
