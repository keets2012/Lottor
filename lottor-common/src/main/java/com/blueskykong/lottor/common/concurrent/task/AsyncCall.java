package com.blueskykong.lottor.common.concurrent.task;

@FunctionalInterface
public interface AsyncCall {

    /**
     * 回调处理
     *
     * @param objects 参数
     * @return Object
     * @throws Throwable 异常
     */
    Object callBack(Object... objects) throws Throwable;
}
