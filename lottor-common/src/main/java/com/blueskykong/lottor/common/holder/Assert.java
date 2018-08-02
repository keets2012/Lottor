package com.blueskykong.lottor.common.holder;


import com.blueskykong.lottor.common.exception.TransactionRuntimeException;

public class Assert {

    private Assert() {

    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new TransactionRuntimeException(message);
        }
    }

    public static void notNull(Object obj) {
        if (obj == null) {
            throw new TransactionRuntimeException("argument invalid,Please check");
        }
    }

    public static void checkConditionArgument(boolean condition, String message) {
        if (!condition) {
            throw new TransactionRuntimeException(message);
        }
    }

}
