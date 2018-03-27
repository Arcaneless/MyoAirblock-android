package com.arcaneless.myoairblock.airblock.action;

import android.os.Handler;

import com.arcaneless.myoairblock.AirBlockManager;
import com.arcaneless.myoairblock.airblock.AirBlockInstruction;

import java.util.ArrayList;

/**
 * Created by marcuscheung on 7/1/2018.
 */

// A series of Runnable with different intervals
public class AirAction {

    private Handler handler;
    private ArrayList<Long> intervals;
    private ArrayList<AirBlockInstruction> airInstructions;
    private String name;

    public AirAction(Handler handler, ArrayList<AirBlockInstruction> runnables, ArrayList<Long> intervals, String name) {
        this.handler = handler;
        this.airInstructions = runnables;
        this.intervals = intervals;
        this.name = name;
    }

    public void execute() {
        int n = 0;
        for (int i = 0; i < airInstructions.size(); i++) {
            n += intervals.get(i);
            final int j = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    AirBlockManager.getInstance().writeToBluetooth(airInstructions.get(j).getBytes());
                }
            };
            handler.postDelayed(runnable, n);
        }
    }
}
