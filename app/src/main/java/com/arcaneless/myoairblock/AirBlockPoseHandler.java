package com.arcaneless.myoairblock;

import android.app.Activity;
import android.widget.TextView;

import com.thalmic.myo.Pose;

/**
 * Created by marcuscheung on 14/10/2018.
 */

// TODO add meaning to the gestures
public class AirBlockPoseHandler implements PoseHandler {

    private AirBlockManager manager;
    private TextView poseStatus;
    private Activity activity;

    AirBlockPoseHandler(Activity activity, AirBlockManager manager, TextView poseStatus) {
        this.manager = manager;
        this.poseStatus = poseStatus;
        this.activity = activity;
    }

    @Override
    public void poseSwitch(Pose pose) {
        switch (pose) {
            case UNKNOWN:
                poseStatus.setText(activity.getString(R.string.receiving));
                break;
            case REST:
                break;
            case DOUBLE_TAP:
                onDoubleTap();
                break;
            case FIST:
                onFist();
                break;
            case WAVE_IN:
                onWaveIn();
                break;
            case WAVE_OUT:
                onWaveOut();
                break;
            case FINGERS_SPREAD:
                onFingerSpread();
                break;
        }
    }

    @Override
    public void onRest() {

    }

    @Override
    public void onDoubleTap() {
        manager.setAirblockState(AirBlockState.ON);
    }

    @Override
    public void onFist() {
        poseStatus.setText(activity.getString(R.string.pose_fist));
    }

    @Override
    public void onWaveIn() {
        poseStatus.setText(activity.getString(R.string.pose_wavein));
        //manager.doInstruction(airblockTakeOff());
    }

    @Override
    public void onWaveOut() {
        poseStatus.setText(activity.getString(R.string.pose_waveout));
        manager.setAirblockState(AirBlockState.OFF);
       // manager.doInstruction(airblockStop());
    }

    @Override
    public void onFingerSpread() {
        //manager.doInstruction(airblockLanding());
        poseStatus.setText(activity.getString(R.string.pose_fingersspread));
    }
}
