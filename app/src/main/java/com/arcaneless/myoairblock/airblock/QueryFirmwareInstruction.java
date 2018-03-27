package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class QueryFirmwareInstruction {

    private byte[] Head = { -1, 85 };

    public byte[] getBytes() {
        ByteBuffer localByteBuffer = ByteBuffer.allocate(Head.length + 3 + 1);
        localByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        localByteBuffer.put(Head);
        localByteBuffer.put((byte)3);
        localByteBuffer.put((byte)0);
        localByteBuffer.put((byte)1);
        localByteBuffer.put((byte)0);
        return localByteBuffer.array();
    }

}
