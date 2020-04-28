package com.curtisnewbie.services;

import com.curtisnewbie.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Component class for Dependency Injection. A component is just like an Injector. Activities that
 * are created by the Application, is passed into the methods in this class, where the dependencies
 * in the activity are injected.
 */
@Singleton
@Component(modules = {AuthModule.class})
public interface AppComponent {

    /**
     * injecting dependencies in MainActivity
     */
    void inject(MainActivity activity);
}