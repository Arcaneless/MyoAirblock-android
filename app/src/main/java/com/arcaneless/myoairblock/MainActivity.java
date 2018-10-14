package com.arcaneless.myoairblock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arcaneless.myoairblock.airblock.AirBlockControlWordInstruction;
import com.arcaneless.myoairblock.airblock.AirBlockGetInstruction;
import com.arcaneless.myoairblock.airblock.action.AirActionHandler;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;

import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockControlWord;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockGyroCalibrate;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockLanding;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockLaunch;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockSpeed;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockTakeOff;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    public AirBlockManager airblockManager;
    private PoseHandler poseHandler;


    private TextView linkStatus;
    private TextView syncStatus;
    private TextView lockStatus;
    private TextView poseStatus;
    private TextView armStatus;
    private TextView rotationStatus;
    private TextView airblockStatus;


    private AbstractDeviceListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        airblockManager = AirBlockManager.getInstance();
        airblockManager.initConsultant(this);

        // AirAction handler
        AirActionHandler.initActionHandler();


        
        linkStatus = findViewById(R.id.linkStatus);
        poseStatus = findViewById(R.id.posStatus);
        armStatus = findViewById(R.id.armStatus);
        lockStatus = findViewById(R.id.lockStatus);
        rotationStatus = findViewById(R.id.rotationStatus);
        airblockStatus = findViewById(R.id.airblockStatus);

        // Thalmic Hub
        final Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e("MYOHUB", "Could not initialize the Hub.");
            finish();
            return;
        }
        hub.setSendUsageData(false);
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        listener = new AirBlockMyoLinker(this, airblockManager);

        hub.addListener(listener);

        // Ble Consultant


        Button button = findViewById(R.id.linkMyo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPressedLinkMyo();
            }
        });

        Button button2 = findViewById(R.id.linkAirblock);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPressedLinkAirblock();
            }
        });

        Button test = findViewById(R.id.test_airblock);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPressedTest();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeListener(listener);

        if (isFinishing()) {
            Hub.getInstance().shutdown();
        }

        airblockManager.resetDevice();
        airblockManager.getConsultant().disconnect();
    }

    private void onPressedLinkMyo() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void onPressedLinkAirblock() {
        Intent intent = new Intent(this, BluetoothSelectActivity.class);
        startActivity(intent);
    }

    private void onPressedTest() {
        if (airblockManager.getDevice() != null) {
            final Thread getThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int i = 0;
                        while(airblockManager.isLaunched()) {
                            airblockManager.doInstruction(AirBlockGetInstruction.GET.ULTRASONICDISTANCE.instruction());
                            Thread.sleep(50L);
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            // Thread main
            new Thread() {
                @Override
                public void run() {
                    try {
                        airblockManager.initDevice();
                        Thread.sleep(1000L);
                        airblockManager.doInstruction(airblockGyroCalibrate());

                        airblockManager.doInstruction(airblockSpeed((short) 0, (short) 0));
                        airblockManager.doInstruction(airblockLaunch());
                        getThread.start();
                        Thread.sleep(1000L);
                        airblockManager.doInstruction(airblockTakeOff());
                        Thread.sleep(100L);
                        //Thread.sleep(1000L);
                        int i = 0;
                        while (i < 2000) {
                            if (airblockManager.getDistance() < 1.4)
                                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_HOVER, 1F));
                            else if (airblockManager.getDistance() > 1.6)
                                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_DOWN, 1F));
                            //airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_BALANCE, 0));
                            i += 200;
                            Thread.sleep(200L);
                        }
                        //Thread.sleep(2000L);
                        while(airblockManager.getDistance() > 1) {
                            airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_DOWN, 1F));
                        }
                        airblockManager.doInstruction(airblockLanding());
                        Thread.sleep(500L);
                        airblockManager.resetDevice();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        }

    }


}
