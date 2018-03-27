package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public abstract class AirBlockInstruction {

    public final byte Head = -16;
    public final byte Tail = -9;

    private ByteBuffer getByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(getCommandLength() + 2 + 3); // Airblock identifier(2) and Head tail checksum(3)
        // original Neuron Instruct
        buffer.order(ByteOrder.LITTLE_ENDIAN).put(Head).put(buffer.limit() - 1, Tail).position(1);
        // orig Airblock Instruct
        buffer.put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, 1));
        buffer.put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, 95));
        return buffer;
    }

    public abstract int getCommandLength();

    protected abstract void putData(ByteBuffer buffer);

    public byte[] getBytes() {
        ByteBuffer buffer = getByteBuffer();
        putData(buffer);
        buffer.put(AirBlockByteUtil.getCheckSum(buffer.array(), 1, buffer.position()));
        return buffer.array();
    }

    protected final int parseToIntBit(float paramFloat) {
        return Float.floatToIntBits(paramFloat);
    }

}
