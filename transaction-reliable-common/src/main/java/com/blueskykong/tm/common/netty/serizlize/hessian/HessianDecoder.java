
package com.blueskykong.tm.common.netty.serizlize.hessian;


import com.blueskykong.tm.common.netty.MessageCodecService;
import com.blueskykong.tm.common.netty.serizlize.AbstractMessageDecoder;

public class HessianDecoder extends AbstractMessageDecoder {

    public HessianDecoder(MessageCodecService util) {
        super(util);
    }
}

