package com.blueskykong.tm.server.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 */
public interface MsgSource extends SenderParent {
    String OUTPUT = "msg-output";

    @Output(OUTPUT)
    MessageChannel output();

}
