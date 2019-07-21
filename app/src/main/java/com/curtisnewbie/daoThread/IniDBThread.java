package com.curtisnewbie.daoThread;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.ImageData;

import java.util.List;

public class IniDBThread extends Thread {

    private List<ImageData> imgs;
    private AppDatabase db;

    public IniDBThread(List<ImageData> imgs, AppDatabase db) {

        this.imgs = imgs;
        this.db = db;
    }

    @Override
    public void run() {
       db.dao().addListOfImageData(imgs);
    }
}
