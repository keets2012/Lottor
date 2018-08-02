package com.blueskykong.lottor.common.holder;


import com.blueskykong.lottor.common.constant.CommonConstant;

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
