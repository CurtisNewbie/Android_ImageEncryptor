package com.curtisnewbie.daoThread;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.Image;

import java.util.List;

public class AddImgThread extends Thread {

    private Image img;
    private List<Image> imgs;
    private AppDatabase db;

    public AddImgThread(Image img, AppDatabase db) {
        this.img = img;
        this.db = db;
    }

    public AddImgThread(List<Image> imgs, AppDatabase db) {
        this.img = null;
        this.imgs = imgs;
        this.db = db;
    }

    @Override
    public void run() {
        if (img != null) {
            db.dao().addImage(img);
        } else {
            db.dao().addImages(imgs);
        }

    }
}
