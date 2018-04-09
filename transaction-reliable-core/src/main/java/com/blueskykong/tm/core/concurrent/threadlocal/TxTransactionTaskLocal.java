package com.blueskykong.tm.core.concurrent.threadlocal;


/**
 * @author keets
 */
public class TxTransactionTaskLocal {

    private static final TxTransactionTaskLocal TX_TRANSACTION_LOCAL = new TxTransactionTaskLocal();

    private TxTransactionTaskLocal() {

    }

    public static TxTransactionTaskLocal getInstance() {
        return TX_TRANSACTION_LOCAL;
    }


    private static final ThreadLocal<String> CURRENT_LOCAL = new ThreadLocal<>();


    public void setTxTaskId(String txTaskId) {
        CURRENT_LOCAL.set(txTaskId);
    }

    public String getTxTaskId() {
        return CURRENT_LOCAL.get();
    }


    public void removeTxTaskId() {
        CURRENT_LOCAL.remove();
    }


}
