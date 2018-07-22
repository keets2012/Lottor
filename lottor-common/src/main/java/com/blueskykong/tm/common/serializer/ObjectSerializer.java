
package com.blueskykong.tm.common.serializer;


import com.blueskykong.tm.common.exception.TransactionException;

public interface ObjectSerializer {
    /**
     * 序列化对象
     *
     * @param obj 需要序更列化的对象
     * @return byte []
     * @throws TransactionException 异常
     */
    byte[] serialize(Object obj) throws TransactionException;

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @return 对象
     * @throws TransactionException 异常
     */

    /**
     * 反序列化对象
     *
     * @param param 需要反序列化的byte []
     * @param clazz 序列化后对应的java class
     * @param <T>   泛型
     * @return <T>
     * @throws TransactionException 异常
     */
    <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException;

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    String getScheme();
}
