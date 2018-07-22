package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum MethodNameEnum {

    /**
     * notice: MethodNameEnum 由两部分组成，服务名和方法名
     */
    CONSUMER_TEST("consumer-test"),
    AFFAIR_MODIFY_AFFAIR("modifyAffair");

    String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    MethodNameEnum(String method) {
        this.method = method;
    }

    /**
     * From string method type enum.
     *
     * @param value the value
     * @return the method enum
     */
    public static MethodNameEnum fromString(String value) {
        Optional<MethodNameEnum> serviceNameEnum =
                Arrays.stream(MethodNameEnum.values())
                        .filter(v -> Objects.equals(v.getMethod(), value))
                        .findFirst();
        return serviceNameEnum.orElseThrow(() -> new IllegalArgumentException("method is illegal!"));
    }

    @Override
    public String toString() {
        return this.method;
    }
}
