package com.blueskykong.lottor.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lottor.core")
public class TxConfig {

    private String serializer = "kryo";

    private String nettySerializer = "kryo";

    private int delayTime = 30;

    private int transactionThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    private int nettyThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    private int heartTime = 10;

    private String rejectPolicy = "Abort";

    private String blockingQueueType = "Linked";

    private Boolean cache = false;

    private String cacheType;

    private int cacheQueueMax = 5000;

    private int cacheThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    private int cacheRecoverTime = 60;

    /**
     * 刷新manager的周期
     * 单位：s
     */
    private int refreshInterval = 60;


    private int retryMax = 5;

    /**
     * 客户端失联，重试间隔
     * 单位：s
     */
    private int retryInterval = 6;


    private int recoverDelayTime = 60;


    private String txManagerId = "lottor";

    private TxMongoConfig txMongoConfig;

    private TxRedisConfig txRedisConfig;

    private boolean onEpoll = false;

}
