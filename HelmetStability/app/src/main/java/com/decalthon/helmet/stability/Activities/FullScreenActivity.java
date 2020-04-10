package com.decalthon.helmet.stability.Activities;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.decalthon.helmet.stability.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.decalthon.helmet.stability.Utilities.Constants.TEST_SAMPLE_SIZE;

/**All fullscreen clicks lead to a sample graph - Under development*/

public class FullScreenActivity extends FragmentActivity {

    public LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        final String name = getIntent().getStringExtra("PlotName");


        /**Full Screen rendering approach*/
        lineChart = findViewById(R.id.activity_multiple_line_chart_test_line_chart);
        lineChart.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        /**This callback is invoked when the there
                         * is a change in any global layout state
                         * in the project source tree*/

                        /**LineChart-associated listeners are removed, to override
                         * default behavior of the lineChart's view
                         * */

                        lineChart.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        /**An offset is used to calculate linear graph translation
                         * along X and Y directions
                         * */
                        int offset = (lineChart.getHeight() - lineChart.getWidth()) / 2;

                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) lineChart.getLayoutParams();
                        layoutParams.width = lineChart.getHeight();
                        layoutParams.height = lineChart.getWidth();

                        /**The linechart layout parameters are converted to a frame.
                         * Specifically, the width and height of the lineChart are reset
                         * to dimensions that can create a FrameLayout*/
                        lineChart.setLayoutParams(layoutParams);

                        lineChart.setTranslationX(-offset);

                        lineChart.setTranslationY(offset);

                        initLineChart(name);
                    }
                });

    }

    private void initLineChart(String graphName) {

        int entrySize = TEST_SAMPLE_SIZE;
        LineDataSet line1 = new LineDataSet(setEntries(entrySize), graphName );
        line1.setColor(Color.RED);
        line1.setCircleColor(Color.RED);
        line1.setLineWidth(2f);
        line1.setCircleRadius(2f);
        line1.setDrawCircles(false);
        line1.setDrawValues(false);

        LineDataSet line2 = new LineDataSet(getRandomEntries(entrySize), graphName);
        line2.setColor(Color.GREEN);
        line2.setCircleColor(Color.GREEN);
        line2.setLineWidth(2f);
        line2.setCircleRadius(2f);
        line2.setDrawCircles(false);
        line2.setDrawValues(false);

        LineDataSet line3 = new LineDataSet(getRandomEntries(entrySize), graphName);
        line3.setColor(Color.BLUE);
        line3.setCircleColor(Color.BLUE);
        line3.setLineWidth(2f);
        line3.setCircleRadius(2f);
        line3.setDrawCircles(false);
        line3.setDrawValues(false);

        LineData lineData = new LineData(line1, line2, line3);
        lineChart.setData(lineData);
        lineChart.invalidate();

        for(int i = 0; i < 7; i++) {
            lineChart.zoom(i,0,0,0, YAxis.AxisDependency.LEFT);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private List<Entry> getRandomEntries(int entrySize) {
        List<Entry> entries = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < entrySize ; i++) {
            entries.add(new Entry(i, ThreadLocalRandom.current().nextFloat()*100));
        }
        return entries;
    }

    private List<Entry> setEntries(int entrySize) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < entrySize ; i++) {
            entries.add(new Entry(i, ThreadLocalRandom.current().nextFloat()*100));
        }
        return entries;
    }
}

