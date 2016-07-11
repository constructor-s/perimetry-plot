package com.shirunjie.graphing;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // in this example, a LineChart is initialized from xml
        mChart = (LineChart) findViewById(R.id.chart);

        // no description text
        mChart.setDescription("This is the description");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        mChart.setBackgroundColor(Color.GRAY);

        //        setData(10, 10);

//        ArrayList<Entry> values = new ArrayList<>();
//        for (int i = 1; i < 5; i++) {
//            values.add(new Entry(-i, i));
//            values.add(new Entry(i, i));
//        }
//        values.add(new Entry(0, 0));
//
//        Collections.sort(values, new Comparator<Entry>() {
//            @Override
//            public int compare(Entry lhs, Entry rhs) {
//                return Math.round(lhs.getX() - rhs.getX());
//            }
//        });
//        Log.v(TAG, "onCreate: " + values);
//
//        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
//        set1.setValueTextSize(19f);
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        dataSets.add(set1);
//        LineData data = new LineData(dataSets);
//        mChart.setData(data);
//        mChart.invalidate();

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (int i = -27; i <= 27; i+=6) {
            for (int j = -27; j <= 27; j+=6) {
                final LineDataSet newLineDataSet = getNewLineDataSet(i, j);
                newLineDataSet.setValueTextSize(18f);
                dataSets.add(newLineDataSet);

            }
        }


        mChart.setData(new LineData(dataSets));

        final XAxis xAxis = mChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        xAxis.setAxisLineWidth(5);

        mChart.invalidate();

    }

    private LineDataSet getNewLineDataSet(float x, float y) {
        final ArrayList<Entry> entry = new ArrayList<>();
        entry.add(new Entry(x, y));
        return new LineDataSet(entry, String.format("(%.1f,%.1f)", x, y));
    }

    private void setData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) + 3;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        //        if (mChart.getData() != null &&
        //                mChart.getData().getDataSetCount() > 0) {
        //            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
        //            set1.setValues(values);
        //            mChart.getData().notifyDataChanged();
        //            mChart.notifyDataSetChanged();
        //        } else {
        // create a dataset and give it a type
        set1 = new LineDataSet(values, "DataSet 1");

        // set the line to be drawn like this "- - - - - -"
        //            set1.enableDashedLine(10f, 5f, 0f);
        //            set1.enableDashedHighlightLine(10f, 5f, 0f);
        //            set1.setColor(Color.BLACK);
        //            set1.setCircleColor(Color.BLACK);
        //            set1.setLineWidth(1f);
        //            set1.setCircleRadius(3f);
        //            set1.setDrawCircleHole(false);
        set1.setValueTextSize(19f);
        //            set1.setDrawFilled(true);

        //            if (Utils.getSDKInt() >= 18) {
        // fill drawable only supported on api level 18 and above
        //                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
        //                set1.setFillDrawable(drawable);
        //            } else {
        //                set1.setFillColor(Color.BLACK);
        //            }

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(dataSets);

        // set data
        mChart.setData(data);
        //        }
    }
}
