package com.gamecodeschool.pedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;


public class DisplayView extends SurfaceView{
    public final static int TYPE_BALL = 0;
    public final static int TYPE_SQUARE = 1;
    public final static int TYPE_DIAMOND = 2;
    public final static int TYPE_ARC = 3;

    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mRadius = 0.0f;

    private float mPtrCenterX = 100.0f;
    private float mPtrCenterY = 100.0f;
    private float mPtrRadius = 10.0f;

    private int mPtrType = TYPE_BALL;
    private int mPtrColor = Color.RED;

    public DisplayView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setWillNotDraw(false);
    }

    public  void setPtr(float F){
        //mPtrCenterX = posX * mRadius * 0.9f + mCenterX;
        mPtrCenterY = -Math.abs(F-9.8f) * mRadius * 0.4f + mCenterY+175f;
        if(mPtrCenterY < mCenterY - 250f ){
            mPtrCenterY= mCenterY - 250f;
        }
        invalidate();
    }

    public void onDraw(Canvas canvas){
        if (canvas == null)
            return;
        canvas.drawColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(mCenterX-50f,mCenterY-100f,mCenterX+50f,mCenterY,paint);
        //Paint paint2 = new Paint();
        paint.setColor(Color.GREEN);
        canvas.drawRect(mCenterX-50f,mCenterY,mCenterX+50f,mCenterY+100f,paint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(mCenterX-50f,mCenterY+100f,mCenterX+50f,mCenterY+200f,paint);
        paint.setColor(Color.RED);
        canvas.drawRect(mCenterX-50f,mCenterY-200f,mCenterX+50f,mCenterY-100f,paint);
        paint.setColor(Color.WHITE);
        canvas.drawRect(mCenterX-50f,mCenterY-300f,mCenterX+50f,mCenterY-200f,paint);
        paint.setColor(Color.GRAY);
        canvas.drawCircle(mCenterX,mPtrCenterY,mPtrRadius,paint);
       /* canvas.drawRect(mCenterX - mPtrRadius, mPtrCenterY - mPtrRadius,
                mCenterX + mPtrRadius, mPtrCenterY + mPtrRadius, paint);*/

    }
    public void onSizeChanged(int width,int height,int oldWidth,int oldHeight){
        mCenterX = width / 2;
        mCenterY = height / 2;
        mRadius = ((width < height)?width:height) * 3.0f / 8.8f;
        mPtrCenterX = mCenterX;
        mPtrCenterY = mCenterY;
        mPtrRadius = mRadius / 10.0f;
        invalidate();
    }

    public void setPtrColor(int color){
        mPtrColor = color;
        invalidate();
    }
    public void setPtrType(int type){
        mPtrType = type;
        invalidate();
    }

}
