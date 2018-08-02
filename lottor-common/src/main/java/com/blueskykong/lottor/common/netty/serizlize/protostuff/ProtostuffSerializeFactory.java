
package com.blueskykong.lottor.common.netty.serizlize.protostuff;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {

    @Override
    public ProtostuffSerialize create() throws Exception {
        return createProtostuff();
    }

    @Override
    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize protostuffSerialize) {
        return new DefaultPooledObject<>(protostuffSerialize);
    }

    private ProtostuffSerialize createProtostuff() {
        return new ProtostuffSerialize();
    }
}
