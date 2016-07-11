package com.shirunjie.graphing;

import android.app.Activity;
import android.os.Bundle;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.RectRegion;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

public class Main3Activity extends Activity {

    private XYPlot plot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        XYSeries series1 = generateScatter("series1", 80, new RectRegion(-27, 27, -27, 27));
//        XYSeries series2 = generateScatter("series2", 80, new RectRegion(30, 70, 30, 70));

//        plot.setDomainBoundaries(0, 80, BoundaryMode.FIXED);
//        plot.setRangeBoundaries(0, 80, BoundaryMode.FIXED);

        plot.setDomainBoundaries(-27, 27, BoundaryMode.FIXED);
        plot.setRangeBoundaries(-27, 27, BoundaryMode.FIXED);

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
//        LineAndPointFormatter series1Format = new LineAndPointFormatter();
//        series1Format.configure(getApplicationContext(),
//                R.xml.point_formatter);
//
//        LineAndPointFormatter series2Format = new LineAndPointFormatter();
//        series2Format.configure(getApplicationContext(),
//                R.xml.point_formatter_2);

        // add each series to the xyplot:
//        plot.addSeries(series1, series1Format);
//        plot.addSeries(series2, series2Format);
        final LineAndPointFormatter formatter = new LineAndPointFormatter();
        formatter.setLinePaint(null);
        formatter.setFillPaint(null);
        formatter.getVertexPaint().setColor(0xFFFFFFFF);
        plot.addSeries(series1, formatter);
//        plot.addSeries(series2, new LineAndPointFormatter());

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 6);
        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 6);
    }

    /**
     * Generate a XYSeries of random points within a specified region
     * @param title
     * @param numPoints
     * @param region
     * @return
     */
    private XYSeries generateScatter(String title, int numPoints, RectRegion region) {
        SimpleXYSeries series = new SimpleXYSeries(title);
        for(int i = 0; i < numPoints; i++) {
            series.addLast(
                    region.getMinX().doubleValue() + (Math.random() * region.getWidth().doubleValue()),
                    region.getMinY().doubleValue() + (Math.random() * region.getHeight().doubleValue())
            );
        }
        return series;
    }

}
