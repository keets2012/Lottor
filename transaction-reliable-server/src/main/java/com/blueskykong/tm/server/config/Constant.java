package com.blueskykong.tm.server.config;


public interface Constant {

    String APPLICATION_NAME = "tm-server";

    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";

    String HTTP_COMMIT = "http://%s/tx/manager/HTTP_COMMIT";

    String HTTP_ROLLBACK = "http://%s/tx/manager/HTTP_ROLLBACK";


}
