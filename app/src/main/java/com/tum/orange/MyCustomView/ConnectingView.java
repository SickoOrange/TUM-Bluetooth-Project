package com.tum.orange.MyCustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Orange on 25.09.2016.
 */
public class ConnectingView extends View {
    private Paint mPaintOne;
    private Paint mPaintTwo;
    private Path path;
    private int mWidth;
    private int mHeight;

    private int mCircleRadius = 30;

    private float mDegree;
    private float mOffset;

    private int mCirclePositiveShrinkPercent;
    private int mCircleNegativePercent;

    public ConnectingView(Context context) {
        super(context);
    }

    public ConnectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public ConnectingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init() {
        mPaintOne = new Paint();
        mPaintOne.setColor(Color.WHITE);
        mPaintOne.setAntiAlias(true);


        mPaintTwo = new Paint();
        mPaintTwo.setColor(Color.WHITE);
        mPaintTwo.setAntiAlias(true);
        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mWidth = widthSize;
        } else {
            mWidth = 200;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        } else {
            mHeight = 200;
        }
        System.out.println("measure" + mWidth + " " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDegree += 2f;
        if (mDegree == 360f) {
            mDegree = 0;
        }
        canvas.rotate(mDegree, mWidth / 2, mHeight / 2);
        if (mCirclePositiveShrinkPercent < 100) {
            mOffset = (mHeight / 2 - mCircleRadius) * (mCirclePositiveShrinkPercent / 100f);
            canvas.drawCircle(mWidth / 2, mCircleRadius + mOffset, mCircleRadius, mPaintOne);
            canvas.drawCircle(mWidth / 2, mHeight - mCircleRadius - mOffset, mCircleRadius, mPaintTwo);
            mCirclePositiveShrinkPercent += 1f;
        } else {
            if (mCircleNegativePercent < 100) {
                mOffset = (mHeight / 2 - mCircleRadius) * (mCircleNegativePercent / 100f);
                canvas.drawCircle(mWidth / 2, mHeight / 2 - mOffset, mCircleRadius, mPaintOne);
                canvas.drawCircle(mWidth / 2, mHeight / 2 + mOffset, mCircleRadius, mPaintTwo);
                mCircleNegativePercent += 1f;
            } else {
                canvas.drawCircle(mWidth / 2, mCircleRadius, mCircleRadius, mPaintOne);
                canvas.drawCircle(mWidth / 2, mHeight - mCircleRadius, mCircleRadius, mPaintTwo);
                mCirclePositiveShrinkPercent = 0;
                mCircleNegativePercent = 0;
            }
        }

        invalidate();
        super.onDraw(canvas);
    }
}
