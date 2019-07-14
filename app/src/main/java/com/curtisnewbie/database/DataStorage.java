package com.curtisnewbie.database;


import android.content.Context;
import android.util.Log;

import androidx.room.Room;

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
//            this.db = new DatabaseHelper(context);
        this.db = Room.databaseBuilder(context, AppDatabase.class, "mydatabase.db").allowMainThreadQueries().build();
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


}
