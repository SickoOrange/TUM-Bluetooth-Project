package com.tum.orange.javabean;

import java.io.Serializable;

/**
 * storage the receive data in the bean
 * for serializable and anti serializable
 * Created by Orange on 28.09.2016.
 */
public class MyDataBean implements Serializable {

    private static final long serialVersionUID = -3075736527791479364L;
    private int index;
    private String CurrentMeasure;
    private String Max;
    private String Min;
    private String Mean;
    private String Std_Dev;

    public MyDataBean() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCurrentMeasure() {
        return CurrentMeasure;
    }

    public void setCurrentMeasure(String currentMeasure) {
        CurrentMeasure = currentMeasure;
    }

    public String getMax() {
        return Max;
    }

    public void setMax(String max) {
        Max = max;
    }

    public String getMin() {
        return Min;
    }

    public void setMin(String min) {
        Min = min;
    }

    public String getMean() {
        return Mean;
    }

    public void setMean(String mean) {
        Mean = mean;
    }

    public String getStd_Dev() {
        return Std_Dev;
    }

    public void setStd_Dev(String std_Dev) {
        Std_Dev = std_Dev;
    }
}
