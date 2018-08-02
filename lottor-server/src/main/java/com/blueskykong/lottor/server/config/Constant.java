package com.blueskykong.lottor.server.config;


public interface Constant {

    String APPLICATION_NAME = "lotor";

    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";

    String HTTP_COMMIT = "http://%s/tx/manager/HTTP_COMMIT";

    String HTTP_ROLLBACK = "http://%s/tx/manager/HTTP_ROLLBACK";


}
