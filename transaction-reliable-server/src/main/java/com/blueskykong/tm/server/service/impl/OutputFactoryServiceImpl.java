package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ServiceNameEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.server.service.OutputFactoryService;
import com.blueskykong.tm.server.stream.AffairSource;
import com.blueskykong.tm.server.stream.MaterialSource;
import com.blueskykong.tm.server.stream.TestSource;
import com.blueskykong.tm.server.stream.TssSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author keets
 */
@Component
public class OutputFactoryServiceImpl implements OutputFactoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputFactoryServiceImpl.class);


    @Autowired
    private AffairSource affairSource;

    @Autowired
    private MaterialSource materialSource;

    @Autowired
    private TssSource tssSource;

    @Autowired
    private TestSource testSource;

    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    @Override
    public Boolean sendMsg(TransactionMsg msg) {
        ServiceNameEnum serviceNameEnum = ServiceNameEnum.fromString(msg.getTarget());
        LogUtil.debug(LOGGER, "target service: {}", () -> serviceNameEnum.getServiceName());
        switch (serviceNameEnum) {
            case AFFAIR:
                affairSource.output().send(MessageBuilder.withPayload(msg).build());
                break;
            case MATERIAL:
                materialSource.output().send(MessageBuilder.withPayload(msg).build());
                break;
            case TSS:
                tssSource.output().send(MessageBuilder.withPayload(msg).build());
                break;
            case TEST:
                testSource.output().send(MessageBuilder.withPayload(msg).build());
            default:
                LogUtil.error(LOGGER, "no available cases for {}.", () -> serviceNameEnum.getServiceName());
                break;
        }
        return true;
    }
}
