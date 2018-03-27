package com.arcaneless.myoairblock.airblock;

/**
 * Created by marcuscheung on 22/12/2017.
 */

public class AirBlockByteUtil {

    static final int SIZE_BYTE = 1;
    static final int SIZE_SHORT = 2;
    static final int SIZE_byte = 2;
    static final int SIZE_float = 5;
    static final int SIZE_long = 5;
    static final int SIZE_short = 3;
    static final int TYPE_BYTE = 1;
    static final int TYPE_SHORT = 3;
    static final int TYPE_byte = 2;
    static final int TYPE_float = 6;
    static final int TYPE_long = 5;
    static final int TYPE_short = 4;

    public static int convert7to8(final byte[] array, final int n, final int n2) {
        int n3 = 0;
        for (int i = 0; i < n2 - n; ++i) {
            n3 |= array[i + n] << i * 7;
        }
        return n3;
    }

    public static byte[] convert8to7(final int n, final int n2) {
        switch (n) {
            default: {
                return null;
            }
            case 1: {
                return new byte[] { (byte)(n2 & 0x7F) };
            }
            case 3: {
                return new byte[] { (byte)(n2 & 0x7F), (byte)((n2 & 0x3F80) >>> 7) };
            }
            case 2: {
                return new byte[] { (byte)(n2 & 0x7F), (byte)((n2 & 0x80) >>> 7) };
            }
            case 4: {
                return new byte[] { (byte)(n2 & 0x7F), (byte)((n2 & 0x3F80) >>> 7), (byte)((0xC000 & n2) >>> 14) };
            }
            case 5: {
                return new byte[] { (byte)(n2 & 0x7F), (byte)((n2 & 0x3F80) >>> 7), (byte)((0x1FC000 & n2) >>> 14), (byte)((0xFE00000 & n2) >>> 21), (byte)((n2 & 0xF0000000) >>> 28) };
            }
            case 6: {
                return new byte[] { (byte)(n2 & 0x7F), (byte)((n2 & 0x3F80) >>> 7), (byte)((0x1FC000 & n2) >>> 14), (byte)((0xFE00000 & n2) >>> 21), (byte)((n2 & 0xF0000000) >>> 28) };
            }
        }
    }

    public static byte getCheckSum(final byte[] array, int i, final int n) {
        if (array == null || i <= 0 || n >= array.length) {
            throw new RuntimeException("\u6570\u636e\u4e0d\u5bf9\u600e\u4e48\u7b97\u6821\u9a8c");
        }
        byte b = 0;
        while (i < n) {
            b += array[i];
            ++i;
        }
        return (byte)(b & 0x7F);
    }

}
