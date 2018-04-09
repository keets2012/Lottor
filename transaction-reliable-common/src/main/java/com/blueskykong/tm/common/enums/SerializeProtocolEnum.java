package com.blueskykong.tm.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum SerializeProtocolEnum {

    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk"),

    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo"),

    /**
     * Hessian serialize protocol enum.
     */
    HESSIAN("hessian"),

    /**
     * Protostuff serialize protocol enum.
     */
    PROTOSTUFF("protostuff");

    private String serializeProtocol;

    SerializeProtocolEnum(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }

    /**
     * Acquire serialize protocol serialize protocol enum.
     *
     * @param serializeProtocol the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeProtocolEnum acquireSerializeProtocol(String serializeProtocol) {
        Optional<SerializeProtocolEnum> serializeProtocolEnum =
                Arrays.stream(SerializeProtocolEnum.values())
                        .filter(v -> Objects.equals(v.getSerializeProtocol(), serializeProtocol))
                        .findFirst();
        return serializeProtocolEnum.orElse(SerializeProtocolEnum.KRYO);

    }

    /**
     * Gets serialize protocol.
     *
     * @return the serialize protocol
     */
    public String getSerializeProtocol() {
        return serializeProtocol;
    }

    /**
     * Sets serialize protocol.
     *
     * @param serializeProtocol the serialize protocol
     */
    public void setSerializeProtocol(String serializeProtocol) {
        this.serializeProtocol = serializeProtocol;
    }


}
