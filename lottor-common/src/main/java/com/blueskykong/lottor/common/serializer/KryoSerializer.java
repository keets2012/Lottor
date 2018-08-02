
package com.blueskykong.lottor.common.serializer;

import com.blueskykong.lottor.common.enums.SerializeProtocolEnum;
import com.blueskykong.lottor.common.exception.TransactionException;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class KryoSerializer implements ObjectSerializer {
    /**
     * 序列化
     *
     * @param obj 需要序更列化的对象
     * @return 序列化后的byte 数组
     * @throws TransactionException
     */
    @Override
    public byte[] serialize(Object obj) throws TransactionException {
        byte[] bytes;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            //获取kryo对象
            Kryo kryo = new Kryo();
            Output output = new Output(outputStream);
            kryo.writeObject(output, obj);
            bytes = output.toBytes();
            output.flush();
        } catch (Exception ex) {
            throw new TransactionException("kryo serialize error" + ex.getMessage());
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {

            }
        }
        return bytes;
    }

    /**
     * 反序列化
     *
     * @param param 需要反序列化的byte []
     * @return 序列化对象
     * @throws TransactionException
     */
    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException {
        T object;
        try(ByteArrayInputStream inputStream=new ByteArrayInputStream(param)) {
            Kryo kryo = new Kryo();
            Input input = new Input(inputStream);
            object = kryo.readObject(input, clazz);
            input.close();
        } catch (Exception e) {
            throw new TransactionException("kryo deSerialize error" + e.getMessage());
        }
        return object;
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return SerializeProtocolEnum.KRYO.getSerializeProtocol();
    }
}
