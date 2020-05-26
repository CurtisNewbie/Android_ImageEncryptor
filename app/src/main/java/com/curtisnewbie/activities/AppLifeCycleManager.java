package com.curtisnewbie.activities;

import android.content.Context;
import android.content.Intent;

import com.curtisnewbie.services.App;
import com.curtisnewbie.services.AuthService;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * This class manages the lifecycle of the whole application. It's operations are fairly simple,
 * this class can be considered as a unified way to manage the app's lifecycle, such as restarting
 * the app.
 */
@Singleton
public class AppLifeCycleManager {

    @Inject
    protected AuthService auth;
    private Context appContext;

    public AppLifeCycleManager(Context appContext) {
        this.appContext = appContext;
        App.getAppComponent().inject(this);
    }

    /**
     * Restart the app
     * <p>
     * Creates the {@code MainActivity}, clear all remaining activities in the stack, and signout the
     * authenticated user.
     */
    public void restart() {
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.appContext.startActivity(intent);
        this.auth.signOut();
    }
}
