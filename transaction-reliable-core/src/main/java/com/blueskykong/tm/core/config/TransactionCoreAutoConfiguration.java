package com.blueskykong.tm.core.config;

import com.blueskykong.tm.common.config.TxConfig;
import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.helper.SpringBeanUtils;
import com.blueskykong.tm.common.holder.ServiceBootstrap;
import com.blueskykong.tm.common.serializer.KryoSerializer;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.bootstrap.TxTransactionBootstrap;
import com.blueskykong.tm.core.bootstrap.TxTransactionInitialize;
import com.blueskykong.tm.core.compensation.TxCompensationService;
import com.blueskykong.tm.core.compensation.command.TxCompensationCommand;
import com.blueskykong.tm.core.compensation.impl.TxCompensationServiceImpl;
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

/**
 * @author keets
 */
@Configuration
@EnableConfigurationProperties(TxConfig.class)
@ConditionalOnBean({DiscoveryClient.class})
public class TransactionCoreAutoConfiguration {

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
        public InitService initService(NettyClientService nettyClientService, TxCompensationService txCompensationService) {
            return new InitServiceImpl(nettyClientService, txCompensationService);
        }


        @Bean
        public ExternalNettyService externalNettyService() {
            return new ExternalNettyServiceImpl();
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
        public TxCompensationService txCompensationService(ModelNameService modelNameService, TxManagerMessageService txManagerMessageService) {
            return new TxCompensationServiceImpl(modelNameService, txManagerMessageService);
        }

        @Bean
        public TxCompensationCommand command(TxCompensationService txCompensationService) {
            return new TxCompensationCommand(txCompensationService);
        }

    }

    @Configuration
    protected static class TransactionHandler {
        @Bean
        public TxTransactionHandler consumedTransactionHandler(TxManagerMessageService txManagerMessageService) {
            return new ConsumedTransactionHandler(txManagerMessageService);
        }

        @Bean
        public TxTransactionHandler confirmTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxCompensationCommand txCompensationCommand) {
            return new ConfirmTxTransactionHandler(txManagerMessageService, txCompensationCommand);
        }

        @Bean
        public TxTransactionHandler startTxTransactionHandler(TxManagerMessageService txManagerMessageService, TxCompensationCommand txCompensationCommand, ObjectSerializer objectSerializer) {
            return new StartTxTransactionHandler(txManagerMessageService, txCompensationCommand, objectSerializer);
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
        public NettyClientMessageHandler nettyClientMessageHandler() {
            return new NettyClientMessageHandler();
        }

        @Bean
        public NettyClientHandlerInitializer nettyClientHandlerInitializer(NettyClientMessageHandler nettyClientMessageHandler) {
            return new NettyClientHandlerInitializer(nettyClientMessageHandler);
        }
    }

}
