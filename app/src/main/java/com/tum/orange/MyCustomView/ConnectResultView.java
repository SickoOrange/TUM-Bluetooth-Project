package com.tum.orange.MyCustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * show the bluetooth connect result state view
 * Created by Orange on 25.09.2016.
 */
public class ConnectResultView extends View {
    private int mWidth;
    private int mHeight;
    private Paint paint;
    private Path path;

    //设置默认的初始圆弧宽度
    private int ArcWidth = 8;

    private RectF rectF;

    //标记是否可以开始动画
    private boolean canStartDraw = false;
    //成功或者失败的标记
    private boolean isSucceed;

    private int mLineShrinkPercent;
    //绘制圆点下面的横线Path标记
    private boolean isPathToLine;


    private int mPathPercent;
    private int mRisePercent;
    private int mReadyFinishPercent;


    private boolean canShowEnd;



    public ConnectResultView(Context context) {
        super(context);
    }

    public ConnectResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConnectResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        //初始化画笔以及Path
        paint = new Paint();
        paint.setColor(Color.parseColor("#2EA4F2"));
        //你所画的是图形(paint.setStyle(Paint.Style.FILL))还是空心(paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ArcWidth);
        paint.setAntiAlias(true);
        path = new Path();
    }

    public void setSucceed(boolean isSucceed) {
        this.isSucceed = isSucceed;
    }


    public void setStart(boolean start) {
        canStartDraw = start;
        canShowEnd = true;
        postInvalidateDelayed(10);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        //百分比弧的矩形
        rectF = new RectF(5, 5, mWidth - 5, mHeight - 5);
        System.out.println(mWidth + " " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //每次重新设置画笔的初始颜色
        if (canStartDraw) {
            //开始变形
            //设置画笔颜色
            paint.setColor(Color.parseColor("#2EA4F2"));
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
            paint.setColor(Color.WHITE);
            //绘制path折线
            if (!isPathToLine) {
                path.moveTo(mWidth / 4, mHeight / 2);
                path.lineTo(mWidth / 2, mHeight * 0.75f);
                path.lineTo(mWidth * 0.75f, mHeight / 2);
                canvas.drawPath(path, paint);
                //清空path 防止重影
                path.reset();
            }
            //mLIneShrinkPercent是竖线的收缩百分比 当收缩到中点时候
            //竖线变成一个小圆点
            if (mLineShrinkPercent < 95) {
                float ShrinkSpeed = mHeight / 4 * (mLineShrinkPercent / 100f);
                canvas.drawLine(mWidth / 2, mHeight / 4 + ShrinkSpeed, mWidth / 2, mHeight * 0.75f - ShrinkSpeed, paint);
                mLineShrinkPercent += 15;
            } else {
                //竖线变成小圆点时候 开始折线变形
                // 圆点下面的PATH折线 变形为横线
                isPathToLine = true;
                if (mPathPercent < 100) {

                    float PathSpeed = (mHeight / 4) * (mPathPercent / 100f);
                    path.moveTo(mWidth / 4, mHeight / 2);
                    path.lineTo(mWidth / 2, mHeight * 0.75f - PathSpeed);
                    path.lineTo(mWidth * 0.75f, mHeight / 2);
                    canvas.drawPath(path, paint);
                    //清空path 防止重影
                    path.reset();
                    mPathPercent += 15;
                    //竖线变成一个小圆点 注意小圆点消失的时机
                    //是在path折线变成横线的时候 冰倩小圆点上弹的那一刻才消失
                    canvas.drawCircle(mWidth / 2, mHeight / 2, 2.5f, paint);
                } else {

                    //绘制动态上弹得小圆点
                    if (mRisePercent < 100) {
                        float RiseSpeed = mHeight / 2 * (mRisePercent / 100f);
                        canvas.drawLine(mWidth / 4, mHeight * 0.5f, mWidth * 0.75f, mHeight * 0.5f, paint);
                        canvas.drawCircle(mWidth / 2, mHeight / 2 - RiseSpeed, 2.5f, paint);
                        mRisePercent += 15;
                    } else {
                        //小圆点上升到了最高点的时候
                        // canvas.drawCircle(mWidth / 2, 5, 2f, paint);

                        if (mReadyFinishPercent < 100) {
                            //动态的画对勾或者叉叉 跟动态的画百分比圆圈
                            float FinishPercent = mHeight / 4 * (mReadyFinishPercent / 100f);

                            if (isSucceed) {
                                //对勾
                                path.moveTo(mWidth / 4, mHeight / 2);
                                path.lineTo(mWidth / 2, mHeight / 2 + FinishPercent);
                                path.lineTo(mWidth * 0.75f, mHeight / 2 + (mReadyFinishPercent / 100f) * (0.3f * mHeight - mHeight / 2));
                                canvas.drawPath(path, paint);
                                //清空path 防止重影
                                path.reset();
                            } else {
                                //画叉叉
                                canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 + ((mWidth * 0.75f - mWidth / 2) * (mReadyFinishPercent / 100f)),
                                        mHeight / 2 - mHeight / 4 * (mReadyFinishPercent / 100f), paint);
                                canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 + ((mWidth * 0.75f - mWidth / 2) * (mReadyFinishPercent / 100f)),
                                        mHeight / 2 + mHeight / 4 * (mReadyFinishPercent / 100f), paint);
                                canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 - ((mWidth * 0.75f - mWidth / 2) * (mReadyFinishPercent / 100f)),
                                        mHeight / 2 - mHeight / 4 * (mReadyFinishPercent / 100f), paint);
                                canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 2 - ((mWidth * 0.75f - mWidth / 2) * (mReadyFinishPercent / 100f)),
                                        mHeight / 2 + mHeight / 4 * (mReadyFinishPercent / 100f), paint);
                            }
                            canvas.drawArc(rectF, 270, -mReadyFinishPercent / 100.0f * 360, false, paint);
                            mReadyFinishPercent += 15;
                        } else {
                            if (isSucceed) {
                                //绘制最终的对勾Path
                                drawSucceed(canvas);
                            } else {
                                //绘制最终的叉叉
                                drawFailed(canvas);
                            }
                            //绘制最终的圆圈

                            canvas.drawArc(rectF, 270, -mReadyFinishPercent / 100.0f * 360, false, paint);
                            // canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
                            //结束动画
                            canStartDraw = false;
                            //重置控制变量
                            //paint.reset();
                            //paint.setStyle(Paint.Style.STROKE);
                            path.reset();
                            mLineShrinkPercent = 0;
                            isPathToLine = false;
                            mPathPercent = 0;
                            mRisePercent = 0;
                            mReadyFinishPercent = 0;

                        }

                    }

                }

            }
        } else {


            if (canShowEnd) {
                //动画结束时候 最终显示的View
                if (isSucceed) {
                    //绘制最终的对勾Path
                    paint.setColor(Color.WHITE);
                    drawSucceed(canvas);
                } else {
                    //绘制最终的叉叉
                    paint.setColor(Color.WHITE);
                    drawFailed(canvas);
                }
                canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
            }else{
                //第一次显示的标志
                //drawInit(canvas);
            }

        }
        postInvalidateDelayed(10);
        super.onDraw(canvas);
    }

    private void drawFailed(Canvas canvas) {
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth * 0.75f, mHeight / 4, paint);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth * 0.75f, mHeight * 0.75f, paint);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 4, mHeight / 4, paint);
        canvas.drawLine(mWidth / 2, mHeight / 2, mWidth / 4, mHeight * 0.75f, paint);
    }

    private void drawSucceed(Canvas canvas) {
        path.moveTo(mWidth / 4, mHeight * 0.5f);
        path.lineTo(mWidth / 2, mHeight * 0.75f);
        path.lineTo(mWidth * 0.75f, mHeight * 0.3f);
        canvas.drawPath(path, paint);
        //清空path 防止重影
        path.reset();
    }

    private void drawInit(Canvas canvas){
        paint.setColor(Color.parseColor("#2EA4F2"));
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
    }

}
