package com.shirunjie.graphing;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by shirunjie on 2016-07-08.
 */

public class PerimetryDataView extends View {

    private PerimetryData data = null;

    public PerimetryDataView(Context context) {
        super(context);
    }

    public PerimetryDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PerimetryDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PerimetryDataView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (data != null) {
            new TextResultDrawer(canvas, data, this).draw();
        }
    }

    public void setData(PerimetryData data) {
        this.data = data;
    }

}
