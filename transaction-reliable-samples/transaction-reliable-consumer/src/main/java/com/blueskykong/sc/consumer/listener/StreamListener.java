package com.blueskykong.sc.consumer.listener;

import com.blueskykong.sc.consumer.stream.TestSink;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author keets
 */
@Component
@EnableBinding({TestSink.class})
public class StreamListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamListener.class);

//    @Autowired
//    private NotificationService notificationService;

    @Autowired
    private ExternalNettyService nettyService;

    @org.springframework.cloud.stream.annotation.StreamListener(TestSink.INPUT)
    public void processSMS(Message<TransactionMsg> message) {
        process(message.getPayload());
    }

    private void process(TransactionMsg message) {
        try {
            message.getSubTaskId();
//        message.get
            LOGGER.info("===============consume notification message: =======================" + message.toString());
            if (Objects.nonNull(message)) {
                int i = 1/0;
            }
        } catch (Exception e) {
            LogUtil.error(LOGGER, e::getLocalizedMessage);
            message.setMessage(e.getLocalizedMessage());
            nettyService.consumedSend(message, false);
            return;
        }
        nettyService.consumedSend(message, true);
        return;
    }
}