# MyoAirblock-android
Remotely hack into Airblock: closed source drone with Bluetooth and bind human arm gestures with Myo

## Background
Back in the secondary school, my teacher saw a drone that can be controlled by human gesture, and wonder if I could do the same. 
The only problem is that the drone we have bought is closed sourced. Since we have ran out of budget, we have to hack into the drone and tune the accelerations for our use case.

## Airblock
Created by MakeBlock, a stem toy drone for education. It can be programmed using its blocky program on android or ios. Closed-source.

## Myo armband
Able to detect arm gestures by wearing it on arm. Simple. Sdk is provided.

## Method
I have "investigated" the android app and found out the communication method of the drone. The BLE protocol of the drone had been obtained. 
After calibration and writing connection controller to the armband. Using an android app as a mediator, I am able to control the up, down, forward, backward, LED motion of the drone successfully.

## Things can be improved
- The acceleration can be fine-tuned and optimized.

- More controls should be supported.

- A more user-friendly GUI should be made.
