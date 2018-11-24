package com.arcaneless.myoairblock.airblock.respond;

/**
 * Created by marcuscheung on 13/1/2018.
 */

public class AirBlockOffsetAngleRespond extends BleRespond {

    public static final byte cmd = 44;
    public final float angle1;
    public final float angle2;
    public final float angle3;

    public AirBlockOffsetAngleRespond(final float angle1, final float angle2, final float angle3) {
        this.angle1 = angle1;
        this.angle2 = angle2;
        this.angle3 = angle3;
    }

    @Override
    public String toString() {
        return super.toString() + ": Pitch：" + this.angle1 + " Roll：" + this.angle2 + " Yaw：" + this.angle3;
    }

}
