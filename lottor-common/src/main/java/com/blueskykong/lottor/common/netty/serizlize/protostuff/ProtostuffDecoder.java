package com.blueskykong.lottor.common.netty.serizlize.protostuff;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageDecoder;

public class ProtostuffDecoder extends AbstractMessageDecoder {

    public ProtostuffDecoder(MessageCodecService util) {
        super(util);
    }
}

