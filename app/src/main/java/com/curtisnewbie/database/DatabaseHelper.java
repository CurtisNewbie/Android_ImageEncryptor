//package com.curtisnewbie.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.util.ArrayList;
//
///**
// * class for SQLite database connection and query processing
// */
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "mydatabase.db";
//
//    private static final int VERSION = 1;
//
//    /**
//     * This is used to create database.
//     *
//     * @param context Context object
//     */
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//
//        // execute query for creating tables
//        sqLiteDatabase.execSQL(Credential.CRED_TABLE_QUERY);
//
//        sqLiteDatabase.execSQL(ImageData.IMG_TABLE_QUERY);
//
//        // ContentValues is for storing the data that are inserted into the table
//        ContentValues content = new ContentValues();
//        content.put(CRED_NAME, "admin");
//        content.put(CRED_PW, "password");
//        long result = sqLiteDatabase.insert(CREDENTIAL_TABLE, null, content);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//
//    }
//
//    /**
//     * This is used to check credential
//     *
//     * @param name name
//     * @param pw   password
//     * @return true/false indicating whether the credential is verified.
//     */
//    public boolean checkCredential(String name, String pw) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String query = "SELECT cred_name, cred_pw FROM cred_table";
//
//        // cursor is used to access the result
//        Cursor curs = db.rawQuery(query, null);
//
//        curs.moveToNext();
//        // only one user is needed.
//        if (curs.getString(0).equals(name) && curs.getString(1).equals(pw)) {
//            curs.close();
//            return true;
//        } else {
//            curs.close();
//            return false;
//        }
//    }
//
//    /**
//     * Get the encrypted data
//     *
//     * @param imgName image name as primary key
//     * @return byte[] of encrypted data
//     */
//    public byte[] getEncryptedImgData(String imgName) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT image_data FROM image_table WHERE image_name = '" + imgName + "'";
//
//        // cursor is used to access the result
//        Cursor curs = db.rawQuery(query, null);
//        byte[] bytes = curs.getBlob(0);
//        curs.close();
//        return bytes;
//    }
//
//    /**
//     * Get the list of title of images for RecyclerView
//     *
//     * @return ArrayList of titles
//     */
//    public ArrayList<String> getListOfImgTitle() {
//        ArrayList<String> listOfTitle = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT image_name FROM image_table;";
//
//        // cursor is used to access the result
//        Cursor curs = db.rawQuery(query, null);
//
//        while (curs.moveToNext()) {
//            listOfTitle.add(curs.getString(0));
//        }
//        curs.close();
//        return listOfTitle;
//    }
//
//    /**
//     * Get the password
//     *
//     * @return password
//     */
//    public char[] getDecryPW() {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String query = "SELECT cred_pw FROM cred_table;";
//
//        // cursor is used to access the result
//        Cursor curs = db.rawQuery(query, null);
//        curs.close();
//        return curs.getString(0).toCharArray();
//    }
//
//
//
//
//}
