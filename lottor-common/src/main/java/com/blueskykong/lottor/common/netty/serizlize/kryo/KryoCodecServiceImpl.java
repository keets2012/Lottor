package com.blueskykong.lottor.common.netty.serizlize.kryo;

import com.blueskykong.lottor.common.netty.MessageCodecService;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoCodecServiceImpl implements MessageCodecService {

    private KryoPool pool;
    private static Closer closer = Closer.create();

    public KryoCodecServiceImpl(KryoPool pool) {
        this.pool = pool;
    }

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            closer.register(byteArrayOutputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            kryoSerialization.serialize(byteArrayOutputStream, message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
        } finally {
            closer.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            closer.register(byteArrayInputStream);
            KryoSerialize kryoSerialization = new KryoSerialize(pool);
            return kryoSerialization.deserialize(byteArrayInputStream);
        } finally {
           // closer.close();
        }
    }
}
