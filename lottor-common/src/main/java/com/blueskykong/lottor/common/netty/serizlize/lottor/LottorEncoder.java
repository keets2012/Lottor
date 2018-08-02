package com.blueskykong.lottor.common.netty.serizlize.lottor;

import com.blueskykong.lottor.common.netty.bean.LottorBean;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <pre>
 * 自己定义的协议
 *  数据包格式
 * +——----——+——-----——+——----——+
 * |协议开始标志|  长度             |   数据       |
 * +——----——+——-----——+——----——+
 * 1.协议开始标志head_data，为int类型的数据，16进制表示为0X76
 * 2.传输数据的长度contentLength，int类型
 * 3.要传输的数据
 * </pre>
 *
 * @data 2018/6/9.
 */
public class LottorEncoder extends MessageToByteEncoder<LottorBean> {
    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, LottorBean msg, ByteBuf out) throws Exception {
        // 写入消息Lottor的具体内容

        // 1.写入消息的开头的信息标志(int类型)
        out.writeInt(msg.getHead_data());

        // 2.写入消息的长度(int 类型)
        out.writeInt(msg.getContentLength());

        // 3.写入消息的内容(byte[]类型)
        out.writeBytes(msg.getContent());
    }
}

