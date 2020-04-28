package com.curtisnewbie.services;

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
 * Module for injecting dependencies related to multithreading and concurrency
 */
@Module
public class ConcurrencyModule {

    @Provides
    @Singleton
    ExecService providesThreadManager() {
        return new ExecService();
    }
}
