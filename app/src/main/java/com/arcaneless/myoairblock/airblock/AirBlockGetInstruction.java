package com.arcaneless.myoairblock.airblock;

import java.nio.ByteBuffer;

/**
 * Created by marcuscheung on 28/1/2018.
 */

public class AirBlockGetInstruction extends AirBlockInstruction {

    public byte command;

    public enum GET {
        ELECTRICITY(0x40),
        LOCATION(0x25),
        SPEED(0x21),
        HEIGHTTYPE(0x15),
        ATTITUDE(0x23),
        BOARDOFFSETANGLE(0x2C),
        LEDNUMBER(0x50),
        ULTRASONICDISTANCE(0x54),
        AIRPRESSURE(0x55),
        MOTORCURRENT(0x56);

        private byte command;

        GET(int command) {
            this.command = (byte) command;
        }

        public AirBlockInstruction instruction() {
            return new AirBlockGetInstruction(command);
        }
    }

    public AirBlockGetInstruction(byte command) {
        this.command = command;
    }


    @Override
    public int getCommandLength() {
        return 1;
    }

    @Override
    protected void putData(ByteBuffer buffer) {
        buffer.put(AirBlockByteUtil.convert8to7(AirBlockByteUtil.SIZE_BYTE, command));
    }
}
