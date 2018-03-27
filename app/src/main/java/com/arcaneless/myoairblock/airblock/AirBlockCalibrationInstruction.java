package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockCalibrationInstruction extends AirBlockInstruction {

    public static final byte TYPE_BOARD = 2;
    public static final byte TYPE_GYROSCOPE = 1;
    private static final byte cmd = 58;
    private byte type;

    public AirBlockCalibrationInstruction(byte type) {
        this.type = type;
    }

    @Override
    public int getCommandLength() {
        return 2;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, type));
    }
}
