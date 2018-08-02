package com.blueskykong.lottor.common.netty.serizlize;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public abstract class AbstractMessageEncoder extends MessageToByteEncoder<Object> {

    private MessageCodecService util;

    public AbstractMessageEncoder(final MessageCodecService util) {
        this.util = util;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        util.encode(out, msg);
    }
}

