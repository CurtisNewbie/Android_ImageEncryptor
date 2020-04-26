package com.curtisnewbie.database;

import android.content.Context;

import androidx.room.Room;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Singleton Class for the room database.
 * </p>
 */
// TODO: Change to DI and @Singleton
public class DBManager {
    private static final String DB_NAME = "imageEncrypter.db";
    private AppDatabase db = null;
    private static DBManager dbManager = null;

    /**
     * Used for passing imgKey with intent between activities.
     *
     * @see User
     * @see com.curtisnewbie.activities.MainActivity#imgKey
     */
    public static final String IMG_KEY_TAG = "pw";

    /**
     * Get the {@code AppDatabase}.
     *
     * @return the {@code AppDatabase}
     */
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

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager();
            dbManager.iniDatabase(context);
            return dbManager;
        } else {
            return dbManager;
        }
    }
}
