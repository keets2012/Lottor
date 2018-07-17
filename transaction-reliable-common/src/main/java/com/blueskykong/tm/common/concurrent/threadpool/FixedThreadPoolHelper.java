package com.blueskykong.tm.common.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author keets
 */
public class FixedThreadPoolHelper {

    private static final FixedThreadPoolHelper INSTANCE = new FixedThreadPoolHelper();

    /**
     * 线程数量大小
     */
    private static final int DEFAULT_THREAD_MAX = Runtime.getRuntime().availableProcessors();


    private FixedThreadPoolHelper() {
    }

    public static FixedThreadPoolHelper getInstance() {
        return INSTANCE;
    }


    public ExecutorService getExecutorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("search-thread-%d")
                .setDaemon(true).build();
        return new ThreadPoolExecutor(DEFAULT_THREAD_MAX, DEFAULT_THREAD_MAX,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());

    }

    public int getDefaultThreadMax() {
        return DEFAULT_THREAD_MAX;
    }
}
