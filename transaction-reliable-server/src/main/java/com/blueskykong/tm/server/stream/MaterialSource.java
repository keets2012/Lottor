package com.blueskykong.tm.server.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 */
public interface MaterialSource {
    String OUTPUT = "tx-material-output";

    @Output(OUTPUT)
    MessageChannel output();


}
