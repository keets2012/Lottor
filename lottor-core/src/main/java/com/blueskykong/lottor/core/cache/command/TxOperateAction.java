package com.blueskykong.lottor.core.cache.command;

import com.blueskykong.lottor.common.bean.TransactionRecover;
import com.blueskykong.lottor.common.enums.CompensationActionEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class TxOperateAction implements Serializable {

    private static final long serialVersionUID = 7474184793963072848L;


    private CompensationActionEnum compensationActionEnum;


    private TransactionRecover transactionRecover;


}
