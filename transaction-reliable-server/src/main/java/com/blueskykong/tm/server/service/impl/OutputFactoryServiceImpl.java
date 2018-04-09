package com.blueskykong.tm.server.service.impl;

import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.ServiceNameEnum;
import com.blueskykong.tm.server.service.OutputFactoryService;
import com.blueskykong.tm.server.stream.MsgSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author keets
 */
@Component
public class OutputFactoryServiceImpl implements OutputFactoryService {

    @Autowired
    private MsgSource msgSource;

    /**
     * 发送事务消息
     *
     * @param msg
     * @return Boolean
     */
    @Override
    public Boolean sendMsg(TransactionMsg msg) {
        ServiceNameEnum serviceNameEnum = ServiceNameEnum.fromString(msg.getTarget());
        switch (serviceNameEnum) {
            case AFFAIR:
                msgSource.output().send(MessageBuilder.withPayload(msg).build());
                break;

            default:
                msgSource.output().send(MessageBuilder.withPayload(msg).build());
                break;

        }
        return true;
    }
}
