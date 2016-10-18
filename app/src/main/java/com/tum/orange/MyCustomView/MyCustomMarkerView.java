package com.tum.orange.MyCustomView;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.tum.orange.tum_lmt.R;

/**
 * show the marker view in the chart
 * Created by Orange on 2016/10/9.
 */

public class MyCustomMarkerView extends MarkerView {
    private TextView tvContent;


    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyCustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tv_MarkerView1);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        float s = e.getVal();
        tvContent.setText(String.valueOf(s)); // set the entry-value as the display text

    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        System.out.println("xpos:" + xpos);
        return getWidth() / 10;

    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        System.out.println("ypos:" + ypos);
        return -getHeight();
    }
}
