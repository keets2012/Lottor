package com.blueskykong.tm.server.config;

import lombok.Data;

@Data
public class NettyConfig {

    /**
     * 启动服务端口
     */
    private int port;

    /**
     * 最大线程数
     */
    private int maxThreads = Runtime.getRuntime().availableProcessors() << 2;


    /**
     * 客户端与服务端链接数
     */
    private int maxConnection = 50;

    /**
     * 序列化方式
     */
    private String serialize;

    /**
     * 与客户端通信最大延迟时间，超过该时间就会自动唤醒线程,返回失败  单位：秒）
     */
    private int delayTime;

    /**
     * 与客户端保持通讯的心跳时间（单位：秒）
     */
    private int heartTime;

    private Boolean onEpoll = false;

    private Boolean check = true;

    private int checkPeriod = 45;

    private int initDelay = 30;
}

