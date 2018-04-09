
package com.blueskykong.tm.common.netty.serizlize;

import com.blueskykong.tm.common.netty.MessageCodecService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public  abstract class AbstractMessageDecoder extends ByteToMessageDecoder {

    final private static int MESSAGE_LENGTH = MessageCodecService.MESSAGE_LENGTH;
    private MessageCodecService util = null;

    public AbstractMessageDecoder(final MessageCodecService service) {
        this.util = service;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < AbstractMessageDecoder.MESSAGE_LENGTH) {
            return;
        }

        in.markReaderIndex();
        int messageLength = in.readInt();

        if (messageLength < 0) {
            ctx.close();
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
        } else {
            byte[] messageBody = new byte[messageLength];
            in.readBytes(messageBody);

            try {
                Object obj = util.decode(messageBody);
                out.add(obj);
            } catch (IOException ex) {
                Logger.getLogger(AbstractMessageDecoder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

