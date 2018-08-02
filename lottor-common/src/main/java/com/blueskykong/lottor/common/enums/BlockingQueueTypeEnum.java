
package com.blueskykong.lottor.common.enums;


import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;


public enum BlockingQueueTypeEnum {


    /**
     * Linked blocking queue blocking queue type enum.
     */
    LINKED_BLOCKING_QUEUE("Linked"),
    /**
     * Array blocking queue blocking queue type enum.
     */
    ARRAY_BLOCKING_QUEUE("Array"),
    /**
     * Synchronous queue blocking queue type enum.
     */
    SYNCHRONOUS_QUEUE("SynchronousQueue");

    private String value;

    BlockingQueueTypeEnum(String value) {
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
     * From string blocking queue type enum.
     *
     * @param value the value
     * @return the blocking queue type enum
     */
    public static BlockingQueueTypeEnum fromString(String value) {
        Optional<BlockingQueueTypeEnum> blockingQueueTypeEnum =
                Arrays.stream(BlockingQueueTypeEnum.values())
                        .filter(v -> Objects.equals(v.getValue(), value))
                        .findFirst();
        return blockingQueueTypeEnum.orElse(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE);
    }

    @Override
    public String toString() {
        return value;
    }
}

