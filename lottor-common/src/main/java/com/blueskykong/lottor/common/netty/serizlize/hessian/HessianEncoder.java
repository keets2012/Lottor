package com.blueskykong.lottor.common.netty.serizlize.hessian;


import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageEncoder;

public class HessianEncoder extends AbstractMessageEncoder {

    public HessianEncoder(MessageCodecService util) {
        super(util);
    }
}

