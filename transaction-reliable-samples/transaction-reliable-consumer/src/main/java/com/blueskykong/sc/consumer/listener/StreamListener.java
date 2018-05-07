package com.blueskykong.sc.consumer.listener;

import com.blueskykong.sc.consumer.domain.Product;
import com.blueskykong.sc.consumer.service.ConsumerService;
import com.blueskykong.sc.consumer.stream.TestSink;
import com.blueskykong.tm.common.entity.TransactionMsg;
import com.blueskykong.tm.common.enums.MethodNameEnum;
import com.blueskykong.tm.common.holder.LogUtil;
import com.blueskykong.tm.common.serializer.ObjectSerializer;
import com.blueskykong.tm.core.service.ExternalNettyService;
import org.apache.commons.lang3.StringUtils;
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

    final private ConsumerService consumerService;

    final private ExternalNettyService nettyService;

    final private ObjectSerializer objectSerializer;

    @Autowired
    public StreamListener(ExternalNettyService nettyService, ObjectSerializer objectSerializer, ConsumerService consumerService) {
        this.nettyService = nettyService;
        this.objectSerializer = objectSerializer;
        this.consumerService = consumerService;
    }

    @org.springframework.cloud.stream.annotation.StreamListener(TestSink.INPUT)
    public void processSMS(Message<TransactionMsg> message) {
        process(message.getPayload());
    }

    private void process(TransactionMsg message) {
        try {
            if (Objects.nonNull(message)) {
                LOGGER.info("===============consume notification message: =======================" + message.toString());
                if (StringUtils.isNotBlank(message.getMethod())) {
                    MethodNameEnum method = MethodNameEnum.fromString(message.getMethod());

                    switch (method) {
                        case CONSUMER_TEST:
                            //TODO 下个版本优化，客户端暂时需要反序列化对应的对象
                            Product product = objectSerializer.deSerialize(message.getArgs(), Product.class);
                            LogUtil.info(LOGGER, "matched case {}, param is {}", () -> MethodNameEnum.CONSUMER_TEST, () -> product);
                            consumerService.testConsumer(product);
                            break;
                        default:
                            LogUtil.warn(LOGGER, () -> "no matched consumer case!");
                            message.setMessage("no matched consumer case!");
                            nettyService.consumedSend(message, false);
                            return;
                    }
                }
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