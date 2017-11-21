package com.arcaneless.myoairblock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.Vector3;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;
    private TextView linkStatus;
    private TextView syncStatus;
    private TextView lockStatus;
    private TextView poseStatus;
    private TextView armStatus;
    private TextView rotationStatus;


    private AbstractDeviceListener listener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            Toast.makeText(context, "Myo connected!", Toast.LENGTH_SHORT).show();
            linkStatus.setText("LINKED");
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            Toast.makeText(context, "Myo disconnected!", Toast.LENGTH_SHORT).show();
            linkStatus.setText("UNLINKED");
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
            lockStatus.setText(R.string.unlocked);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            lockStatus.setText(R.string.locked);
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
            rotationStatus.setRotationX(pitch);
            rotationStatus.setRotationY(yaw);
        }

        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            switch (pose) {
                case UNKNOWN:
                    poseStatus.setText(getString(R.string.receiving));
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.receiving;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    poseStatus.setText(getString(restTextId));
                    break;
                case FIST:
                    poseStatus.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    poseStatus.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    poseStatus.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    poseStatus.setText(getString(R.string.pose_fingersspread));
                    break;
            }

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
            armStatus.setText(getString(R.string.accel, accel.x(), accel.y(), accel.z()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        
        linkStatus = findViewById(R.id.linkStatus);
        poseStatus = findViewById(R.id.posStatus);
        armStatus = findViewById(R.id.armStatus);
        lockStatus = findViewById(R.id.lockStatus);
        rotationStatus = findViewById(R.id.rotationStatus);

        // Thalmic Hub
        final Hub hub = Hub.getInstance();
        if (!hub.init(this)) {
            Log.e("MYOHUB", "Could not initialize the Hub.");
            finish();
            return;
        }
        hub.setSendUsageData(false);
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);
        hub.addListener(listener);

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hub.getInstance().removeListener(listener);

        if (isFinishing()) {
            Hub.getInstance().shutdown();
        }
    }

    private void onPressedLinkMyo() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private void onPressedLinkAirblock() {
        Intent intent = new Intent(this, BluetoothSelectActivity.class);
        startActivity(intent);
    }
}
