package com.arcaneless.myoairblock;

import com.thalmic.myo.Pose;

/**
 * Created by marcuscheung on 14/10/2018.
 */

public interface PoseHandler {

    void poseSwitch(Pose pose);

    void onRest();
    void onDoubleTap();
    void onFist();
    void onWaveIn();
    void onWaveOut();
    void onFingerSpread();



}
