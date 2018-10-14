package com.arcaneless.myoairblock;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.arcaneless.myoairblock.airblock.AirBlockControlWordInstruction;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;

import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockBoardCalibrate;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockControlWord;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockGyroCalibrate;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockLanding;
import static com.arcaneless.myoairblock.airblock.AirBlockInstructionFactory.airblockStop;

/**
 * Created by marcuscheung on 14/10/2018.
 */

public class AirBlockMyoLinker extends AbstractDeviceListener {

    private Activity activity;
    private TextView linkStatus;
    private TextView syncStatus;
    private TextView lockStatus;
    private TextView poseStatus;
    private TextView armStatus;
    private TextView rotationStatus;
    private TextView airblockStatus;

    private AirBlockManager airblockManager;
    private PoseHandler poseHandler;

    AirBlockMyoLinker(Activity mainActivity, AirBlockManager manager) {
        this.activity = mainActivity;
        linkStatus = activity.findViewById(R.id.linkStatus);
        poseStatus = activity.findViewById(R.id.posStatus);
        armStatus = activity.findViewById(R.id.armStatus);
        lockStatus = activity.findViewById(R.id.lockStatus);
        rotationStatus = activity.findViewById(R.id.rotationStatus);
        airblockStatus = activity.findViewById(R.id.airblockStatus);

        airblockManager = manager;
        poseHandler = new AirBlockPoseHandler(activity, airblockManager, poseStatus);
    }

    // Myo input, AirBlock output

    @Override
    public void onConnect(Myo myo, long timestamp) {
        Toast.makeText(activity, "Myo connected!", Toast.LENGTH_SHORT).show();
        linkStatus.setText("LINKED");
        airblockManager.initDevice();
        airblockManager.doInstruction(airblockGyroCalibrate());
        airblockManager.doInstruction(airblockBoardCalibrate());
    }

    @Override
    public void onDisconnect(Myo myo, long timestamp) {
        Toast.makeText(activity, "Myo disconnected!", Toast.LENGTH_SHORT).show();
        linkStatus.setText("UNLINKED");
        airblockManager.resetDevice();
    }

    @Override
    public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {

    }

    // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
    // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
    // when Myo is moved around on the arm.
    @Override
    public void onArmUnsync(Myo myo, long timestamp) {


    }

    // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
    // policy, that means poses will now be delivered to the listener.
    @Override
    public void onUnlock(Myo myo, long timestamp) {

    }

    // onLock() is called whenever a synced Myo has been locked. Under the standard locking
    // policy, that means poses will no longer be delivered to the listener.
    @Override
    public void onLock(Myo myo, long timestamp) {

    }

    // onOrientationData() is called whenever a Myo provides its current orientation,
    // represented as a quaternion.
    @Override
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        // Adjust roll and pitch for the orientation of the Myo on the arm.
        if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
            roll *= -1;
            pitch *= -1;
        }

        // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
        rotationStatus.setRotation(roll);
        rotationStatus.setText(String.valueOf(yaw));
        rotationStatus.setRotationX(pitch);

        if (airblockManager.isLaunched()) {
            if (roll > 1) {
                airblockManager.setAirblockState(AirBlockState.LEFTWARD);
            } else if (roll < -1) {
                airblockManager.setAirblockState(AirBlockState.RIGHTWARD);
            } else {
                airblockManager.setAirblockState(AirBlockState.HOVER);
            }

            // up
            if (yaw > 1) {
                airblockManager.setAirblockState(AirBlockState.UPWARD);
            } else if (yaw < -1) {
                airblockManager.setAirblockState(AirBlockState.DOWNWARD);
            } else {
                airblockManager.setAirblockState(AirBlockState.HOVER);
            }
        }


    }

    @Override
    public void onPose(Myo myo, long timestamp, Pose pose) {
        poseHandler.poseSwitch(pose);

        if (pose != Pose.UNKNOWN && pose != Pose.REST) {
            //myo.unlock(Myo.UnlockType.HOLD);

            // Notify the Myo that the pose has resulted in an action, in this case changing
            // the text on the screen. The Myo will vibrate.
            myo.notifyUserAction();
        } else {
            // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
            // stay unlocked while poses are being performed, but lock after inactivity.
            //myo.unlock(Myo.UnlockType.TIMED);
        }
    }

    @Override
    public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
        armStatus.setText(activity.getString(R.string.accel, accel.x(), accel.y(), accel.z()));

        if (accel.x() > 2) {
            airblockManager.setAirblockState(AirBlockState.BACKWARD);
            myo.notifyUserAction();
        }
        if (accel.x() < -2) {
            airblockManager.setAirblockState(AirBlockState.FORWARD);
            myo.notifyUserAction();
        }



        new Thread() {
            @Override
            public void run() {
                try {
                    airBlockStateExecution();
                    Thread.sleep(200L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void airBlockStateExecution() {
        airblockStatus.setText(airblockManager.getAirblockState().toString());
        switch (airblockManager.getAirblockState()) {
            case FORWARD:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_FORWARD, 0.1F));
                break;
            case BACKWARD:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_BACKWARD, 0.1F));
                break;
            case DOWNWARD:
                airblockManager.doInstruction(airblockLanding());
                break;
            case UPWARD:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_UP, 0.1F));
            case LEFTWARD:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_LEFT, 0.1F));
                break;
            case RIGHTWARD:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_RIGHT, 0.1F));
                break;
            case HOVER:
                airblockManager.doInstruction(airblockControlWord(AirBlockControlWordInstruction.WORD_HOVER, 0.1F));
                break;
            case OFF:
                airblockManager.doInstruction(airblockStop());
            default:
                break;
        }
    }
}
