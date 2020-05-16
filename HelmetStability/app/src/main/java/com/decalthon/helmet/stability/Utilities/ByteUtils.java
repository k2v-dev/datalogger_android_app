package com.decalthon.helmet.stability.utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteUtils {
    private static ByteBuffer buffer_long = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
    private static ByteBuffer buffer_int = ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN);
    //private static ByteBuffer buffer_float = ByteBuffer.allocate(Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);

    public static byte[] longToBytes(long x) {
        buffer_long.putLong(0, x);
        return buffer_long.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer_long.put(bytes, 0, bytes.length);
        buffer_long.flip();//need flip
        return buffer_long.getLong();
    }

    public static byte[] intToBytes(int x) {
        buffer_int.putInt(0, x);
        return buffer_int.array();
    }

    public static float bytesToFloat(byte[] bytes) {
        ByteBuffer buffer_float = ByteBuffer.allocate(Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer_float.put(bytes, 0, bytes.length);
        buffer_float.flip();//need flip
        return buffer_float.getFloat();
    }

    public static byte[] longToBytesNew(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.BIG_ENDIAN);
        //buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(x);
        return buffer.array();
    }
    public static long bytesToLongNew(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
