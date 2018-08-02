package com.blueskykong.lottor.common.netty.serizlize.hessian;


import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageDecoder;

public class HessianDecoder extends AbstractMessageDecoder {

    public HessianDecoder(MessageCodecService util) {
        super(util);
    }
}

