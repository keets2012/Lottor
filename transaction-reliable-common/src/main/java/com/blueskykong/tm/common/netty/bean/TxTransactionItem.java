package com.blueskykong.tm.common.netty.bean;

import com.blueskykong.tm.common.entity.TransactionMsgAdapter;
import com.blueskykong.tm.common.enums.TransactionStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class TxTransactionItem implements Serializable {

    private static final long serialVersionUID = -983809184773470584L;

    /**
     * taskKey
     */
    private String taskKey;

    /**
     * 事务状态 {@link TransactionStatusEnum}
     */
    private int status;

    /**
     * 模块信息
     */
    private String modelName;

    /**
     * tm 的域名信息
     */
    private String tmDomain;

    /**
     * 事务组id
     */
    private String txGroupId;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 事务最大等待时间 单位秒
     */
    private Integer waitMaxTime;

    /**
     * 耗时 秒
     */
    private Long consumeTime;

    /**
     * 服务消息
     */
    private List<TransactionMsgAdapter> msgs;

    /**
     * 操作结果信息
     */
    private Object message;


}
