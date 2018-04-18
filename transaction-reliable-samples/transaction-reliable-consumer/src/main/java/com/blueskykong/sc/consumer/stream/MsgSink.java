package com.blueskykong.sc.consumer.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author keets
 */
public interface MsgSink {
    String INPUT = "msg-input";

    @Input(INPUT)
    SubscribableChannel input();
}
