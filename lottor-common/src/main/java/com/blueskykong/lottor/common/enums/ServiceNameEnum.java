package com.blueskykong.lottor.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum ServiceNameEnum {
    AFFAIR("affair", "tx-affair"),
    MATERIAL("material", "tx-material"),
    TSS("tss-app", "tx-tss-app"),
    TEST_PRODUCER("producer", "tx-producer"),
    TEST("test", "tx-test");

    String serviceName;

    String topic;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    ServiceNameEnum(String serviceName, String topic) {
        this.serviceName = serviceName;
        this.topic = topic;
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
