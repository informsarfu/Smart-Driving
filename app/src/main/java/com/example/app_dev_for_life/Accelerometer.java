package com.example.app_dev_for_life;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

class RateLimiter {
    private final long maxRequestsPerSecond;
    private final long intervalInMillis;
    private final AtomicLong lastRequestTime = new AtomicLong(0);

    public RateLimiter(long maxRequestsPerSecond) {
        this.maxRequestsPerSecond = maxRequestsPerSecond;
        this.intervalInMillis = TimeUnit.SECONDS.toMillis(1);
        Log.i("project4_RATE", "RATE LIMITER CONSTRUCTOR");
    }

    public boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        long lastTime = lastRequestTime.get();
        long elapsed = currentTime - lastTime;

        Log.i("project4_RATE", String.format("Elapsed = %d", elapsed));

        if (elapsed < intervalInMillis) {
            // Too many requests made in a short time period
            Log.i("project4_RATE", "NOT ALLOWING THIS");
            return false;
        }

        if (lastRequestTime.compareAndSet(lastTime, currentTime)) {
            // Request allowed, update last request time
            Log.i("project4_RATE", "ALLOWING THIS");
            return true;
        } else {
            Log.i("project4_RATE", "ELSE NOT ALLOWING THIS");
            return false;
        }
    }

}

public class Accelerometer extends Service implements SensorEventListener {

    private SensorManager accelerometer;
    private Sensor sensorShake;

    private float brakeThreshold = 0.85f;  // Adjust this threshold as needed
    private float laneChangeThreshold = 1.5f;  // Adjust this threshold as needed

    private boolean brakeDetected = false;
    private boolean laneChangeDetected = false;

    private ArrayList<Float> brakeValues = new ArrayList<>();
    private ArrayList<Float> laneChangeValues = new ArrayList<>();
    private ArrayList<Integer> accelX = new ArrayList<>();
    private ArrayList<Integer> accelY = new ArrayList<>();
    private ArrayList<Integer> accelZ = new ArrayList<>();

    private int respRateValue;

    float deltaX = 0;
    float deltaY = 0;
    float deltaZ = 0;

    float currentValue = 0F;
    float previousValue = 0F;

    int k = 0;

    private float mLastX = 0 , mLastY = 0, mLastZ = 0;
    private boolean mInitialized;

    private int ignoreFirst = 0;

    int considered_requests = 0;

    RateLimiter rateLimiter = new RateLimiter(5); // Allow up to 5 requests per second

    @Override
    public void onCreate(){

//        Toast.makeText(getApplicationContext(), "Accelerometer object created...",
//                Toast.LENGTH_SHORT).show();

        accelerometer = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //
        sensorShake = accelerometer.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register the event listener for accelerometer sensor data
//        accelerometer.registerListener(this, sensorShake, SensorManager.SENSOR_DELAY_NORMAL);
        accelerometer.registerListener(this, sensorShake, 1000000);

        mInitialized = false;
        considered_requests = 0;
    }

    protected void onResume() {
        accelerometer.registerListener(this, sensorShake, 1000000);
    }
    protected void onPause() {
        accelerometer.unregisterListener(this);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

    @Override
    public int onStartCommand(Intent in, int flags, int id) {
        Toast.makeText(getApplicationContext(), "Started Measuring the Spatial Data Rate",
                Toast.LENGTH_SHORT).show();
        accelX.clear();
        accelY.clear();
        accelZ.clear();
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i("log", "onSensorChanged event received succesfully");

        Sensor sensorId = event.sensor;
        if (event != null) {
            Log.i("log", "checking if it's accelerometer event");
            if (sensorId.getType() == Sensor.TYPE_ACCELEROMETER) {

                Log.i("log", "Accelerometer event detected succesfully");

                if (rateLimiter.allowRequest()) {

                    Log.i("project4_ACCEL","Request Allowed");

                    if (!mInitialized) {
                        Log.i("log", "mInit not true");
                        mLastX = event.values[0];
                        mLastY = event.values[1];
                        mLastZ = event.values[2];
                        mInitialized = true;
                    } else {
                        Log.i("log", "mInit TRUE");
                        deltaX = Math.abs(mLastX - event.values[0]);
                        deltaY = Math.abs(mLastY - event.values[1]);
                        deltaZ = Math.abs(mLastZ - event.values[2]);
                    }

                    //Saves the accelerometer sensor recorded data
                    accelX.add((int) (event.values[0]));
                    accelY.add((int) (event.values[1]));
                    accelZ.add((int) (event.values[2]));
                    float acceleration = (float) Math.sqrt(
                            Math.pow(deltaX, 2.0) + Math.pow(deltaY, 2.0) + Math.pow(deltaZ, 2.0)
                    );

                    // Check for sudden brakes
                    if (acceleration > brakeThreshold) {
                        brakeDetected = true;
                        brakeValues.add(acceleration);
                    } else {
                        brakeDetected = false;
                    }

                    // Check for lane changes
                    if (deltaX > laneChangeThreshold) {
                        laneChangeDetected = true;
                        laneChangeValues.add(deltaX);
                    } else {
                        laneChangeDetected = false;
                    }


                    Log.i("RESP_RATE", String.format("X Value -->  %f", event.values[0]));
                    Log.i("RESP_RATE", String.format("Y Value --> %f", event.values[1]));
                    Log.i("RESP_RATE", String.format("Z Value --> %f", event.values[2]));

                    ignoreFirst++;

                    if(ignoreFirst >=5) {
                        Log.i("log", "Ignoring Done...Reality NOW");
                        considered_requests++;
                        currentValue = (float) Math.sqrt(
                                Math.pow(deltaX, 2.0) + Math.pow(deltaY, 2.0) + Math.pow(deltaZ, 2.0)
                        );
                        float delta = Math.abs(currentValue - previousValue);
                        if (delta > 0.85) {
                            k++;
                        }
                        if (brakeDetected) {
                            Log.i("EVENT", "Sudden Brake Detected");
                            // Handle sudden brake event
                        }

                        if (laneChangeDetected) {
                            Log.i("EVENT", "Lane Change Detected");
                            // Handle lane change event
                        }
                        // x axis - lane change ( left to right lanes change - values) - lane change
                        // front back front back, break and stop - axis changes.

                        // add a threshold for both values - and return those values ( for peak detection )
                        previousValue = currentValue;

                        //Sensing stops after 45s at approximately 2250 data points - 1 event every 20ms
                        //Not implementing rate adaptation code as it wasn't stated in the project description.
                        if (accelX.size() >= 60) {
                            stopSelf();
    //                        float ret = (float) (k / 20.00); //1 per 20ms is the rate at which my accelerator is sending data
                            float ret = (float) (k);
                            int respVal = (int) (ret);
                            respRateValue = respVal;
                            Log.i(" ", "accelerometer reading done");
                        }
                    }
                } else {
                    Log.i("project4_ACCEL","Request Throttled");
                }
            }
        }
    }

    @Override
    public void onDestroy(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("log", "onDestroy TRUE");
                //De-registers the accelerometer listener
                accelerometer.unregisterListener(Accelerometer.this);

                //Send the Accelerometer XValues via Broadcasts
                Intent intent = new Intent("AccelerometerBcast");

                Bundle bundle = new Bundle();
                bundle.putInt("respRateValue", respRateValue);
                intent.putExtras(bundle);

                LocalBroadcastManager.getInstance(Accelerometer.this).sendBroadcast(intent);
            }
        });
        thread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
