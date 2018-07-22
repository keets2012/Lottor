package com.blueskykong.tm.core.compensation.command;


import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.enums.CompensationActionEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class TxOperateAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;


}
