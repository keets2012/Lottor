package com.blueskykong.lottor.common.netty.serizlize.kryo;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.blueskykong.lottor.common.netty.serizlize.AbstractMessageDecoder;

public class KryoDecoder extends AbstractMessageDecoder {

    public KryoDecoder(MessageCodecService service) {
        super(service);
    }
}
