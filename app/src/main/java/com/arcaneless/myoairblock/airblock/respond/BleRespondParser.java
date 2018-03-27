package com.arcaneless.myoairblock.airblock.respond;

import android.util.Log;

import com.arcaneless.myoairblock.airblock.AirBlockByteUtil;
import com.arcaneless.myoairblock.airblock.AirBlockGetInstruction;
import com.arcaneless.myoairblock.airblock.respond.AirBlockOffsetAngleRespond;
import com.arcaneless.myoairblock.airblock.respond.AirBlockStateRespond;
import com.arcaneless.myoairblock.airblock.respond.BleRespond;

import ml.xuexin.bleconsultant.tool.BleLog;

/**
 * Created by marcuscheung on 13/1/2018.
 */

// respond packager for airblock
public class BleRespondParser {

    private byte[] buffer;
    private byte head[];
    private byte tail[];
    private int length;
    private OnRespondReceiveListener onRespondReceiveListener;

    public BleRespondParser(final byte[] head, final byte[] tail) {
        this.buffer = new byte[1000];
        this.length = 0;
        this.head = head;
        this.tail = tail;
    }

    // extract specific amount of bytes, just like unshift
    private byte[] extractBytes(final int n) {
        final byte[] array = new byte[n];
        System.arraycopy(this.buffer, 0, array, 0, n);
        System.arraycopy(this.buffer, n, this.buffer, 0, this.length - n);
        this.length -= n;
        return array;
    }

    private int findHeadIndex() {
        for (int i = 0; i <= this.length - this.head.length; ++i) {
            int n = 0;
            boolean b2;
            while (true) {
                b2 = true;
                if (n >= this.head.length) {
                    break;
                }
                if (this.buffer[i + n] != this.head[n]) {
                    b2 = false;
                    break;
                }
                ++n;
            }
            if (b2) {
                return i;
            }
        }
        return -1;
    }

    private int findTailIndex() {
        for (int i = this.head.length; i <= this.length - this.tail.length; ++i) {
            int n = 0;
            boolean b2;
            while (true) {
                b2 = true;
                if (n >= this.tail.length) {
                    break;
                }
                if (this.buffer[i + n] != this.tail[n]) {
                    b2 = false;
                    break;
                }
                ++n;
            }
            if (b2) {
                return i;
            }
        }
        return -1;
    }

    private boolean check(final byte[] array) {
        if (array != null && array.length > 3) {
            byte b = 0;
            for (int i = 1; i < array.length - 2; ++i) {
                b += array[i];
            }
            if ((byte)(b & 0x7F) == array[array.length - 2]) {
                return true;
            }
        }
        return false;
    }

    private void parseData() {
        if (this.length >= this.head.length + this.tail.length) {
            if (this.length > this.buffer.length * 2 / 3) {
                this.extractBytes(this.buffer.length / 2);
            }
            final int headIndex = this.findHeadIndex();
            if (headIndex > 0) {
                this.extractBytes(headIndex);
                this.parseData();
                return;
            }
            if (headIndex >= 0) {
                final int tailIndex = this.findTailIndex();
                if (tailIndex > 0) {
                    this.packData(this.extractBytes(this.tail.length + tailIndex));
                    this.parseData();
                }
            }
        }
    }

    private void packData(final byte[] array) {
        Log.e("BYTES:", BleLog.parseByte(array));
        BleRespond neuronRespond2;
        final BleRespond neuronRespond = neuronRespond2 = null;
        if (this.check(array)) {
            switch (array[3]) {
                default: {
                    neuronRespond2 = neuronRespond;
                    break;
                }
                case 67: {
                    final int convert7to8 = AirBlockByteUtil.convert7to8(array, 4, 5);
                    final int n = 4 + 1;
                    neuronRespond2 = new AirBlockStateRespond(convert7to8, AirBlockByteUtil.convert7to8(array, n, 6), AirBlockByteUtil.convert7to8(array, n + 1, 7));
                    break;
                }
                case 44: {
                    final float intBitsToFloat = Float.intBitsToFloat(AirBlockByteUtil.convert7to8(array, 4, 9));
                    final int n2 = 4 + 5;
                    neuronRespond2 = new AirBlockOffsetAngleRespond(intBitsToFloat, Float.intBitsToFloat(AirBlockByteUtil.convert7to8(array, n2, 14)), Float.intBitsToFloat(AirBlockByteUtil.convert7to8(array, n2 + 5, 19)));
                    break;
                }
                case 0x54: {
                    final float distance = Float.intBitsToFloat(AirBlockByteUtil.convert7to8(array, 4, 10));
                    neuronRespond2 = new AirBlockUltrasonicDistRespond(distance);
                }
            }
        }
        if (this.onRespondReceiveListener != null && neuronRespond2 != null) {
            this.onRespondReceiveListener.onRespondReceive(neuronRespond2);
        }
    }



    public void parseBytes(byte[] array) {
        int length = this.length;
        this.length += array.length;
        if (this.length > this.buffer.length) {
            throw new RuntimeException("Buffer overflow");
        }
        System.arraycopy(array, 0, this.buffer, length, array.length);
        this.parseData();
    }

    public void setOnRespondReceiveListener(final OnRespondReceiveListener onRespondReceiveListener) {
        this.onRespondReceiveListener = onRespondReceiveListener;
    }

    public interface OnRespondReceiveListener
    {
        void onRespondReceive(final BleRespond p0);
    }

}
