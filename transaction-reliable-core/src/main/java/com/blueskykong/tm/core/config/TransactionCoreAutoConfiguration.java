package com.blueskykong.tm.core.config;

import com.blueskykong.tm.common.concurrent.threadpool.TransactionThreadPool;
import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.holder.ServiceBootstrap;
import com.blueskykong.tm.common.serializer.KryoSerializer;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.bootstrap.TxTransactionBootstrap;
import com.blueskykong.tm.core.bootstrap.TxTransactionInitialize;
import com.blueskykong.tm.core.compensation.TxOperateService;
import com.blueskykong.tm.core.compensation.command.TxOperateCommand;
import com.blueskykong.tm.core.compensation.impl.TxOperateServiceImpl;
import com.blueskykong.tm.core.interceptor.TxTransactionInterceptor;
import com.blueskykong.tm.core.netty.NettyClientService;
import com.blueskykong.tm.core.netty.handler.NettyClientHandlerInitializer;
import com.blueskykong.tm.core.netty.handler.NettyClientMessageHandler;
import com.blueskykong.tm.core.netty.impl.NettyClientServiceImpl;
import com.blueskykong.tm.core.service.AspectTransactionService;
import com.blueskykong.tm.core.service.ExternalNettyService;
import com.blueskykong.tm.core.service.InitService;
import com.blueskykong.tm.core.service.ModelNameService;
import com.blueskykong.tm.core.service.TxManagerMessageService;
import com.blueskykong.tm.core.service.TxTransactionFactoryService;
import com.blueskykong.tm.core.service.TxTransactionHandler;
import com.blueskykong.tm.core.service.handler.ConfirmTxTransactionHandler;
import com.blueskykong.tm.core.service.handler.ConsumedTransactionHandler;
import com.blueskykong.tm.core.service.handler.StartTxTransactionHandler;
import com.blueskykong.tm.core.service.impl.AspectTransactionServiceImpl;
import com.blueskykong.tm.core.service.impl.ExternalNettyServiceImpl;
import com.blueskykong.tm.core.service.impl.InitServiceImpl;
import com.blueskykong.tm.core.service.impl.TxTransactionFactoryServiceImpl;
import com.blueskykong.tm.core.service.message.NettyMessageServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

@Configuration
@EnableConfigurationProperties(TxConfig.class)
@ConditionalOnBean({DiscoveryClient.class})
public class TransactionCoreAutoConfiguration {

    @Bean
    public TransactionThreadPool transactionThreadPool(TxConfig txConfig) {
        return new TransactionThreadPool(txConfig);
    }

    @Bean
    public NettyClientService nettyClientService(NettyClientHandlerInitializer nettyClientHandlerInitializer, DiscoveryClient discoveryClient) {
        return new NettyClientServiceImpl(nettyClientHandlerInitializer, discoveryClient);
    }

    @Bean
    public TxManagerMessageService txManagerMessageService(NettyClientMessageHandler nettyClientMessageHandler) {
        return new NettyMessageServiceImpl(nettyClientMessageHandler);
    }

    @Configuration
    protected static class InitialService {

        @Bean
        @Primary
        public InitService initService(NettyClientService nettyClientService, TxOperateService txOperateService) {
            return new InitServiceImpl(nettyClientService, txOperateService);
        }

        @Bean
        public ExternalNettyService externalNettyService(TxTransactionInterceptor txTransactionInterceptor) {
            return new ExternalNettyServiceImpl(txTransactionInterceptor);
        }

        @Bean
        public AspectTransactionService aspectTransactionService(TxTransactionFactoryService txTransactionFactoryService) {
            return new AspectTransactionServiceImpl(txTransactionFactoryService);
        }

        @Bean
        public TxTransactionFactoryService txTransactionFactoryService() {
            return new TxTransactionFactoryServiceImpl();
        }

        @Bean
        public ObjectSerializer objectSerializer(TxConfig nettyConfig) {
            final SerializeProtocolEnum serializeProtocolEnum =
                    SerializeProtocolEnum.acquireSerializeProtocol(nettyConfig.getNettySerializer());

            final ServiceLoader<ObjectSerializer> objectSerializers = ServiceBootstrap.loadAll(ObjectSerializer.class);

            final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false)
                    .filter(objectSerializer ->
                            Objects.equals(objectSerializer.getScheme(), serializeProtocolEnum.getSerializeProtocol())).findFirst();
            return serializer.orElse(new KryoSerializer());
        }
    }

    @Configuration
    protected static class Compensation {

        @Bean
        public TxOperateService txCompensationService(ModelNameService modelNameService) {
            return new TxOperateServiceImpl(modelNameService);
        }

        @Bean
        public TxOperateCommand command(TxOperateService txOperateService) {
            return new TxOperateCommand(txOperateService);
        }
    }

    @Configuration
    protected static class TransactionHandler {
        @Bean
        public TxTransactionHandler consumedTransactionHandler(TxManagerMessageService txManagerMessageService, TxOperateService txOperateService) {
            return new ConsumedTransactionHandler(txManagerMessageService, txOperateService);
        }

        @Bean
        public TxTransactionHandler confirmTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxOperateCommand txOperateCommand) {
            return new ConfirmTxTransactionHandler(txManagerMessageService, txOperateCommand);
        }

        @Bean
        public TxTransactionHandler startTxTransactionHandler(TxManagerMessageService txManagerMessageService,
                                                              TxOperateCommand txOperateCommand,
                                                              ObjectSerializer objectSerializer,
                                                              ModelNameService modelNameService) {
            return new StartTxTransactionHandler(txManagerMessageService, txOperateCommand, objectSerializer, modelNameService);
        }
    }

    @Configuration
    protected static class TransactionBootstrap {

        @Bean
        public TxTransactionInitialize txTransactionInitialize(InitService initService) {
            return new TxTransactionInitialize(initService);
        }

        @Bean
        public TxTransactionBootstrap txTransactionBootstrap(TxTransactionInitialize txTransactionInitialize, TxConfig txConfig) {
            return new TxTransactionBootstrap(txTransactionInitialize, txConfig);
        }
    }

    @Configuration
    protected static class NettyHandler {

        @Bean
        public NettyClientMessageHandler nettyClientMessageHandler(ModelNameService modelNameService, TxOperateCommand txOperateCommand) {
            return new NettyClientMessageHandler(modelNameService, txOperateCommand);
        }

        @Bean
        public NettyClientHandlerInitializer nettyClientHandlerInitializer(NettyClientMessageHandler nettyClientMessageHandler) {
            return new NettyClientHandlerInitializer(nettyClientMessageHandler);
        }
    }

}
