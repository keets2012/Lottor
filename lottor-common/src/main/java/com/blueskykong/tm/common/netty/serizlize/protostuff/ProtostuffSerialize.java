
package com.blueskykong.tm.common.netty.serizlize.protostuff;

import com.blueskykong.tm.common.netty.NettyTransferSerialize;
import com.blueskykong.tm.common.netty.bean.LottorRequest;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.InputStream;
import java.io.OutputStream;

public class ProtostuffSerialize implements NettyTransferSerialize {
    private static SchemaCache cachedSchema = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) cachedSchema.get(cls);
    }

    @Override
    public Object deserialize(InputStream input) {
        try {
            LottorRequest message = objenesis.newInstance(LottorRequest.class);
            Schema<LottorRequest> schema = getSchema(LottorRequest.class);
            ProtostuffIOUtil.mergeFrom(input, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void serialize(OutputStream output, Object object) {
        Class cls = object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(output, object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }
}

