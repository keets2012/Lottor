package com.blueskykong.tm.common.holder;

import com.blueskykong.tm.common.constant.CommonConstant;

public class RepositoryPathUtils {


    public static String buildFilePath(String applicationName) {
        return String.join("/", CommonConstant.PATH_SUFFIX, applicationName.replaceAll("-", "_"));
    }


    public static String buildDbTableName(String applicationName) {
        return CommonConstant.DB_SUFFIX + applicationName.replaceAll("-", "_");
    }


    public static String buildMongoTableName(String applicationName) {
        return CommonConstant.DB_SUFFIX + applicationName.replaceAll("-", "_");
    }

    public static String buildRedisKey(String applicationName) {
        return String.format(CommonConstant.RECOVER_REDIS_KEY_PRE, applicationName);
    }

    public static String buildZookeeperPath(String applicationName) {
        return String.join("_", CommonConstant.PATH_SUFFIX, applicationName);
    }


}
