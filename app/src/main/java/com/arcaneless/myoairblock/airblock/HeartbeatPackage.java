package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class HeartbeatPackage {

    public byte[] getBytes() {
        ByteBuffer bb = ByteBuffer.allocate(3 + 3);
        bb.order(ByteOrder.LITTLE_ENDIAN).put((byte)(-16)).put(bb.limit() - 1, (byte)(-9)).position(1);
        bb.put((byte) -1).put((byte) 16) .put((byte) 0);
        // checksum
        bb.put(AirBlockByteUtil.getCheckSum(bb.array(), 1, bb.position()));
        return bb.array();
    }

}
