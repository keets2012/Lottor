package com.blueskykong.lottor.core.spi;

import com.blueskykong.lottor.common.config.TxConfig;
import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.serializer.ObjectSerializer;

/**
 * @data 2018/7/15.
 */
public interface TransactionMsgRepository {

    long save(TransactionMsg msg);

    TransactionMsg findById(String id);

    int update(TransactionMsg msg);

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
}
