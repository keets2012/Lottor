package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum CompensationCacheTypeEnum {


    DB("db"),

    FILE("file"),

    REDIS("redis"),

    MONGODB("mongodb"),

    ZOOKEEPER("zookeeper");

    private String compensationCacheType;

    CompensationCacheTypeEnum(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }


    public static CompensationCacheTypeEnum acquireCompensationCacheType(String compensationCacheType) {
        Optional<CompensationCacheTypeEnum> serializeProtocolEnum =
                Arrays.stream(CompensationCacheTypeEnum.values())
                        .filter(v -> Objects.equals(v.getCompensationCacheType(), compensationCacheType))
                        .findFirst();
        return serializeProtocolEnum.orElse(CompensationCacheTypeEnum.DB);
    }


    public String getCompensationCacheType() {
        return compensationCacheType;
    }


    public void setCompensationCacheType(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }
}
