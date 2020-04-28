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
 * Module for Injecting Authorisation and Authorisation related dependencies.
 */
@Module
public class AuthModule {

    @Singleton
    @Provides
    AuthService provideAuthService() {
        return new AuthService();
    }
}
