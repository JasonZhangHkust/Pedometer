package com.gamecodeschool.pedometer;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DisplayView mDisplayView;
    private float mX = -100.0f;
    private float mY = -100.0f;
    private float mZ = -100.0f;
    private final static float THRESHOLD = .5f;
    private float mF = 0f;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private AccelerometerSensor mAccelerometerSensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,getClass().getName());
        mWakeLock.acquire();
        mDisplayView = (DisplayView) findViewById(R.id.mDisplayView);
        mAccelerometerSensor = new AccelerometerSensor(this, new Handler(){
            @Override
            public void handleMessage(Message msg){
                float tmpX = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DX);
                float tmpY = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DY);
                float tmpZ = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_DZ);
                float tmpS = msg.getData().getFloat(AccelerometerSensor.TAG_VALUE_STEP);
                if (tmpX - mX > THRESHOLD || tmpX - mX < -THRESHOLD||
                        tmpY - mY>THRESHOLD || tmpY- mY< -THRESHOLD||
                        tmpZ-mZ>THRESHOLD || tmpZ-mZ < -THRESHOLD){
                    mX = tmpX; mY=tmpY; mZ = tmpZ;
                    TextView tvValueX = (TextView) findViewById(R.id.tvValueX);
                    TextView tvValueY = (TextView) findViewById(R.id.tvValueY);
                    TextView tvValueZ = (TextView) findViewById(R.id.tvValueZ);
                    TextView tvForce =(TextView) findViewById(R.id.tvForce);
                    TextView tvCount = (TextView) findViewById(R.id.tvCount);

                    tvValueX.setText("" + mX);
                    tvValueY.setText("" + mY);
                    tvValueZ.setText("" + mZ);
                    tvForce.setText("" + mF);
                    tvCount.setText("" + tmpS);
                    mF = (float) Math.sqrt(mX*mX+mY*mY+mZ*mZ);
                    mDisplayView.setPtr(mF);
                }
            }
        });

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAccelerometerSensor.CURRENT_SETP = 0;
            }
        });

    }
    @Override
    public  synchronized void onResume(){
        super.onResume();
        if(mAccelerometerSensor != null){
            mAccelerometerSensor.startListening();
        }
        mWakeLock.acquire();
    }
    @Override
    public synchronized void onPause(){
        if(mAccelerometerSensor !=null){
            mAccelerometerSensor.stopListening();
        }
        mWakeLock.release();
        super.onPause();
    }

}
