package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ServiceNameEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.server.service.OutputFactoryService;
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

/**
 * @author keets
 */
@Component
@EnableBinding
public class OutputFactoryServiceImpl implements OutputFactoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFactoryServiceImpl.class);

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
    public Boolean sendMsg(TransactionMsg msg) {
        ServiceNameEnum serviceNameEnum = ServiceNameEnum.fromString(msg.getTarget());
        if (!Objects.nonNull(serviceNameEnum)) {
            LogUtil.warn(LOGGER, "no available cases for {}. pls check if this topic exists.", () -> msg.getTarget());
        } else {
            LogUtil.debug(LOGGER, "send tx-msg and target service: {}", () -> serviceNameEnum.getServiceName());
        }
        resolver.resolveDestination(msg.getTarget()).send(MessageBuilder.createMessage(msg,
                new MessageHeaders(Collections.singletonMap(MessageHeaders.CONTENT_TYPE, contentType))));

        return true;
    }

}
