package com.blueskykong.tm.common.helper;

import com.blueskykong.tm.common.holder.RedisKeyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author keets
 */
public class RedisHelper {

    public static byte[] getRedisKey(String keyPrefix, String id) {
        return RedisKeyUtils.getRedisKey(keyPrefix, id);
    }

    public static String buildRecoverKey(String keyPrefix, String id) {
        return String.join(":", keyPrefix, id);
    }


    public static <T> T execute(JedisPool jedisPool, JedisCallback<T> callback) {
        try(Jedis jedis=jedisPool.getResource()) {
            return callback.doInJedis(jedis);
        }
    }
}