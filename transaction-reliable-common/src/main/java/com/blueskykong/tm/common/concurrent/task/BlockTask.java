package com.blueskykong.tm.common.concurrent.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockTask {

    private Lock lock;
    private Condition condition;
    private CountDownLatch countDownLatch;

    private AsyncCall asyncCall;

    /**
     * 是否被唤醒
     */
    private volatile static boolean Notify = false;

    /**
     * 是否被唤醒
     */
    private volatile static boolean remove = false;

    /**
     * 唯一标示key
     */
    private String key;

    /**
     * 数据状态用于业务处理
     */
    private int state = 0;


    public BlockTask() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void countDownAwait() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            countDownLatch.countDown();
        }
    }

    public void countDown() {
        countDownLatch.countDown();
    }


    public void signal() {
        try {
            lock.lock();
            Notify = true;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }


    public void await() {
        try {
            lock.lock();
            condition.await();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }


    public void setAsyncCall(AsyncCall asyncCall) {
        this.asyncCall = asyncCall;
    }

    public AsyncCall getAsyncCall() {
        return asyncCall;
    }

    public boolean isNotify() {
        return Notify;
    }

    public static void setNotify(boolean notify) {
        Notify = notify;
    }

    public static boolean isRemove() {
        return remove;
    }

    public static void setRemove(boolean remove) {
        BlockTask.remove = remove;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
