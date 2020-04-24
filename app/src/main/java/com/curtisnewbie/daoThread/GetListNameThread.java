package com.curtisnewbie.daoThread;

import com.curtisnewbie.activities.ImageListAdapter;
import com.curtisnewbie.database.AppDatabase;

//TODO: Consider removing this class since we can use Lambda?
public class GetListNameThread extends Thread {

    private ImageListAdapter adapter;
    private AppDatabase db;

    public GetListNameThread(ImageListAdapter adapter, AppDatabase db){
        this.adapter = adapter;
        this.db =db;
    }

    @Override
    public void run() {
        this.adapter.setImageNames(db.imgDao().getImageNames());
    }
}
