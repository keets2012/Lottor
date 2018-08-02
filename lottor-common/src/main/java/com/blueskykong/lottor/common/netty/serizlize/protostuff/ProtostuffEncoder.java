package com.blueskykong.lottor.common.netty.serizlize.protostuff;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageEncoder;

public class ProtostuffEncoder extends AbstractMessageEncoder {

    public ProtostuffEncoder(MessageCodecService util) {
        super(util);
    }
}

