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

    private int ArcWidth = 8;

    private RectF rectF;

    private boolean canStartDraw = false;
    private boolean isSucceed;

    private int mLineShrinkPercent;
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
        paint = new Paint();
        paint.setColor(Color.parseColor("#2EA4F2"));
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
        rectF = new RectF(5, 5, mWidth - 5, mHeight - 5);
        System.out.println(mWidth + " " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canStartDraw) {
            paint.setColor(Color.parseColor("#2EA4F2"));
            canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
            paint.setColor(Color.WHITE);
            if (!isPathToLine) {
                path.moveTo(mWidth / 4, mHeight / 2);
                path.lineTo(mWidth / 2, mHeight * 0.75f);
                path.lineTo(mWidth * 0.75f, mHeight / 2);
                canvas.drawPath(path, paint);
                path.reset();
            }
            if (mLineShrinkPercent < 95) {
                float ShrinkSpeed = mHeight / 4 * (mLineShrinkPercent / 100f);
                canvas.drawLine(mWidth / 2, mHeight / 4 + ShrinkSpeed, mWidth / 2, mHeight * 0.75f - ShrinkSpeed, paint);
                mLineShrinkPercent += 15;
            } else {
                isPathToLine = true;
                if (mPathPercent < 100) {

                    float PathSpeed = (mHeight / 4) * (mPathPercent / 100f);
                    path.moveTo(mWidth / 4, mHeight / 2);
                    path.lineTo(mWidth / 2, mHeight * 0.75f - PathSpeed);
                    path.lineTo(mWidth * 0.75f, mHeight / 2);
                    canvas.drawPath(path, paint);
                    path.reset();
                    mPathPercent += 15;
                    canvas.drawCircle(mWidth / 2, mHeight / 2, 2.5f, paint);
                } else {

                    if (mRisePercent < 100) {
                        float RiseSpeed = mHeight / 2 * (mRisePercent / 100f);
                        canvas.drawLine(mWidth / 4, mHeight * 0.5f, mWidth * 0.75f, mHeight * 0.5f, paint);
                        canvas.drawCircle(mWidth / 2, mHeight / 2 - RiseSpeed, 2.5f, paint);
                        mRisePercent += 15;
                    } else {
                        if (mReadyFinishPercent < 100) {
                            float FinishPercent = mHeight / 4 * (mReadyFinishPercent / 100f);

                            if (isSucceed) {
                                path.moveTo(mWidth / 4, mHeight / 2);
                                path.lineTo(mWidth / 2, mHeight / 2 + FinishPercent);
                                path.lineTo(mWidth * 0.75f, mHeight / 2 + (mReadyFinishPercent / 100f) * (0.3f * mHeight - mHeight / 2));
                                canvas.drawPath(path, paint);
                                path.reset();
                            } else {
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
                                drawSucceed(canvas);
                            } else {
                                drawFailed(canvas);
                            }

                            canvas.drawArc(rectF, 270, -mReadyFinishPercent / 100.0f * 360, false, paint);
                            canStartDraw = false;
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
                if (isSucceed) {
                    paint.setColor(Color.WHITE);
                    drawSucceed(canvas);
                } else {
                    paint.setColor(Color.WHITE);
                    drawFailed(canvas);
                }
                canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
            }else{

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
        path.reset();
    }

    private void drawInit(Canvas canvas){
        paint.setColor(Color.parseColor("#2EA4F2"));
        canvas.drawCircle(mWidth / 2, mHeight / 2, mWidth / 2 - 5, paint);
    }

}
