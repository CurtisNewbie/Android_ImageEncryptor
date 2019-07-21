package com.curtisnewbie.daoThread;

import com.curtisnewbie.database.AppDatabase;
import com.curtisnewbie.database.ImageData;

public class AddImgThread extends Thread {

    private ImageData img;
    private AppDatabase db;

    public AddImgThread(ImageData img, AppDatabase db){
        this.img = img;
    }

    @Override
    public void run() {
        db.dao().addImageData(img);
    }
}
