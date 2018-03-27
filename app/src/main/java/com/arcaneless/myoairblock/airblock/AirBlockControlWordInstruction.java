package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockControlWordInstruction extends AirBlockInstruction {

    public static final byte WORD_BACKWARD = 1;
    public static final byte WORD_BALANCE = 10;
    public static final byte WORD_DOWN = 5;
    public static final byte WORD_FORWARD = 0;
    public static final byte WORD_FREE = 30;
    public static final byte WORD_GROUP = 12;
    public static final byte WORD_HOVER = 9;
    public static final byte WORD_LEFT = 2;
    public static final byte WORD_RIGHT = 3;
    public static final byte WORD_ROLL = 11;
    public static final byte WORD_ROTATE = 6;
    public static final byte WORD_SHAKE = 13;
    public static final byte WORD_UP = 4;
    private static final byte cmd = 13;
    private final float data1;
    private final float data2;
    private final float data3;
    private final float data4;
    private final float data5;
    private final float data6;
    private final byte word;

    public AirBlockControlWordInstruction(byte word, float data1, float data2, float data3, float data4, float data5, float data6)
    {
        this.word = word;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = data5;
        this.data6 = data6;
    }

    @Override
    public int getCommandLength() {
        return 32;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, word))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data1)))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data2)))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data3)))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data4)))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data5)))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.TYPE_float, parseToIntBit(data6)));
    }
}
