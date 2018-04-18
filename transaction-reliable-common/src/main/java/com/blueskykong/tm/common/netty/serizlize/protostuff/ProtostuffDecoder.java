
package com.blueskykong.tm.common.netty.serizlize.protostuff;


import com.blueskykong.tm.common.netty.MessageCodecService;
import com.blueskykong.tm.common.netty.serizlize.AbstractMessageDecoder;

public class ProtostuffDecoder extends AbstractMessageDecoder {

    public ProtostuffDecoder(MessageCodecService util) {
        super(util);
    }
}

