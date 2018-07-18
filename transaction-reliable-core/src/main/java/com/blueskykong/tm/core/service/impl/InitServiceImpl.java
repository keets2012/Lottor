package com.blueskykong.tm.core.service.impl;

import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.enums.CompensationCacheTypeEnum;
import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.exception.TransactionRuntimeException;
import com.blueskykong.tm.common.helper.SpringBeanUtils;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.holder.ServiceBootstrap;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.compensation.TxOperateService;
import com.blueskykong.tm.core.netty.NettyClientService;
import com.blueskykong.tm.core.service.InitService;
import com.blueskykong.tm.core.spi.TransactionOperateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

public class InitServiceImpl implements InitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitServiceImpl.class);


    private final NettyClientService nettyClientService;

    private final TxOperateService txOperateService;

    @Autowired
    public InitServiceImpl(NettyClientService nettyClientService, TxOperateService txOperateService) {
        this.nettyClientService = nettyClientService;
        this.txOperateService = txOperateService;
    }

    @Override
    public void initialization(TxConfig txConfig) {
        try {
            loadSpi(txConfig);
            nettyClientService.start(txConfig);
            txOperateService.start(txConfig);
        } catch (Exception e) {
            throw new TransactionRuntimeException("补偿配置异常：" + e.getMessage());
        }
        LogUtil.info(LOGGER, () -> "分布式补偿事务初始化成功！");
    }

    /**
     * 根据配置文件初始化spi
     *
     * @param txConfig 配置信息
     */
    private void loadSpi(TxConfig txConfig) {

        //spi  serialize
        final SerializeProtocolEnum serializeProtocolEnum =
                SerializeProtocolEnum.acquireSerializeProtocol(txConfig.getSerializer());
        final ServiceLoader<ObjectSerializer> objectSerializers = ServiceBootstrap.loadAll(ObjectSerializer.class);

        final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false)
                .filter(objectSerializer ->
                        Objects.equals(objectSerializer.getScheme(), serializeProtocolEnum.getSerializeProtocol())).findFirst();

        //spi  RecoverRepository support
        final CompensationCacheTypeEnum compensationCacheTypeEnum = CompensationCacheTypeEnum.acquireCompensationCacheType(txConfig.getCompensationCacheType());
        final ServiceLoader<TransactionOperateRepository> recoverRepositories = ServiceBootstrap.loadAll(TransactionOperateRepository.class);

        final Optional<TransactionOperateRepository> repositoryOptional =
                StreamSupport.stream(recoverRepositories.spliterator(), false)
                        .filter(recoverRepository ->
                                Objects.equals(recoverRepository.getScheme(), compensationCacheTypeEnum.getCompensationCacheType()))
                        .findFirst();
        //将compensationCache实现注入到spring容器
        repositoryOptional.ifPresent(repository -> {
            serializer.ifPresent(repository::setSerializer);
            SpringBeanUtils.getInstance().registerBean(TransactionOperateRepository.class.getName(), repository);
        });
    }
}
