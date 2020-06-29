package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class graph extends AppCompatActivity {

    double [] grayValues;
    GraphView graph;
    String TAG = "graphActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        grayValues = getIntent().getDoubleArrayExtra("grayValues");
        double[] movingAverage = smooth(grayValues , 70);
        graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getDataPoint(movingAverage));


        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.0);
        graph.getViewport().setMaxX(grayValues.length);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0.0);
        graph.getViewport().setMaxY(200.0);
        //graph.getViewport().setScrollable(true);
        //graph.getViewport().setScrollableY(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.setFocusable(true);
        graph.getViewport().setBackgroundColor(50);
        Log.i(TAG,"background color   "+graph.getViewport().getBackgroundColor());
        //graph.setBackgroundColor(2);
        //graph.setScaleX(2);
        //graph.setScaleY(2);
        Log.i(TAG,"is scalable :" + graph.getViewport().isScalable());
        graph.setTitle("im graph");


       // Log.i(TAG , "im grayValues!" + grayValues[0]+"     "+grayValues[100] + "    "+grayValues[200] + "  " +grayValues[600]);

    }

    private double[] smooth(double[] grayValues , int period) {
        double[] movingAverage = new double[grayValues.length];
        //Log.i(TAG,"grayvalues.lenght" + grayValues.length);
        for(int i = 0; i < grayValues.length - period ; i++)
        {
            movingAverage[i] = 0;
         /*   if (i == grayValues.length - period)
            {

            }*/
            for(int j = i; j < i + period ; j++)
            {
                movingAverage[i] += grayValues[j];
            }
            movingAverage[i] = movingAverage[i] / period ;
           // Log.i(TAG,"moving average:index" + i + "    "+movingAverage[i] + "grayvalues" + grayValues[i]);
        }
        return movingAverage;
    }

    private DataPoint[] getDataPoint(double[] movingAverage)
    {
        DataPoint[] dp = new DataPoint[movingAverage.length];
            for(int i = 0; i < movingAverage.length;i++)
            {
               dp[i] = new DataPoint(i,movingAverage[i]);
            }

        return dp;
    }

}
