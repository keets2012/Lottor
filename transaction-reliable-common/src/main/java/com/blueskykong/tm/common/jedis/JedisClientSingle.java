package com.blueskykong.tm.common.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;


public class JedisClientSingle implements JedisClient {

    private JedisPool jedisPool;


    public JedisClientSingle(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }

    }

    @Override
    public String set(String key, byte[] value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key.getBytes(), value);
        }

    }

    @Override
    public Long del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        }

    }

    @Override
    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }

    }

    @Override
    public byte[] get(byte[] key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(pattern);
        }

    }

    @Override
    public Set<String> keys(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.keys(key);
        }
    }

    @Override
    public Long hset(String key, String item, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, item, value);
        }

    }

    @Override
    public String hget(String key, String item) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, item);
        }
    }

    @Override
    public Long hdel(String key, String item) {

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hdel(key, item);
        }

    }

    @Override
    public Long incr(String key) {

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        }

    }

    @Override
    public Long decr(String key) {

        try (Jedis jedis = jedisPool.getResource()) {

            return jedis.decr(key);
        }

    }

    @Override
    public Long expire(String key, int second) {

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.expire(key, second);
        }

    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, end);
        }
    }


}
