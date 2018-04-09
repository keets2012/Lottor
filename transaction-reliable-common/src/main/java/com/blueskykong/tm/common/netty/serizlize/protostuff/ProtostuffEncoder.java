
package com.blueskykong.tm.common.netty.serizlize.protostuff;


import com.blueskykong.tm.common.netty.MessageCodecService;
import com.blueskykong.tm.common.netty.serizlize.AbstractMessageEncoder;

public class ProtostuffEncoder extends AbstractMessageEncoder {

    public ProtostuffEncoder(MessageCodecService util) {
        super(util);
    }
}

