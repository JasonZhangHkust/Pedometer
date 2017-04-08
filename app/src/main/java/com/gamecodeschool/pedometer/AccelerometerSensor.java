package com.gamecodeschool.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by jasonchang on 2016/9/24.
 */
public class AccelerometerSensor implements SensorEventListener{
    public final static String TAG_VALUE_DX = "tagValueDx";
    public final static String TAG_VALUE_DY = "tagValueDy";
    public final static String TAG_VALUE_DZ = "tagValueDz";
    public final static String TAG_VALUE_STEP = "tagValueStep";


    public static int CURRENT_SETP = 0;
    public static float SENSITIVITY = 10; // SENSITIVITY灵敏度
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = { new float[3 * 2], new float[3 * 2] };
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    private boolean isStarted = false;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Handler mHandler;

    public  AccelerometerSensor(Context context, Handler handler){
        super();
        mHandler = handler;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }





    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
            return;
        }
        float dx = sensorEvent.values[0];
        float dy = sensorEvent.values[1];
        float dz = sensorEvent.values[2];

        synchronized (this) {
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
                    final float v = mYOffset + sensorEvent.values[i] * mScale[1];
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3;

                float direction = (v > mLastValues[k] ? 1
                        : (v < mLastValues[k] ? -1 : 0));
                if (direction == -mLastDirections[k]) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or
                    // maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k]
                            - mLastExtremes[1 - extType][k]);

                    if (diff > SENSITIVITY) {
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
                                && isNotContra) {
                            end = System.currentTimeMillis();
                            if (end - start > 500) {// 此时判断为走了一步

                                CURRENT_SETP++;
                                mLastMatch = extType;
                                start = end;
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;

        }

        if(mHandler != null){
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putFloat(TAG_VALUE_DX,dx);
            bundle.putFloat(TAG_VALUE_DY,dy);
            bundle.putFloat(TAG_VALUE_DZ,dz);
            bundle.putFloat(TAG_VALUE_STEP,CURRENT_SETP);

            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startListening(){
        if (isStarted)
            return;
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_UI);
        isStarted = true;
    }
    public void stopListening(){
        if(!isStarted)
            return;
        mSensorManager.unregisterListener(this);
        isStarted = false;
    }



}
