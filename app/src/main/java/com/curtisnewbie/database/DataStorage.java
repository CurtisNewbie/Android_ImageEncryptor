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
// TODO: Change to DI and @Singleton
public class DataStorage {
    private static final String DB_NAME = "imageEncrypter.db";
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
        // create db that is bound to the context of the whole application (or process)
        this.db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME).build();
    }

    public static DataStorage getInstance(Context context) {
        if (dataStorage == null) {
            dataStorage = new DataStorage();
            dataStorage.iniDatabase(context);
            return dataStorage;
        } else {
            return dataStorage;
        }
    }

//     TODO: consider deleting this method? Since the database should persist the data already
//
//        /**
//     * Get local Encrypted Data (only file name and path). Actual data are not loaded
//     * due to the memory issue.
//     *
//     * @param context context
//     * @return list of ImageData obj (encrypted)
//     */
//    private List<Image> getLocalEncryptedData(Context context) {
//        // directory
//        File dir = context.getFilesDir();
//        File[] files = dir.listFiles();
//
//        List<Image> imgData = new LinkedList<>();
//
//        for (File file : files) {
//            if (!file.getName().equals("cred.txt")) {
//
//                // store each ImageData obj to the list.
//                Image img = new Image();
//                img.setPath(file.getPath());
//                img.setName(file.getName());
//                imgData.add(img);
//            }
//        }
//        if (imgData != null && imgData.size() != 0)
//            return imgData;
//        else
//            return null;
//    }
}
