
package com.blueskykong.tm.common.holder;


public class ConfigUtils {

    public static String getString(String filePath, String key) {
        ConfigHelper helper = new ConfigHelper(filePath);
        return helper.getStringValue(key);
    }

    public static int getInt(String filePath, String key) {
        ConfigHelper helper = new ConfigHelper(filePath);
        return helper.getIntValue(key);
    }

    public static void setProperty(String filePath, String key, Object val) {
        ConfigHelper helper = new ConfigHelper(filePath);
        helper.setProperty(key, val);
    }

}
