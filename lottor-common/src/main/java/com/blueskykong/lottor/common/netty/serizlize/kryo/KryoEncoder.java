package com.blueskykong.lottor.common.netty.serizlize.kryo;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageEncoder;

public class KryoEncoder extends AbstractMessageEncoder {

    public KryoEncoder(MessageCodecService util) {
        super(util);
    }
}
