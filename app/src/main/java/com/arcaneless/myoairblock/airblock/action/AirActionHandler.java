package com.arcaneless.myoairblock.airblock.action;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by marcuscheung on 7/1/2018.
 */

public class AirActionHandler {

    private static Handler handler;

    public static Handler getHandler() {
        if (handler == null) throw new RuntimeException("AirAction handler is not initialized");
        return handler;
    }

    public static void initActionHandler() {
        final HandlerThread handlerThread = new HandlerThread("AirActionHandlerThread");
        handlerThread.start();
        AirActionHandler.handler = new Handler(handlerThread.getLooper());
    }

}
