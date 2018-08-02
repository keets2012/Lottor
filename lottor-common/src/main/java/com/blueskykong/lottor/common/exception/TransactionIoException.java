package com.blueskykong.lottor.common.exception;

public class TransactionIoException extends RuntimeException {

    private static final long serialVersionUID = 6508064607297986329L;

    public TransactionIoException(String message) {
        super(message);
    }

    public TransactionIoException(Throwable e) {
        super(e);
    }
}
