package com.blueskykong.tm.common.config;

import lombok.Data;

@Data
public class TxRedisConfig {

    private Boolean cluster = false;

    /**
     * 集群url   ip:port;ip:port
     */
    private String clusterUrl;

    private String hostName;
    private int port;
    private String password;
    private int maxTotal = 8;
    private int maxIdle = 8;
    private int minIdle = 0;
    private long maxWaitMillis = -1L;
    private long minEvictableIdleTimeMillis = 1800000L;
    private long softMinEvictableIdleTimeMillis = 1800000L;
    private int numTestsPerEvictionRun = 3;
    private Boolean testOnCreate = false;
    private Boolean testOnBorrow = false;
    private Boolean testOnReturn = false;
    private Boolean testWhileIdle = false;
    private long timeBetweenEvictionRunsMillis = -1L;
    private Boolean blockWhenExhausted = true;
    private int timeOut = 10000;

}


