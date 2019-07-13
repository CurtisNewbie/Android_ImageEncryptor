package com.curtisnewbie.dbconnect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * class for SQLite database connection and query processing
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student.db";
    private static final String CREDENTIAL_TABLE = "credential_table";
    private static final String IMAGE_TABLE = "image_table";


    private static final String CRED_ID = "credential_id"; // INTEGER
    private static final String CRED_NAME = "credential_name"; // TEXT
    private static final String CRED_PW = "credential_pw"; // TEXT

    private static final String IMG_ID = "image_id"; // INTEGER
    private static final String IMG_DATA = "image_data"; // BLOB


    private static final int VERSION = 1;

    /**
     * This is used to create database.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // execute query for creating tables
        sqLiteDatabase.execSQL("CREATE TABLE " + CREDENTIAL_TABLE + "(" + CRED_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                CRED_NAME + "TEXT NOT NULL, " + CRED_PW + "TEXT NOT NULL);");
        sqLiteDatabase.execSQL("CREATE TABLE " + IMAGE_TABLE + "(" + IMG_ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                IMG_DATA + "BOLB NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
