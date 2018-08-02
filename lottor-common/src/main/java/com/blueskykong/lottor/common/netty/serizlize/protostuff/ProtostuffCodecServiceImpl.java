package com.blueskykong.lottor.common.netty.serizlize.protostuff;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ProtostuffCodecServiceImpl implements MessageCodecService {
    private static Closer closer = Closer.create();
    private ProtostuffSerializePool pool = ProtostuffSerializePool.getProtostuffPoolInstance();
    @Override
    public void encode(final ByteBuf out, final Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            protostuffSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            pool.restore(protostuffSerialization);
        } finally {
            closer.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            ProtostuffSerialize protostuffSerialization = pool.borrow();
            Object obj = protostuffSerialization.deserialize(byteArrayInputStream);
            pool.restore(protostuffSerialization);
            return obj;
        } finally {
            //closer.close();
        }
    }
}

