
package com.blueskykong.tm.common.serializer;

import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.exception.TransactionException;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ProtostuffSerializer implements ObjectSerializer {
    private static final SchemaCache CACHED_SCHEMA = SchemaCache.getInstance();
    private static final Objenesis OBJENESIS_STD = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) CACHED_SCHEMA.get(cls);
    }


    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws TransactionException
     */
    @Override
    public byte[] serialize(Object obj) throws TransactionException {
        Class cls = obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try ( ByteArrayOutputStream outputStream = new ByteArrayOutputStream();){
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.writeTo(outputStream, obj, schema, buffer);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new TransactionException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz
     * @return 对象
     * @throws TransactionException
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException {
        T object;
        try( ByteArrayInputStream inputStream = new ByteArrayInputStream(param)) {
            Class cls = clazz;
            object = OBJENESIS_STD.newInstance((Class<T>) cls);
            Schema schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(inputStream, object, schema);
            return object;
        } catch (Exception e) {
            throw new TransactionException(e.getMessage(), e);
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return SerializeProtocolEnum.PROTOSTUFF.getSerializeProtocol();
    }
}

