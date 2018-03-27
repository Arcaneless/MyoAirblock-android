package com.arcaneless.myoairblock.airblock.respond;

import com.arcaneless.myoairblock.airblock.respond.BleRespond;

/**
 * Created by marcuscheung on 13/1/2018.
 */

public class AirBlockStateRespond extends BleRespond {

    public static final byte cmd = 67;
    public final float battery;
    public final boolean land;
    public final boolean switchOn;

    public AirBlockStateRespond(final float n, final int n2, final int n3) {
        this.battery = n / 100.0f;
        this.switchOn = (n2 == 1);
        this.land = (n3 == 1);
    }

    @Override
    public String toString() {
        return super.toString() + ": \u72b6\u6001\uff0c\u7535\u91cf\uff1a" + this.battery + " \u5f00\u5173\u6253\u5f00\uff1a" + this.switchOn + " \u7740\u9646\uff1a" + this.land;
    }

}
