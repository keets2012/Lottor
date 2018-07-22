package com.blueskykong.tm.common.holder;


import com.blueskykong.tm.common.constant.CommonConstant;

public class DbTypeUtils {

    public static String buildByDriverClassName(String driverClassName) {
        String dbType = "mysql";
        if (driverClassName.contains(CommonConstant.DB_MYSQL)) {
            dbType = "mysql";
        } else if (driverClassName.contains(CommonConstant.DB_SQLSERVER)) {
            dbType = "sqlserver";
        } else if (driverClassName.contains(CommonConstant.DB_ORACLE)) {
            dbType = "oracle";
        }
        return dbType;
    }


}
