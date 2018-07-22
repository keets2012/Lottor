package com.blueskykong.tm.common.bean;


import com.blueskykong.tm.common.entity.TransactionMsg;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TransactionRecover implements Serializable {

    private static final long serialVersionUID = -3262858695515766275L;

    private String id;

    private int retriedCount = 0;

    private Date createTime = new Date();

    private Date lastTime = new Date();


    private int version = 1;

    private String groupId;


    private String taskId;


    private List<TransactionMsg> transactionMsgs;

    private int status;


}
