package com.blueskykong.tm.common.bean;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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


    private TransactionInvocation transactionInvocation;


    private int status;


}
