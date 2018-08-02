package com.blueskykong.lottor.common.exception;

public class TransactionException extends Exception {
    private static final long serialVersionUID = -948934144333391208L;

    public TransactionException() {
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
