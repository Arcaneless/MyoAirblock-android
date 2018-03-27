package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockStopInstruction extends AirBlockInstruction {

    private final byte cmd = 2;

    @Override
    public int getCommandLength() {
        return 1;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer.put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd));
    }
}
