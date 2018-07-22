package com.blueskykong.tm.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


public enum RejectedPolicyTypeEnum {
    /**
     * Abort policy rejected policy type enum.
     */
    ABORT_POLICY("Abort"),
    /**
     * Blocking policy rejected policy type enum.
     */
    BLOCKING_POLICY("Blocking"),
    /**
     * Caller runs policy rejected policy type enum.
     */
    CALLER_RUNS_POLICY("CallerRuns"),
    /**
     * Discarded policy rejected policy type enum.
     */
    DISCARDED_POLICY("Discarded"),
    /**
     * Rejected policy rejected policy type enum.
     */
    REJECTED_POLICY("Rejected");

    private String value;

    RejectedPolicyTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * From string rejected policy type enum.
     *
     * @param value the value
     * @return the rejected policy type enum
     */
    public static RejectedPolicyTypeEnum fromString(String value) {
        Optional<RejectedPolicyTypeEnum> rejectedPolicyTypeEnum =
                Arrays.stream(RejectedPolicyTypeEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return rejectedPolicyTypeEnum.orElse(RejectedPolicyTypeEnum.ABORT_POLICY);
    }

    @Override
    public String toString() {
        return value;
    }
}

