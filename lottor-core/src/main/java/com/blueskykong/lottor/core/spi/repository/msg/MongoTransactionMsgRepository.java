package com.blueskykong.lottor.core.spi.repository.msg;

import com.blueskykong.lottor.common.config.TxConfig;
import com.blueskykong.lottor.common.entity.TransactionMsg;
import com.blueskykong.lottor.common.serializer.ObjectSerializer;
import com.blueskykong.lottor.core.spi.TransactionMsgRepository;


public class MongoTransactionMsgRepository implements TransactionMsgRepository {

    @Override
    public long save(TransactionMsg msg) {
        return 0;
    }

    @Override
    public TransactionMsg findById(String id) {
        return null;
    }

    @Override
    public int update(TransactionMsg msg) {
        return 0;
    }

    /**
     * 初始化操作
     *
     * @param modelName 模块名称
     * @param txConfig  配置信息
     * @throws Exception 初始化异常信息
     */
    @Override
    public void init(String modelName, TxConfig txConfig) throws Exception {

    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return null;
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {

    }
}
