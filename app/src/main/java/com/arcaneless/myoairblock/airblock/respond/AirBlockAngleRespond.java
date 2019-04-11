package com.arcaneless.myoairblock.airblock.respond;

public class AirBlockAngleRespond extends BleRespond {

    public static final int cmd = 0x2C;
    public final float angle1;
    public final float angle2;
    public final float angle3;

    public AirBlockAngleRespond(float angle1, float angle2, float angle3) {
        this.angle1 = angle1;
        this.angle2 = angle2;
        this.angle3 = angle3;
    }

    @Override
    public String toString() {
        return "Angle1: " + angle1 + ",Angle2: " + angle2 + ", Angle3: " + angle3;
    }
}
