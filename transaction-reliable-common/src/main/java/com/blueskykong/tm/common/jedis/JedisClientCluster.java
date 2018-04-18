package com.blueskykong.tm.common.jedis;

import redis.clients.jedis.JedisCluster;

import java.util.Set;

public class JedisClientCluster implements JedisClient {


    private JedisCluster jedisCluster;

    public JedisClientCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public String set(String key, String value) {
        return jedisCluster.set(key, value);
    }

    @Override
    public String set(String key, byte[] value) {
        return jedisCluster.set(key.getBytes(),value);
    }

    @Override
    public Long del(String... keys) {
        return jedisCluster.del(keys);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public byte[] get(byte[] key) {
        return jedisCluster.get(key);
    }

    @Override
    public Set<byte[]> keys(byte[] pattern) {
        return jedisCluster.hkeys(pattern);
    }

    @Override
    public Set<String> keys(String key) {
        return jedisCluster.hkeys(key);
    }

    @Override
    public Long hset(String key, String item, String value) {
        return jedisCluster.hset(key, item, value);
    }

    @Override
    public String hget(String key, String item) {
        return jedisCluster.hget(key, item);
    }

    @Override
    public Long hdel(String key, String item) {
        return jedisCluster.hdel(key, item);
    }

    @Override
    public Long incr(String key) {
        return jedisCluster.incr(key);
    }

    @Override
    public Long decr(String key) {
        return jedisCluster.decr(key);
    }

    @Override
    public Long expire(String key, int second) {
        return jedisCluster.expire(key, second);
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        return jedisCluster.zrange(key,start,end);
    }


}
