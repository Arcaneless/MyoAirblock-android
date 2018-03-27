package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockSetLEDInstruction extends AirBlockInstruction {

    private final byte cmd = 0x51;
    private byte no; // 0, 1, 2, 3
    private int no1, no2, no3;

    public AirBlockSetLEDInstruction(byte no, int no1, int no2, int no3) {
        this.no = no;
        this.no1 = no1;
        this.no2 = no2;
        this.no3 = no3;
    }

    @Override
    public int getCommandLength() {
        return 1 + 1 + 3 * 3;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, no))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, no1))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, no2))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_short, no3));
    }
}
