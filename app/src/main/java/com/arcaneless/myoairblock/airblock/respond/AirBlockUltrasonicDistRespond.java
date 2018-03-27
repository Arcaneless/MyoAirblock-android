package com.arcaneless.myoairblock.airblock.respond;

/**
 * Created by marcuscheung on 15/2/2018.
 */

public class AirBlockUltrasonicDistRespond extends BleRespond {

    public static final byte cmd = 0x54;
    public final float distance;

    public AirBlockUltrasonicDistRespond(final float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Distance between floor: " + distance + " m";
    }
}
