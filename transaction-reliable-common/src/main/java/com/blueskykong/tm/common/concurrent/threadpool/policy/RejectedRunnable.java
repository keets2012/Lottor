package com.blueskykong.tm.common.concurrent.threadpool.policy;

/**
 * @author keets
 */
public interface RejectedRunnable extends Runnable {

    /**
     * 线程池拒绝策略
     */
    void rejected();
}
