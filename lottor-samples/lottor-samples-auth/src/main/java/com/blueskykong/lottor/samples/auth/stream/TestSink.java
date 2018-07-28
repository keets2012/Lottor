package com.blueskykong.lottor.samples.auth.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface TestSink {
    String INPUT = "test-input";

    @Input(INPUT)
    SubscribableChannel input();
}
