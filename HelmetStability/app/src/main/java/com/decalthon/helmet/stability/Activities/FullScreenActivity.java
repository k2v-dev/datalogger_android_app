package com.decalthon.helmet.stability.Activities;


import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.decalthon.helmet.stability.Fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.model.NineAxisModels.ChartType;
import com.decalthon.helmet.stability.model.NineAxisModels.NineAxis;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;
import com.decalthon.helmet.stability.model.NineAxisModels.TabMetaData;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**All fullscreen clicks lead to a sample graph - Under development*/

public class FullScreenActivity extends FragmentActivity {

    public LineChart lineChart;
    private static LineData lineData_g = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        final String graph_type = getIntent().getStringExtra("GRAPH_TYPE");
        final String device_id = getIntent().getStringExtra("DEVICE_ID");
        final String axis_type = getIntent().getStringExtra("AXIS_TYPE");

        final String frag_type = getIntent().getStringExtra("FRAG_TYPE");
        final String chart_type = getIntent().getStringExtra("CHART_TYPE");

//        final long session_id = getIntent().getShortExtra()
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
                        ChartType chartType = Enum.valueOf(ChartType.class, chart_type);
                        initLineChart(frag_type, chartType);
//                        initLineChart(graph_type,device_id, axis_type);
                    }
                });

    }

    private void initLineChart(String frag_type, ChartType chartType) {
        Map<Integer, SensorDataEntry> maps = null;
        TabMetaData tabMetaData = CustomGraphFragment.SAVE_TAB_DATA.get(frag_type);
        if (tabMetaData == null)  return;
        if(chartType == ChartType.ACC){
            maps = tabMetaData.accChartData;
        }else  if(chartType == ChartType.GYRO){
            maps = tabMetaData.gyrChartData;
        }else  if(chartType == ChartType.MAG){
            maps = tabMetaData.magChartData;
        }
       final Map<Integer, SensorDataEntry> maps_f = maps;
        if (maps.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        drawFullScreenGraph(maps, start_ts);
                        NineAxis.getInstance().drawGraph(frag_type, lineChart, maps_f, chartType);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (ExecutionException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

//    private void initLineChart(String graphType, String device_id, String axis_type ) {
//        String key =  graphType+device_id+axis_type;
//        if(CustomGraphFragment.SaveLineData.get(key) != null) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        drawFullScreenGraph(key);
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    } catch (ExecutionException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }).start();
//        }
////
////            }else if(graphType.equalsIgnoreCase(getString(R.string.gyroscope))){
////                try {
////                    drawFullScreenGraph(graphType);
////                    NineAxis.getInstance(getApplicationContext())
////                            .drawGraph(getString(R.string.gyroscope), lineChart);
////                } catch (ExecutionException e) {
////                    e.printStackTrace();
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }else if(graphType.equalsIgnoreCase(getString(R.string.magnetometer))){
////
////                    NineAxis.getInstance(getApplicationContext())
////                            .drawGraph(getString(R.string.magnetometer), lineChart);
////                } catch (ExecutionException e) {
////                    e.printStackTrace();
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
//
//
//
////        LineDataSet line1 = new LineDataSet(setEntries( graphType, device_id) );
////        line1.setColor(Color.RED);
////        line1.setCircleColor(Color.RED);
////        line1.setLineWidth(2f);
////        line1.setCircleRadius(2f);
////        line1.setDrawCircles(false);
////        line1.setDrawValues(false);
////
////        LineDataSet line2 = new LineDataSet(getRandomEntries(entrySize), graphType);
////        line2.setColor(Color.GREEN);
////        line2.setCircleColor(Color.GREEN);
////        line2.setLineWidth(2f);
////        line2.setCircleRadius(2f);
////        line2.setDrawCircles(false);
////        line2.setDrawValues(false);
////
////        LineDataSet line3 = new LineDataSet(getRandomEntries(entrySize), graphType);
////        line3.setColor(Color.BLUE);
////        line3.setCircleColor(Color.BLUE);
////        line3.setLineWidth(2f);
////        line3.setCircleRadius(2f);
////        line3.setDrawCircles(false);
////        line3.setDrawValues(false);
//
////        LineData lineData = new LineData(line1, line2, line3);
////        lineChart.setData(lineData);
////        lineChart.invalidate();
//
////        for(int i = 0; i < 7; i++) {
////            lineChart.zoom(i,0,0,0, YAxis.AxisDependency.LEFT);
////        }
//    }



//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private List<Entry> getRandomEntries(int entrySize) {
//        List<Entry> entries = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < entrySize ; i++) {
//            entries.add(new Entry(i, ThreadLocalRandom.current().nextFloat()*100));
//        }
//        return entries;
//    }

//    private List<Entry> setEntries(String graphType, String device_id) {
//        List<Entry> entries = new ArrayList<>();
//        if(graphType.equals(R.string.acceleration)){
////            NineAxis.GetAccNineAxisSensorDataAsyncTask.execute(session_id);
//        }
//
//        return entries;
//    }
//}

//    private void drawFullScreenGraph(String key) throws ExecutionException,
//            InterruptedException {
////        NineAxis.getInstance().drawGraph(graphType,
////                lineChart, device_id,getString(R.string.three_axis));
//        lineChart.setData(CustomGraphFragment.SaveLineData.get(key));
//        NineAxis.getInstance().prepareChart(lineChart);
//    }


    public static void setLineData(LineData lineData){
        lineData_g = lineData;
    }

}

