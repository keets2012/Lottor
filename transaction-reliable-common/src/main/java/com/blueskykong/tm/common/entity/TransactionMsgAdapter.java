package com.blueskykong.tm.common.entity;

import com.blueskykong.tm.common.enums.ConsumedStatus;
import com.blueskykong.tm.common.holder.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author keets
 */
@Data
@NoArgsConstructor
public class TransactionMsgAdapter implements Serializable {

    private static final long serialVersionUID = -6333084714739703982L;
    /**
     * 用于消息的追溯
     */
    private String groupId;

    /**
     * 子任务id
     */
    @NonNull
    private String subTaskId;

    /**
     * 源服务，即调用发起方
     */
    private String source;

    /**
     * 目标方
     */
    private String target;

    /**
     * 执行的方法，适配成枚举
     */
    private String method;

    /**
     * 参数，即要传递的内容，可以为null
     */
    private byte[] args;

    private String argsType;
    /**
     * 创建时间
     */
    private Long createTime = Timestamp.valueOf(DateUtils.getCurrentDateTime()).getTime();

    /**
     * 操作结果信息
     */
    private String message;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 是否消费，默认为否
     *
     * {@linkplain ConsumedStatus}
     */
    private int consumed = ConsumedStatus.UNCONSUMED.getStatus();


    public static TransactionMsg convert(TransactionMsgAdapter transactionMsgAdapter) {

        TransactionMsg transactionMsg = new TransactionMsg.Builder()
                .setSource(transactionMsgAdapter.getSource())
                .setMethod(transactionMsgAdapter.getMethod())
                .setGroupId(transactionMsgAdapter.getGroupId())
                .setSubTaskId(transactionMsgAdapter.getSubTaskId())
                .setTarget(transactionMsgAdapter.getTarget())
                .setConsumed(transactionMsgAdapter.getConsumed())
                .build();

        return transactionMsg;

    }

    public static class Builder {
        private TransactionMsgAdapter transactionMsg = new TransactionMsgAdapter();

        public Builder setGroupId(String groupId) {
            if (StringUtils.isNoneEmpty(groupId)) {
                transactionMsg.setGroupId(groupId);
            }
            return this;
        }

        public Builder setSubTaskId(String subTaskId) {
            if (StringUtils.isNoneEmpty(subTaskId)) {
                transactionMsg.setSubTaskId(subTaskId);
            }
            return this;
        }

        public Builder setSource(String source) {
            if (StringUtils.isNoneEmpty(source)) {
                transactionMsg.setSource(source);
            }
            return this;
        }

        public Builder setTarget(String target) {
            if (StringUtils.isNoneEmpty(target)) {
                transactionMsg.setTarget(target);
            }
            return this;
        }

        public Builder setMethod(String method) {
            if (StringUtils.isNoneEmpty(method)) {
                transactionMsg.setMethod(method);
            }
            return this;
        }

        public Builder setCreateTime(Long createTime) {
            transactionMsg.setCreateTime(createTime);
            return this;
        }

        public Builder setUpdateTime(Long updateTime) {
            transactionMsg.setUpdateTime(updateTime);
            return this;
        }

        public Builder setArgs(byte[] args) {
            if (args != null) {
                transactionMsg.setArgs(args);
            }
            return this;
        }

        public Builder setMessage(String message) {
            if (StringUtils.isNotBlank(message)) {
                transactionMsg.setMessage(message);
            }
            return this;
        }

        public Builder setConsumed(int consumed) {
            transactionMsg.setConsumed(consumed);
            return this;
        }

        public Builder setArgsType(String type) {
            transactionMsg.setArgsType(type);
            return this;
        }

        public TransactionMsgAdapter build() {
            return transactionMsg;
        }
    }
}
