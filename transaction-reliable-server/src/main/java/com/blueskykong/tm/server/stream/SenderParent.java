package com.blueskykong.tm.server.stream;

import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 */
public interface SenderParent {

    /**
     * 父接口
     */
    MessageChannel output();


}
