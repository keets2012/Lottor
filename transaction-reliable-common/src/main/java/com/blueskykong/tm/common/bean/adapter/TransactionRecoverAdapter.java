package com.blueskykong.tm.common.bean.adapter;

import lombok.Data;

import java.util.Date;

@Data
public class TransactionRecoverAdapter {


    private String transId;

    private int retriedCount = 0;

    private Date createTime = new Date();

    private Date lastTime = new Date();

    private int version = 1;

    private String groupId;

    private String taskId;

    private byte[] contents;

    private int status;

    private String targetClass;

    private String targetMethod;

}
