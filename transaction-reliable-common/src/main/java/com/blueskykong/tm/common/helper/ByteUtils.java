package com.blueskykong.tm.common.helper;

import java.nio.ByteBuffer;

/**
 * @author keets
 */
public class ByteUtils {

    public static byte[] longToBytes(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }
}
