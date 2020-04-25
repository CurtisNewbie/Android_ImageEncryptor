package com.curtisnewbie.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class that internally has an {@code ExecutorService} to execute submitted Runnable in a FIFO order.
 * This class is a Singleton.
 */
public class ThreadManager {
    private int NUM_OF_THREADS = 4;
    private static final ThreadManager tm = new ThreadManager();
    private ExecutorService es;

    private ThreadManager() {
        es = Executors.newFixedThreadPool(NUM_OF_THREADS);
    }

    public static ThreadManager getThreadManager() {
        return tm;
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
