package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockLandingInstruction extends AirBlockInstruction {

    public static final byte Landing = 5;
    public static final byte Manual = 1;
    public static final byte TakeOff = 8;
    private static final byte cmd = 11;
    private final byte mode;

    public AirBlockLandingInstruction(byte mode) {
        this.mode = mode;
    }

    @Override
    public int getCommandLength() {
        return 2;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, mode));
    }
}
