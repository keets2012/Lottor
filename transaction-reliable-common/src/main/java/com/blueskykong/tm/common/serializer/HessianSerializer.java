
package com.blueskykong.tm.common.serializer;

import com.blueskykong.tm.common.enums.SerializeProtocolEnum;
import com.blueskykong.tm.common.exception.TransactionException;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@SuppressWarnings("unchecked")
public class HessianSerializer implements ObjectSerializer {
    @Override
    public byte[] serialize(Object obj) throws TransactionException {
        ByteArrayOutputStream baos;
        try {
            baos = new ByteArrayOutputStream();
            Hessian2Output hos = new Hessian2Output(baos);
            hos.writeObject(obj);
            hos.flush();
            hos.close();
        } catch (IOException ex) {
            throw new TransactionException("Hessian serialize error " + ex.getMessage());
        }
        return baos.toByteArray();
    }

    @Override
    public <T> T deSerialize(byte[] param, Class<T> clazz) throws TransactionException {
        ByteArrayInputStream bios;
        try {
            bios = new ByteArrayInputStream(param);
            Hessian2Input his = new Hessian2Input(bios);
            return (T) his.readObject();
        } catch (IOException e) {
            throw new TransactionException("Hessian deSerialize error " + e.getMessage());
        }
    }

    /**
     * 设置scheme
     *
     * @return scheme 命名
     */
    @Override
    public String getScheme() {
        return SerializeProtocolEnum.HESSIAN.getSerializeProtocol();
    }
}
