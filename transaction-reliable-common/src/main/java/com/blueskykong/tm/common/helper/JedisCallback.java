package com.blueskykong.tm.common.helper;

import redis.clients.jedis.Jedis;

/**
 * @author keets
 */
@FunctionalInterface
public interface JedisCallback<T> {

    /**
     * redis 操作
     *
     * @param jedis jedis客户端
     * @return T
     */
    T doInJedis(Jedis jedis);
}