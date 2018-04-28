package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author keets
 */
public enum ServiceNameEnum {
    AFFAIR("affair"),
    MATERIAL("material"),
    TSS("tss-app");

    String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    ServiceNameEnum(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * From string serviceName type enum.
     *
     * @param value the value
     * @return the serviceName enum
     */
    public static ServiceNameEnum fromString(String value) {
        Optional<ServiceNameEnum> serviceNameEnum =
                Arrays.stream(ServiceNameEnum.values())
                        .filter(v -> Objects.equals(v.getServiceName(), value))
                        .findFirst();
        return serviceNameEnum.orElseThrow(() -> new IllegalArgumentException("serviceName is illegal!"));
    }

    @Override
    public String toString() {
        return serviceName;
    }
}
