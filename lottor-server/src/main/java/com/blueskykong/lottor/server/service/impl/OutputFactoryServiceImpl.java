package com.blueskykong.lottor.server.service.impl;

import com.blueskykong.lottor.server.service.OutputFactoryService;
import com.blueskykong.lottor.common.entity.TransactionMsgAdapter;
import com.blueskykong.lottor.common.enums.ServiceNameEnum;
import com.blueskykong.lottor.common.holder.LogUtil;
import com.blueskykong.lottor.server.service.OutputFactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
@EnableBinding
public class OutputFactoryServiceImpl implements OutputFactoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFactoryServiceImpl.class);

    private static final String CHANNEL_PREFIX = "tx-";
    private BinderAwareChannelResolver resolver;

    @Value("stream.content-type")
    private String contentType;

    @Autowired
    public OutputFactoryServiceImpl(BinderAwareChannelResolver resolver) {
        this.resolver = resolver;
    }


    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    @Override
    public Boolean sendMsg(TransactionMsgAdapter msg) {
        ServiceNameEnum serviceNameEnum = ServiceNameEnum.fromString(msg.getTarget());
        if (!Objects.nonNull(serviceNameEnum)) {

            LogUtil.warn(LOGGER, "no available cases for【{}】. pls check if this topic exists.", () -> msg.getTarget());
            resolver.resolveDestination(CHANNEL_PREFIX + msg.getTarget()).send(MessageBuilder.createMessage(msg,
                    new MessageHeaders(Collections.singletonMap(MessageHeaders.CONTENT_TYPE, contentType))));
        } else {
            LogUtil.debug(LOGGER, "send tx-msg and target service【{}】", () -> serviceNameEnum.getServiceName());
            resolver.resolveDestination(serviceNameEnum.getTopic()).send(MessageBuilder.createMessage(msg,
                    new MessageHeaders(Collections.singletonMap(MessageHeaders.CONTENT_TYPE, contentType))));
        }


        return true;
    }

}
