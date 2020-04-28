package com.curtisnewbie.services;

import android.app.Application;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Custom Application class for Dependency Injection. The modules are instantiated here.
 */
public class App extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder().authModule(new AuthModule()).build();
    }

    /**
     * Get app component
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return this.appComponent;
    }
}