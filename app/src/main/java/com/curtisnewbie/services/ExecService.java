package com.curtisnewbie.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

/**
 * ------------------------------------
 * <p>
 * Author: Yongjie Zhuang
 * <p>
 * ------------------------------------
 * <p>
 * Class that internally has an {@code ExecutorService} to execute submitted
 * Runnable in a FIFO order. This class is a Singleton.
 * </p>
 */
@Singleton
public class ExecService {
    private int NUM_OF_THREADS = 4;
    private ExecutorService es;

    public ExecService() {
        es = Executors.newFixedThreadPool(NUM_OF_THREADS);
    }

    /**
     * Submit a Runnable to be executed by an {@code ExecutorService}
     *
     * @param r runnable
     */
    public void submit(Runnable r) {
        this.es.submit(r);
    }
}
