package com.blueskykong.tm.server.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 */
public interface AffairSource {
    String OUTPUT = "affair-output";

    @Output(OUTPUT)
    MessageChannel output();

}
