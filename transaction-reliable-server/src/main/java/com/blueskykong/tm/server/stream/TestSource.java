package com.blueskykong.tm.server.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 * @data 2018/4/28.
 */
public interface TestSource {
    String OUTPUT = "tx-test-output";

    @Output(OUTPUT)
    MessageChannel output();


}
