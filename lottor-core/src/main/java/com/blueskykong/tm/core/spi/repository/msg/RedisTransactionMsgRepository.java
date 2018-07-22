package com.blueskykong.tm.core.spi.repository.msg;

import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.config.TxRedisConfig;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.CompensationCacheTypeEnum;
import com.blueskykong.tm.common.exception.TransactionIoException;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.helper.RedisHelper;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.holder.RepositoryPathUtils;
import com.blueskykong.tm.common.holder.TransactionRecoverUtils;
import com.blueskykong.tm.common.jedis.JedisClient;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.spi.TransactionMsgRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @data 2018/7/15.
 */
public class RedisTransactionMsgRepository implements TransactionMsgRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisTransactionMsgRepository.class);


    private ObjectSerializer objectSerializer;

    private String keyName = "txmsg";

    private JedisClient jedisClient;


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
    public void init(String modelName, TxConfig txConfig) {
        return;
    }


    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return CompensationCacheTypeEnum.REDIS.getCompensationCacheType();
    }

    /**
     * 设置序列化信息
     *
     * @param objectSerializer 序列化实现
     */
    @Override
    public void setSerializer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }
}
