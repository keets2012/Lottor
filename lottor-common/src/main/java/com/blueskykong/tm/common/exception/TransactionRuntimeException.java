package com.blueskykong.tm.common.exception;

public class TransactionRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -1949770547060521702L;

    public TransactionRuntimeException() {
    }

    public TransactionRuntimeException(String message) {
        super(message);
    }

    public TransactionRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionRuntimeException(Throwable cause) {
        super(cause);
    }
}
