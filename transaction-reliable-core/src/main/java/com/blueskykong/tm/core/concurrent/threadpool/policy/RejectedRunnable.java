package com.blueskykong.tm.core.concurrent.threadpool.policy;

/**
 * @author keets
 */
public interface RejectedRunnable extends Runnable {

    /**
     * 线程池拒绝策略
     */
    void rejected();
}
