
package com.blueskykong.tm.core.spi;


import com.blueskykong.tm.common.bean.TransactionRecover;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * @author keets
 */
public interface TransactionOperateRepository {

    /**
     * 创建本地事务对象
     *
     * @param transactionRecover 事务对象
     * @return rows
     */
    int create(TransactionRecover transactionRecover);

    /**
     * 删除对象
     *
     * @param id 事务对象id
     * @return rows
     */
    int remove(String id);


    /**
     * 更新数据
     *
     * @param transactionRecover 事务对象
     * @return rows 1 成功 0 失败 失败需要抛异常
     * @throws TransactionRuntimeException 更新异常
     */
    int update(TransactionRecover transactionRecover) throws TransactionRuntimeException;

    /**
     * 根据id获取对象
     *
     * @param id 主键id
     * @return TransactionRecover
     */
    TransactionRecover findById(String id);

    /**
     * 获取需要提交的事务
     *
     * @return List<TransactionRecover>
     */
    List<TransactionRecover> listAll();


    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行
     *
     * @param date 延迟后的时间
     * @return List<TransactionRecover>
     */
    List<TransactionRecover> listAllByDelay(Date date);


    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     * @throws Exception 初始化异常信息
     */
    void init(String modelName, TxConfig txConfig) throws Exception;

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    String getScheme();


    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    void setSerializer(ObjectSerializer objectSerializer);


    long saveMsg(TransactionMsg msg);

    TransactionMsg findMsgById(String id);

    int updateMsg(TransactionMsg msg);
}
