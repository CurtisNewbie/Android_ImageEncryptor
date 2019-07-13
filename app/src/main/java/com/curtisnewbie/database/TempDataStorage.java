package com.curtisnewbie.ImageItem;


import android.util.Log;

/**
 * Singleton Class for temp data storage and sharing. I will use db in the future.
 */
public class TempDataStorage {

    private static final TempDataStorage tempDataStorage = new TempDataStorage();
    private static final String TAG = "temp";

    private byte[] tempData = null;

    public byte[] getTempData() {
        Log.i(TAG, "tempGet");
        return tempData;
    }

    public void setTempData(byte[] data) {
        this.tempData = data;
        Log.i(TAG, "tempSet");
    }

    public void cleanTempData() {
        this.tempData = null;
    }

    public static TempDataStorage getInstance() {
        return tempDataStorage;
    }


}
