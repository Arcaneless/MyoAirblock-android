package com.arcaneless.myoairblock;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.arcaneless.myoairblock.airblock.AirBlockInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory;
import com.arcaneless.myoairblock.airblock.AirBlockLaunchInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockRequestOffsetAngleInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockSetLEDInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockStateInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockStopInstruction;
import com.arcaneless.myoairblock.airblock.HeartbeatPackage;
import com.arcaneless.myoairblock.airblock.respond.AirBlockAngleRespond;
import com.arcaneless.myoairblock.airblock.respond.AirBlockOffsetAngleRespond;
import com.arcaneless.myoairblock.airblock.respond.AirBlockStateRespond;
import com.arcaneless.myoairblock.airblock.respond.AirBlockUltrasonicDistRespond;
import com.arcaneless.myoairblock.airblock.respond.BleRespond;
import com.arcaneless.myoairblock.airblock.respond.BleRespondParser;
import com.thalmic.myo.Vector3;

import java.util.Scanner;
import java.util.UUID;

import ml.xuexin.bleconsultant.BleConsultant;
import ml.xuexin.bleconsultant.entity.BleDevice;
import ml.xuexin.bleconsultant.port.CharacteristicNotifyListener;
import ml.xuexin.bleconsultant.tool.BleLog;

/**
 * Created by marcuscheung on 16/1/2018.
 * A manager of airblock
 * Build a bridge to communicate between bluetooth access on AirBlock and the control program
 */

public class AirBlockManager {

    private static AirBlockManager instance = new AirBlockManager();

    //UUID of airblock
    public static final UUID UUID_NOTIFY = UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_WRITE = UUID.fromString("0000ffe3-0000-1000-8000-00805f9b34fb");

    private BleDevice device = null;
    private BleConsultant consultant = null;
    private BleRespondParser respondParser;
    private Handler handler;

    private Runnable heatbeatRunnable;
    private Runnable turnOnStateRunnable;
    private Runnable angleStateRunnable;

    // AirBlock status
    private boolean turnedOn = false;
    private boolean launched = false;
    // distance between the board and the floor
    private float distance = 0;
    // angle1: Pitch, angle2: roll, angle3: yaw
    private float angle1 = 0;
    private float angle2 = 0;
    private float angle3 = 0;
    private float angle = 0;
    private AirBlockState airblockState = AirBlockState.NOTCONNECTED;

