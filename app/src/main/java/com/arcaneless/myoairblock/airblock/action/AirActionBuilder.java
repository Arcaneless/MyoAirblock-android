package com.arcaneless.myoairblock.airblock.action;

import com.arcaneless.myoairblock.airblock.AirBlockInstruction;

import java.util.ArrayList;

/**
 * Created by marcuscheung on 7/1/2018.
 */

public class AirActionBuilder {

    private ArrayList<Long> intervals;
    private ArrayList<AirBlockInstruction> airInstructions;
    private String name;

    public AirActionBuilder() {
        airInstructions = new ArrayList<>();
        intervals = new ArrayList<>();
    }

    public AirActionBuilder append(AirBlockInstruction instruction, long interval) {
        airInstructions.add(instruction);
        intervals.add(interval);
        return this;
    }

    public AirAction build() {
        return new AirAction(AirActionHandler.getHandler(), airInstructions, intervals, name);
    }

}
