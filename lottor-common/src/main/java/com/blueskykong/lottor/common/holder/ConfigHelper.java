package com.blueskykong.lottor.common.holder;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class ConfigHelper {


    private PropertiesConfiguration propertiesConfiguration = null;

    public String getStringValue(String key) {
        return propertiesConfiguration.getString(key);
    }

    public void setProperty(String key, Object val) {
        propertiesConfiguration.setProperty(key, val);
        try {
            propertiesConfiguration.save();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int getIntValue(String key) {
        return propertiesConfiguration.getInt(key);
    }

    public float getFloatValue(String key) {
        return propertiesConfiguration.getFloat(key);
    }

    public ConfigHelper(String propertyPath) {
        try {
            propertiesConfiguration = new PropertiesConfiguration(propertyPath);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Please configure check  file: " + propertyPath);
        }
    }

}