    public AirBlockManager() {
        //handler
        handler = HandlerHolder.handler;

        // runnable
        heatbeatRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("BLE Send Tag", "Heartbeat");
                writeToBluetooth(new HeartbeatPackage().getBytes());
                handler.postDelayed(this, 500L);
            }
        };

        turnOnStateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("BLE Send Tag", "Turn On State");
                writeToBluetooth(new AirBlockStateInstruction((byte)1).getBytes());
                handler.postDelayed(this, 200L);
            }
        };

        angleStateRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("BLE Send Tag", "Offset Angle State");
                writeToBluetooth(new AirBlockRequestOffsetAngleInstruction().getBytes());
                handler.postDelayed(this, 1000L);
            }
        };

        respondParser = new BleRespondParser(new byte[] { -16 }, new byte[] { -9 });
        respondParser.setOnRespondReceiveListener(new BleRespondParser.OnRespondReceiveListener() {
            @Override
            public void onRespondReceive(BleRespond value) {
                Log.v("BLE Converted Receive", value.toString());

                if (value instanceof AirBlockStateRespond) {
                    Log.i("BLE State", "Turned on");
                    handler.removeCallbacks(turnOnStateRunnable);
                }

                if (value instanceof AirBlockUltrasonicDistRespond) {
                    distance = ((AirBlockUltrasonicDistRespond) value).distance;
                }

                if (value instanceof AirBlockOffsetAngleRespond) {
                    AirBlockOffsetAngleRespond offsetAngleRespond = ((AirBlockOffsetAngleRespond) value);
                    angle1 = offsetAngleRespond.angle1;
                    angle2 = offsetAngleRespond.angle2;
                    angle3 = offsetAngleRespond.angle3;
                }

                if (value instanceof AirBlockAngleRespond) {
                    AirBlockAngleRespond angleRespond = ((AirBlockAngleRespond) value);
                    angle = angleRespond.angle3;
                }
            }
        });
    }

    public void initConsultant(Context context) {
        //consultant
        consultant = BleConsultant.getInstance();
        consultant.init(context);

        consultant.printDebugLog(true);// debug

        consultant.openBluetoothSilently();
    }

    public static AirBlockManager getInstance() {
        return instance;
    }

    public BleConsultant getConsultant() {
        return consultant;
    }

    public void initDevice() {
        handler.post(heatbeatRunnable);
        handler.post(turnOnStateRunnable);
        handler.post(angleStateRunnable);
        turnedOn = true;
        doInstruction(new AirBlockSetLEDInstruction((byte) 0, 0, 0, 0));
    }

    public void resetDevice() { // stop the heartbeat and stop
        turnedOn = false;
        handler.removeCallbacks(heatbeatRunnable);
        handler.removeCallbacks(turnOnStateRunnable);
        writeToBluetooth(AirBlockInstructionFactory.airblockStop().getBytes());
    }

    public void doInstruction(AirBlockInstruction instruction) {
        if (instruction instanceof AirBlockLaunchInstruction) launched = true;
        if (instruction instanceof AirBlockStopInstruction) launched = false;

        writeToBluetooth(instruction.getBytes());
    }

    /**
     *  Below functions handle the fundamental operation of the AirBlock
     *  Developers should not modify them
     *  or it will not control the drone
    **/

    // Write
    public void writeToBluetooth(byte[] array) {
        consultant.sendToBle(UUID_SERVICE, UUID_WRITE, array);
    }

    // Notify
    private void readFromBluetooth(byte[] array) {
        if (isFirmwareRespond(array)) {
            Log.e("Firmware version", decodeFirmwareVersion(array));
            return;
        }
        respondParser.parseBytes(array);
    }

    protected void setDevice(BleDevice device) {
        this.device = device;

        consultant.addNotifyListener(UUID_SERVICE, UUID_NOTIFY, new CharacteristicNotifyListener() {
            @Override
            public void onReceive(byte[] value) {
                Log.i("BLE Receive", BleLog.parseByte(value));
                readFromBluetooth(value);
            }
        }, false);
    }

    public BleDevice getDevice() {
        return device;
    }

    // find firware version of airblock
    private static boolean isFirmwareRespond(byte[] value) {
        return value.length > 3 && value[2] == 0 && value[3] == 4;
    }

    private static boolean assertFirmwareValid(final String s) {
        try {
            final Scanner useDelimiter = new Scanner(s).useDelimiter("\\.");
            useDelimiter.nextInt(16);
            useDelimiter.nextInt(16);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private static String decodeFirmwareVersion(final byte[] array) {
        final byte b = array[4];
        String string = "";
        byte b2 = 0;
        String s;
        while (true) {
            s = string;
            if (b2 < b) {
                try {
                    string += String.format("%c", array[b2 + 5]);
                    ++b2;
                    continue;
                }
                catch (Exception ex) {
                    s = "";
                }
                break;
            }
            break;
        }
        String s2 = s;
        if (!assertFirmwareValid(s)) {
            s2 = "";
        }
        return s2;
    }


    /**
     * The below functions try to get the status of the drone
     * Including its position and power state
     */


    // Get distance between floor
    public float getDistance() {
        return distance;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

    public boolean isLaunched() {
        return launched;
    }

    public AirBlockState getAirblockState() {
        return airblockState;
    }

    public void setAirblockState(AirBlockState state) {
        airblockState = state;
    }

    public Vector3 getAngle() {
        return new Vector3(angle1, angle2, angle3);
    }

    public float getTestAngle() {
        return angle;
    }
}
