package com.arcaneless.myoairblock;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.arcaneless.myoairblock.airblock.AirBlockInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory;
import com.arcaneless.myoairblock.airblock.AirBlockLaunchInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockStateInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockStopInstruction;
import com.arcaneless.myoairblock.airblock.HeartbeatPackage;
import com.arcaneless.myoairblock.airblock.respond.AirBlockStateRespond;
import com.arcaneless.myoairblock.airblock.respond.AirBlockUltrasonicDistRespond;
import com.arcaneless.myoairblock.airblock.respond.BleRespond;
import com.arcaneless.myoairblock.airblock.respond.BleRespondParser;

import java.util.Scanner;
import java.util.UUID;

import ml.xuexin.bleconsultant.BleConsultant;
import ml.xuexin.bleconsultant.entity.BleDevice;
import ml.xuexin.bleconsultant.port.CharacteristicNotifyListener;
import ml.xuexin.bleconsultant.tool.BleLog;

/**
 * Created by marcuscheung on 16/1/2018.
 */

// manager of airblock
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

    // AirBlock status
    private boolean turnedOn = false;
    private boolean launched = false;
    private float distance = 0;

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
                Log.i("BLE Send Tag", "Turn on state");
                writeToBluetooth(new AirBlockStateInstruction((byte)1).getBytes());
                handler.postDelayed(this, 200L);
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
        turnedOn = true;
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




    //  GETTER


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
}