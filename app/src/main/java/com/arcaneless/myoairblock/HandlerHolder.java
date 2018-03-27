package com.arcaneless.myoairblock;

import android.os.Handler;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class HandlerHolder {

    public final static Handler handler;
    static {
        handler = new Handler();
    }
}
