package com.curtisnewbie.daoThread;

import com.curtisnewbie.activities.ImageViewActivity;
import com.curtisnewbie.database.AppDatabase;

public class GetImgPathThread extends Thread{

    private ImageViewActivity imageViewActivity;
    private  AppDatabase db;
    private String imgName;

    public GetImgPathThread (ImageViewActivity imageViewActivity, AppDatabase db, String imgName){
        this.imageViewActivity = imageViewActivity;
        this.db = db;
        this.imgName = imgName;
    }

    @Override
    public void run() {
        String path = db.dao().getImgPath(imgName);
        imageViewActivity.setImgPath(path);
    }
}
