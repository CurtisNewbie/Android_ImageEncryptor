package com.curtisnewbie.daoThread;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.ImageData;

import java.util.List;

public class AddImgThread extends Thread {

    private ImageData img;
    List<ImageData> imgs;
    private AppDatabase db;

    public AddImgThread(ImageData img, AppDatabase db) {
        this.img = img;
        this.db = db;
    }

    public AddImgThread(List<ImageData> imgs, AppDatabase db) {

        this.img = null;
        this.imgs = imgs;
        this.db = db;
    }

    @Override
    public void run() {
        if (img != null) {
            db.dao().addImageData(img);
        } else {
            db.dao().addListOfImageData(imgs);
        }

    }
}
