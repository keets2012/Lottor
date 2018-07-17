package com.blueskykong.tm.core.compensation;


import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.core.compensation.command.TxOperateAction;

public interface TxOperateService {

    /**
     * 补偿操作
     */
    void compensate();


    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     *
     * @param txConfig 配置信息
     * @throws Exception 异常信息
     */
    void start(TxConfig txConfig) throws Exception;

    /**
     * 保存补偿事务信息
     *
     * @param transactionRecover 实体对象
     * @return 主键id
     */
    String saveTransactionRecover(TransactionRecover transactionRecover);


    /**
     * 删除补偿事务信息
     *
     * @param id 主键id
     * @return true成功 false 失败
     */
    boolean removeTransactionRecover(String id);


    TransactionRecover findTransactionRecover(String id);
    /**
     * 更新
     *
     * @param transactionRecover 实体对象
     */
    void updateTransactionRecover(TransactionRecover transactionRecover);


    /**
     * 提交补偿
     *
     * @param txOperateAction 补偿命令
     * @return true 成功
     */
    Boolean submit(TxOperateAction txOperateAction);

    String saveTransactionMsg(TransactionMsg msg);

    void updateTransactionMsg(TransactionMsg msg);

    TransactionMsg findTransactionMsg(String id);


}
