package com.blueskykong.tm.common.bean.adapter;

import com.blueskykong.tm.common.entity.TransactionMsg;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author keets
 */
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
