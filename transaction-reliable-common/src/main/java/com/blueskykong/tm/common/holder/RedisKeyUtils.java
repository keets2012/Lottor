package com.blueskykong.tm.common.holder;

public class RedisKeyUtils {

    public static byte[] getRedisKey(String keyPrefix, String id) {
        byte[] prefix = keyPrefix.getBytes();
        final byte[] idBytes = id.getBytes();
        byte[] key = new byte[prefix.length + idBytes.length];
        System.arraycopy(prefix, 0, key, 0, prefix.length);
        System.arraycopy(idBytes, 0, key, prefix.length, idBytes.length);
        return key;
    }
}
