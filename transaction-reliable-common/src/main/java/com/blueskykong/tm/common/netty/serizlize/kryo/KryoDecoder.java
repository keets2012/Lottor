
package com.blueskykong.tm.common.netty.serizlize.kryo;


import com.blueskykong.tm.common.netty.MessageCodecService;
import com.blueskykong.tm.common.netty.serizlize.AbstractMessageDecoder;

public class KryoDecoder extends AbstractMessageDecoder {

    public KryoDecoder(MessageCodecService service) {
        super(service);
    }
}
