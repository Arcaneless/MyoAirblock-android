package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 16/1/2018.
 */

// Instruction for Turn on/off
public class AirBlockStateInstruction extends AirBlockInstruction {

    public final byte cmd = 67;
    public final byte on = 1;
    public final byte off = 0;
    private byte state = 0;

    public AirBlockStateInstruction(byte state) {
        this.state = state;
    }

    @Override
    public int getCommandLength() {
        return 2;
    }

    @Override
    public void putData(ByteBuffer buffer) {
        buffer
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, cmd))
                .put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, state));
    }
}
