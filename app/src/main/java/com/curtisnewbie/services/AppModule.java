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
 * Module for Dependency Injection in this Application
 */
@Module
public class AppModule {

    private static final String DB_NAME = "imageEncrypter.db";
    private Context appContext;

    public AppModule(Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    public AppDatabase providesAppDatabase() {
        return Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME).build();
    }

    @Singleton
    @Provides
    AuthService provideAuthService() {
        return new AuthService();
    }

    @Provides
    @Singleton
    ExecService providesThreadManager() {
        return new ExecService();
    }

}
