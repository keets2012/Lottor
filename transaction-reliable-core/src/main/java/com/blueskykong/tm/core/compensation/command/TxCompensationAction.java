
package com.blueskykong.tm.core.compensation.command;


import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.enums.CompensationActionEnum;

import java.io.Serializable;


public class TxCompensationAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;

    public CompensationActionEnum getCompensationActionEnum() {
        return compensationActionEnum;
    }

    public void setCompensationActionEnum(CompensationActionEnum compensationActionEnum) {
        this.compensationActionEnum = compensationActionEnum;
    }

    public TransactionRecover getTransactionRecover() {
        return transactionRecover;
    }

    public void setTransactionRecover(TransactionRecover transactionRecover) {
        this.transactionRecover = transactionRecover;
    }


}
