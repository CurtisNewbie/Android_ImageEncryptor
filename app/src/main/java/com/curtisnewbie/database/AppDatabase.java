package com.curtisnewbie.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * This is the database implementing Room api
 */
@Database(entities = {Image.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ImageDao dao();
}
