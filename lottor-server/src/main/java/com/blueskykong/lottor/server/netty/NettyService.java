package com.blueskykong.lottor.server.netty;


public interface NettyService {


    /**
     * 启动netty服务
     *
     * @throws InterruptedException 异常
     */
    void start() throws InterruptedException;


    /**
     * 关闭服务
     */
    void stop();


}
