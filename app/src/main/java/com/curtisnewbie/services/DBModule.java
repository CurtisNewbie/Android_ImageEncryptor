package com.curtisnewbie.services;

import android.content.Context;
import androidx.room.Room;
import com.curtisnewbie.database.AppDatabase;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Module that provides Dependency Injection of AppDatabase
 */
@Module
public class DBModule {

    private static final String DB_NAME = "imageEncrypter.db";
    private Context appContext;

    public DBModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    public AppDatabase providesAppDatabase() {
        return Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME).build();
    }
}
