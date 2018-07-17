package com.blueskykong.tm.common.constant;

public interface CommonConstant {


    String REDIS_PRE_FIX = "transaction:group:%s";

    String REDIS_KEYS = "transaction:group:*";


    String REDIS_KEY_SET = "transaction:group";

    double REDIS_SCOPE = 10.0;

    String PATH_SUFFIX = "/tx";

    String DB_SUFFIX = "tx_";

    String RECOVER_REDIS_KEY_PRE = "transaction:recover:%s";


    String DB_MYSQL = "mysql";

    String DB_SQLSERVER = "sqlserver";

    String DB_ORACLE = "oracle";

    String COMPENSATE_KEY = "COMPENSATE";

    String COMPENSATE_ID = "COMPENSATE_ID";

    String TX_TRANSACTION_GROUP = "TX_TRANSACTION_GROUP";

    String TX_MANAGER_PRE = "/tx/manager";

    String LOAD_TX_MANAGER_SERVICE_URL = "/loadTxManagerService";

    String FIND_SERVER = "/findTxManagerServer";

    int HEAD_DATA = 0X76;
}
