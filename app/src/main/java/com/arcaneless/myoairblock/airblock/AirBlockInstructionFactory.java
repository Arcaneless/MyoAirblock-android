package com.arcaneless.myoairblock.airblock;

/**
 * Created by marcuscheung on 16/1/2018.
 */

public class AirBlockInstructionFactory {

    public static AirBlockInstruction airblockLaunch() {
        return new AirBlockLaunchInstruction();
    }

    public static AirBlockInstruction airblockStop() {
        return new AirBlockStopInstruction();
    }

    public static AirBlockInstruction airblockLanding() {
        return new AirBlockLandingInstruction(AirBlockLandingInstruction.Landing);
    }

    public static AirBlockInstruction airblockTakeOff() {
        return new AirBlockLandingInstruction(AirBlockLandingInstruction.TakeOff);
    }

    public static AirBlockInstruction airblockBoardCalibrate() {
        return new AirBlockCalibrationInstruction(AirBlockCalibrationInstruction.TYPE_BOARD);
    }

    public static AirBlockInstruction airblockGyroCalibrate() {
        return new AirBlockCalibrationInstruction(AirBlockCalibrationInstruction.TYPE_GYROSCOPE);
    }

    public static AirBlockInstruction airblockControlWord(int n, float n2) {
        switch (n) {
            default: {
                return null;
            }
            case 1: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_BACKWARD, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 0: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_FORWARD, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 2: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_LEFT, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 3: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_RIGHT, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 4: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_UP, 0.0f, n2, 0.0f, 1.5f, 0.0f, 0.0f);
            }
            case 5: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_DOWN, 0.0f, n2, 0.0f, -0.8f, 0.0f, 0.0f);
            }
            case 10: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_BALANCE, 0.0f, n2, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 11: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_ROLL, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
            }
            case 13: {
                return new AirBlockControlWordInstruction(AirBlockControlWordInstruction.WORD_SHAKE, 20.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f);
            }
        }
    }

    public static AirBlockInstruction airblockSpeed(short speed, short speed2) {
        return new AirBlockSpeedInstruction(speed, speed2, speed2, speed);
    }


}
