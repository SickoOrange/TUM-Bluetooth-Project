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

    //定义旋转的小圆球的默认半径
    private int mCircleRadius = 30;

    private float mDegree;
    private float mOffset;
    //偏移百分比
    private int mCirclePositiveShrinkPercent;
    private int mCircleNegativePercent;

    public ConnectingView(Context context) {
        super(context);
    }

    public ConnectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化画板
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
        //旋转画布 造成旋转的效果
        canvas.rotate(mDegree, mWidth / 2, mHeight / 2);
        //两个小圆点互相靠近的距离 不断变化
        if (mCirclePositiveShrinkPercent < 100) {
            mOffset = (mHeight / 2 - mCircleRadius) * (mCirclePositiveShrinkPercent / 100f);
            //绘制下降小红点
            canvas.drawCircle(mWidth / 2, mCircleRadius + mOffset, mCircleRadius, mPaintOne);
            //绘制上升小蓝点
            canvas.drawCircle(mWidth / 2, mHeight - mCircleRadius - mOffset, mCircleRadius, mPaintTwo);
            //改变偏移百分比
            mCirclePositiveShrinkPercent += 1f;
        } else {
            if (mCircleNegativePercent < 100) {
                mOffset = (mHeight / 2 - mCircleRadius) * (mCircleNegativePercent / 100f);
                //绘制上升小红点
                canvas.drawCircle(mWidth / 2, mHeight / 2 - mOffset, mCircleRadius, mPaintOne);
                //绘制下降小圆点
                canvas.drawCircle(mWidth / 2, mHeight / 2 + mOffset, mCircleRadius, mPaintTwo);
                //改变偏移百分比
                mCircleNegativePercent += 1f;
            } else {
                //实现在边缘处平滑的滑动
                canvas.drawCircle(mWidth / 2, mCircleRadius, mCircleRadius, mPaintOne);
                canvas.drawCircle(mWidth / 2, mHeight - mCircleRadius, mCircleRadius, mPaintTwo);
                //重置偏移变量 循环
                mCirclePositiveShrinkPercent = 0;
                mCircleNegativePercent = 0;
            }
        }

        //刷新 重绘
        invalidate();
        super.onDraw(canvas);
    }
}
