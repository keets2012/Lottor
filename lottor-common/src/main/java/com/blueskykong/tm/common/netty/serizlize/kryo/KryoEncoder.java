
package com.blueskykong.tm.common.netty.serizlize.kryo;


import com.blueskykong.tm.common.netty.MessageCodecService;
import com.blueskykong.tm.common.netty.serizlize.AbstractMessageEncoder;

public class KryoEncoder extends AbstractMessageEncoder {

    public KryoEncoder(MessageCodecService util) {
        super(util);
    }
}
