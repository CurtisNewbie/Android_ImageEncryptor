package com.curtisnewbie.database;


import android.content.Context;

import androidx.room.Room;

/**
 * Singleton Class for the room database and local data reading.
 */
// TODO: Change to DI and @Singleton
public class DBManager {
    private static final String DB_NAME = "imageEncrypter.db";
    private AppDatabase db = null;
    private static DBManager dbManager = null;

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
