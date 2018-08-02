package com.blueskykong.lottor.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum CompensationCacheTypeEnum {


    REDIS("redis"),

    MONGODB("mongodb");

    private String compensationCacheType;

    CompensationCacheTypeEnum(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }


    public static CompensationCacheTypeEnum acquireCompensationCacheType(String compensationCacheType) {
        Optional<CompensationCacheTypeEnum> serializeProtocolEnum =
                Arrays.stream(CompensationCacheTypeEnum.values())
                        .filter(v -> Objects.equals(v.getCompensationCacheType(), compensationCacheType))
                        .findFirst();
        return serializeProtocolEnum.orElse(CompensationCacheTypeEnum.REDIS);
    }


    public String getCompensationCacheType() {
        return compensationCacheType;
    }


    public void setCompensationCacheType(String compensationCacheType) {
        this.compensationCacheType = compensationCacheType;
    }
}
