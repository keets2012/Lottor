package com.blueskykong.lottor.server.config;


public interface Constant {

    String APPLICATION_NAME = "lottor";

    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";
}
