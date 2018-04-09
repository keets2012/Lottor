package com.blueskykong.tm.core.concurrent.threadlocal;


/**
 * @author keets
 */
public class TxTransactionLocal {

    private static final TxTransactionLocal TX_TRANSACTION_LOCAL = new TxTransactionLocal();

    private TxTransactionLocal() {

    }

    public static TxTransactionLocal getInstance() {
        return TX_TRANSACTION_LOCAL;
    }


    private static final ThreadLocal<String> CURRENT_LOCAL = new ThreadLocal<>();


    public void setTxGroupId(String txGroupId) {
        CURRENT_LOCAL.set(txGroupId);
    }

    public String getTxGroupId() {
        return CURRENT_LOCAL.get();
    }


    public void removeTxGroupId() {
        CURRENT_LOCAL.remove();
    }


}
