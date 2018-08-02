
package com.blueskykong.lottor.common.netty.serizlize.protostuff;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ProtostuffSerializePool {

    private GenericObjectPool<ProtostuffSerialize> protostuffpool;
    private static volatile ProtostuffSerializePool poolFactory = null;

    private ProtostuffSerializePool() {
        protostuffpool = new GenericObjectPool<>(new ProtostuffSerializeFactory());
    }

    public static ProtostuffSerializePool getProtostuffPoolInstance() {
        if (poolFactory == null) {
            synchronized (ProtostuffSerializePool.class) {
                if (poolFactory == null) {
                    poolFactory = new ProtostuffSerializePool();
                }
            }
        }
        return poolFactory;
    }

    public ProtostuffSerializePool(final int maxTotal, final int minIdle, final long maxWaitMillis, final long minEvictableIdleTimeMillis) {
        protostuffpool = new GenericObjectPool<>(new ProtostuffSerializeFactory());

        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        protostuffpool.setConfig(config);
    }

    public ProtostuffSerialize borrow() {
        try {
            return getProtostuffpool().borrowObject();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void restore(final ProtostuffSerialize object) {
        getProtostuffpool().returnObject(object);
    }

    public GenericObjectPool<ProtostuffSerialize> getProtostuffpool() {
        return protostuffpool;
    }
}

