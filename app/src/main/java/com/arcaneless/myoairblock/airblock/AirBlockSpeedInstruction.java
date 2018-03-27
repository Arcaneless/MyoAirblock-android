package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockSpeedInstruction extends AirBlockInstruction {

    private static final byte cmd = 38;
    private final short speed1;
    private final short speed2;
    private final short speed3;
    private final short speed4;

    public AirBlockSpeedInstruction(short speed1, short speed2, short speed3, short speed4) {
        this.speed1 = speed1;
        this.speed2 = speed2;
        this.speed3 = speed3;
        this.speed4 = speed4;
    }

    @Override
    public int getCommandLength() {
        return 9;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, speed1))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, speed2))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, speed3))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, speed4));
    }
}
