package com.decalthon.helmet.stability.model.nineaxismodels;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class NineAxis {

    String type;

    //Used to plot a point of each color in every millisecond time gap
    private static float milliSecondUpdater = Constants.UPDATER_STATIC;

    private static Long mSessionId = (long)4;
    private static MagnetometerData magnetometerData;
    private static NineAxis nineAxisInstance = null;
    private Long[] timestampData;
    private static long timeOfStart ;



    //Default constructor
/*    private NineAxis(Context context, Long session_id) {
        mContext = context;
        mSessionId = session_id;
    }*/

    public static NineAxis getInstance() {
        if(nineAxisInstance == null){
            nineAxisInstance = new NineAxis();
        }
        return nineAxisInstance;
    }

    //Color codes are associated with axes for each dataset
    public static void registerColorCodes(LineDataSet... axes) {
        axes[0].setColor(Color.BLUE);
        axes[1].setColor(Color.GREEN);
        axes[2].setColor(Color.RED);
    }

    //Plot circles are removed for all plots to save screens space
    public static void formatPlotCircles(LineDataSet anyDataSet) {
        anyDataSet.setDrawCircles(false);
        anyDataSet.setDrawFilled(false);
        anyDataSet.setDrawValues(false);
    }

    public void drawGraph(LineChart plottable, QueryParameters queryParameters) throws ExecutionException, InterruptedException {
        LineDataSet lineDataSetX = new LineDataSet(null, queryParameters.graphType.charAt(0)+"x");
        LineDataSet lineDataSetY = new LineDataSet(null, queryParameters.graphType.charAt(0)+"y");
        LineDataSet lineDataSetZ = new LineDataSet(null, queryParameters.graphType.charAt(0)+"z");
        LineData lineData = new LineData();

        //Todo: query the db
        Map<Integer, SensorDataEntry> maps = null;
        if(queryParameters.chartType == ChartType.ACC){
            maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).accChartData;
        }else if(queryParameters.chartType == ChartType.GYR){
            maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).gyrChartData;
        }else if(queryParameters.chartType == ChartType.MAG){
            maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).magChartData;
        }
        List<SensorDataEntry> sensorDataReadings = query_db(queryParameters);
        long timeOfStart = queryParameters.session_start_ts;
        if(sensorDataReadings != null){
            for(SensorDataEntry sensorDataEntry : sensorDataReadings){
                int timeDiff = (int)(sensorDataEntry.getTime() - timeOfStart);
                float deltaT = timeDiff/1000.0f;
                lineDataSetX.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getXval()));
                lineDataSetY.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getYval()));
                lineDataSetZ.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getZval()));
                maps.put(timeDiff, sensorDataEntry);
            }
        }
//        long t2 = System.currentTimeMillis();
//        System.out.println("Loading data "+queryParameters.graphType+", time="+(t2-t1));


        if(queryParameters.chartType == ChartType.GPS_SPEED) {
            formatPlotCircles(lineDataSetX);
            lineDataSetX.setColor(Color.BLUE);
            lineData.addDataSet(lineDataSetX);
        }else{
            formatPlotCircles(lineDataSetX);
            formatPlotCircles(lineDataSetY);
            formatPlotCircles(lineDataSetZ);

            registerColorCodes(lineDataSetX,lineDataSetY, lineDataSetZ);

            lineData.addDataSet(lineDataSetX);
            lineData.addDataSet(lineDataSetY);
            lineData.addDataSet(lineDataSetZ);
        }

        //CustomGraphFragment.SaveLineData.put(graphType+device_id+axes, lineData);

        plottable.setData(lineData);
        if(sensorDataReadings!=null && sensorDataReadings.size() > 200){
//            float ts_start = (sensorDataReadings.get(100).getTime()-timeOfStart)/1000.0f;
//            float ts_end = (sensorDataReadings.get(sensorDataReadings.size()-100).getTime()-timeOfStart)/1000.0f;
            //plottable.setVisibleXRange(ts_start, ts_end);
            CustomGraphFragment.START_TS = (sensorDataReadings.get(0).getTime()-timeOfStart)/1000.0f;
            CustomGraphFragment.END_TS = (sensorDataReadings.get(sensorDataReadings.size()-1).getTime()-timeOfStart)/1000.0f;
        }

        prepareChart(plottable);
    }


    /**
     * this function is used by Full Screen Activity
     * @param frag_type
     * @param plottable
     * @param maps
     * @param chartType
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void drawGraph(String frag_type, LineChart plottable, Map<Integer, SensorDataEntry> maps, ChartType chartType) throws ExecutionException, InterruptedException {
        String graphType = chartType.toString().toLowerCase();
        LineDataSet lineDataSetX = new LineDataSet(null, graphType.charAt(0)+"x");
        LineDataSet lineDataSetY = new LineDataSet(null, graphType.charAt(0)+"y");
        LineDataSet lineDataSetZ = new LineDataSet(null, graphType.charAt(0)+"z");

        if(chartType == ChartType.GPS_SPEED){
            lineDataSetX.setLabel("speed(km/h)");
        }
        LineData lineData = new LineData();

        //Todo: query the db
        if(maps != null){
            for(Map.Entry<Integer, SensorDataEntry> entry: maps.entrySet()){
                SensorDataEntry sensorDataEntry = entry.getValue();
                //int timeDiff = (int)(sensorDataEntry.getTime() - timeOfStart);
                float deltaT = entry.getKey()/1000.0f;
                lineDataSetX.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getXval()));
                lineDataSetY.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getYval()));
                lineDataSetZ.addEntry(new
                        Entry(deltaT,
                        sensorDataEntry.getZval()));
            }
        }

        if(chartType == ChartType.GPS_SPEED) {
            formatPlotCircles(lineDataSetX);
            lineDataSetX.setColor(Color.BLUE);
            lineData.addDataSet(lineDataSetX);
        }else{
            formatPlotCircles(lineDataSetX);
            formatPlotCircles(lineDataSetY);
            formatPlotCircles(lineDataSetZ);

            registerColorCodes(lineDataSetX,lineDataSetY, lineDataSetZ);

            lineData.addDataSet(lineDataSetX);
            lineData.addDataSet(lineDataSetY);
            lineData.addDataSet(lineDataSetZ);
        }

        //CustomGraphFragment.SaveLineData.put(graphType+device_id+axes, lineData);

        plottable.setData(lineData);
//        plottable.setVisibleXRange(ts_start, ts_end);
        prepareChart(plottable);
    }


    private List<SensorDataEntry> query_db(QueryParameters queryParameters) throws ExecutionException, InterruptedException {

        List<SensorDataEntry> dataEntryList = new ArrayList<>();
        QueryDbAPI queryDbAPI = new QueryDbAPI();
            switch (queryParameters.queryType){
                case INITIAL_LOAD:
                    dataEntryList = queryDbAPI.getReadings(queryParameters);
                    break;
                case RELOAD:
                    dataEntryList = queryDbAPI.reloadData(queryParameters);
            }
        return dataEntryList;
    }

//    public void drawGraph(String graphType, LineChart plottable,
//                          String device_id, String axes) throws ExecutionException,
//            InterruptedException {
//
//        LineDataSet lineDataSetX = new LineDataSet(null, graphType.charAt(0)+"x");
//        LineDataSet lineDataSetY = new LineDataSet(null, graphType.charAt(0)+"y");
//        LineDataSet lineDataSetZ = new LineDataSet(null, graphType.charAt(0)+"z");
//
//        LineData lineData = new LineData();
//        long t1 = System.currentTimeMillis();
//        List<SensorDataEntry> sensorDataReadings = getReadings(graphType,axes
//                ,device_id);
//        timeOfStart = sensorDataReadings.get(0).getTime();
//        if(sensorDataReadings != null){
//            for(SensorDataEntry sensorDataEntry : sensorDataReadings){
//                lineDataSetX.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getXval()));
//                lineDataSetY.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getYval()));
//                lineDataSetZ.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getZval()));
//            }
//        }
////        float ts_start = (sensorDataReadings.get(100).getTime() - timeOfStart)/1000.0f;
////        float ts_end = (sensorDataReadings.get(sensorDataReadings.size() - 101).getTime() - timeOfStart)/1000.0f;;
//        long t2 = System.currentTimeMillis();
//        System.out.println("Loading data "+graphType+", time="+(t2-t1));
//        formatPlotCircles(lineDataSetX);
//        formatPlotCircles(lineDataSetY);
//        formatPlotCircles(lineDataSetZ);
//
//        registerColorCodes(lineDataSetX,lineDataSetY, lineDataSetZ);
//
//        lineData.addDataSet(lineDataSetX);
//        lineData.addDataSet(lineDataSetY);
//        lineData.addDataSet(lineDataSetZ);
//        CustomGraphFragment.SaveLineData.put(graphType+device_id+axes, lineData);
//
//        plottable.setData(lineData);
////        plottable.setVisibleXRange(ts_start, ts_end);
//        prepareChart(plottable);
//    }

//    static List<SensorDataEntry> sensorDataReadings = null;
//    public void drawGraph_udpate(String graphType, LineChart plottable,
//                          String device_id, String axes) throws ExecutionException,
//            InterruptedException {
//
//        LineDataSet lineDataSetX = new LineDataSet(null, graphType.charAt(0)+"x");
//        LineDataSet lineDataSetY = new LineDataSet(null, graphType.charAt(0)+"y");
//        LineDataSet lineDataSetZ = new LineDataSet(null, graphType.charAt(0)+"z");
//
//        LineData lineData = new LineData();
//        long t1 = System.currentTimeMillis();
//        if(sensorDataReadings == null){
//            sensorDataReadings = getReadings(graphType,axes
//                    ,device_id);
//            timeOfStart = sensorDataReadings.get(0).getTime();
//
//        }else{
//            timeOfStart = sensorDataReadings.get(0).getTime();
//            long time = timeOfStart+50000;
//            float x, y, z;
//            Random random = new Random();
//            for (int i = 0; i < 10000; i++) {
//                time += 10;
//                SensorDataEntry sensorDataEntry = new SensorDataEntry();
//              x = 10*random.nextFloat();y = 10*random.nextFloat();z = 10*random.nextFloat();
//              sensorDataEntry.setTime(time);
//              sensorDataEntry.setXval(x);sensorDataEntry.setYval(y);sensorDataEntry.setZval(z);
//              sensorDataReadings.add(sensorDataEntry);
//            }
//        }
//        if(sensorDataReadings != null){
//            for(SensorDataEntry sensorDataEntry : sensorDataReadings){
//                lineDataSetX.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getXval()));
//                lineDataSetY.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getYval()));
//                lineDataSetZ.addEntry(new
//                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
//                        sensorDataEntry.getZval()));
//            }
//        }
//        float ts_start = (sensorDataReadings.get(100).getTime() - timeOfStart)/1000.0f;
//        float ts_end = (sensorDataReadings.get(sensorDataReadings.size() - 101).getTime() - timeOfStart)/1000.0f;
//
//        long t2 = System.currentTimeMillis();
//        System.out.println("Loading data "+graphType+", time="+(t2-t1));
//        formatPlotCircles(lineDataSetX);
//        formatPlotCircles(lineDataSetY);
//        formatPlotCircles(lineDataSetZ);
//
//        registerColorCodes(lineDataSetX,lineDataSetY, lineDataSetZ);
//
//        lineData.addDataSet(lineDataSetX);
//        lineData.addDataSet(lineDataSetY);
//        lineData.addDataSet(lineDataSetZ);
//        CustomGraphFragment.SaveLineData.put(graphType+device_id+axes, lineData);
//
//        plottable.setData(lineData);
//        plottable.setVisibleXRange(ts_start, ts_end);
//        prepareChart(plottable);
//    }

    public void prepareChart(LineChart anyLineChart) {

        /*Chart specific settings*/
        anyLineChart.setNoDataText("Loading");
        anyLineChart.setDragEnabled(true);
        anyLineChart.setPinchZoom(false);
        anyLineChart.setDrawGridBackground(false);
        anyLineChart.setScaleXEnabled(true);
        anyLineChart.setScaleYEnabled(true);
        anyLineChart.getLegend().setEnabled(true);
        anyLineChart.getXAxis().setDrawGridLines(true);
        anyLineChart.getXAxis().setDrawAxisLine(true);
        anyLineChart.getAxisLeft().setTextColor(Color.WHITE);
        anyLineChart.getXAxis().setTextColor(Color.WHITE);
        anyLineChart.getLegend().setTextColor(Color.WHITE);


        /*Legend-specific settings*/
        prepareLegend(anyLineChart);

        /*Axis specific settings*/
        XAxis xAxis = anyLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxisRight = anyLineChart.getAxisRight();
        yAxisRight.setEnabled(false);


        /*Default 1x zoom rendered for every iteration. loop used instead of default 2x zoom
         * for increase in zoom level
         * NOTE: Fling actions are not recognized at any zoom level above 1*/
//        for(int i = 0; i < 7; i++) {
//            anyLineChart.zoom(i,0,0,0, YAxis.AxisDependency.LEFT);
//        }
//        anyLineChart.zoom(7,0,0,0,YAxis.AxisDependency.LEFT);

        /*The invalidate() library method refreshes the chart and
         * graph contents*/
        anyLineChart.invalidate();
    }

    private void prepareLegend(LineChart anyLineChart) {
        Context mContext = MainActivity.shared().getApplicationContext();
        Legend legend = anyLineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setTextColor(Color.WHITE);
        anyLineChart.getDescription().setText(mContext.getString(R.string.time_measure));
        anyLineChart.getDescription().setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
    }

//    private static List<SensorDataEntry> getReadings(String graphType,
//                                                     String axes,
//                                                     String device_id) throws ExecutionException, InterruptedException {
//        List<SensorDataEntry> sensorDataReadings = new ArrayList<>();
//
//        Context mContext = MainActivity.shared().getApplicationContext();
//
//        String DEV_1 = mContext.getString(R.string.device1_tv);
//        String DEV_2 = mContext.getString(R.string.device2_tv);
//
//        if(axes.equalsIgnoreCase(mContext.getString(R.string.three_axis))){
//            if(device_id.equals(DEV_1)) {
//                sensorDataReadings =
//                        new GetDevice1ThreeAxisSensorDataAsyncTask().execute((long) 4).get();
//            }else{
//                sensorDataReadings =
//                        new GetDevice2ThreeAxisSensorDataAsyncTask().execute((long) 4).get();
//            }
//        }else if(axes.equalsIgnoreCase(mContext.getString(R.string.nine_axis))){
//            if(device_id.equals(DEV_1)) {
//                if(graphType.equalsIgnoreCase("Acceleration")){
//                    sensorDataReadings =
//                            new GetDevice1AccNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }else if(graphType.equalsIgnoreCase("Gyroscope")){
//                    sensorDataReadings =
//                            new GetDevice1GyroNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }else if(graphType.equalsIgnoreCase(("Magnetometer"))){
//                    sensorDataReadings =
//                            new GetDevice1MagnetoNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }
//            }else{
//                if(graphType.equalsIgnoreCase("Acceleration")){
//                    sensorDataReadings =
//                            new GetDevice2AccNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }else if(graphType.equalsIgnoreCase("Gyroscope")){
//                    sensorDataReadings =
//                            new GetDevice2GyroNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }else if(graphType.equalsIgnoreCase(("Magnetometer"))){
//                    sensorDataReadings =
//                            new GetDevice2MagnetoNineAxisSensorDataAsyncTask().execute((long)4).get();
//                }
//            }
//        }
//        return sensorDataReadings;
//    }



    //The accelerometer class
//    public class Accelerometer {
//
//        //LineChart specific datszets instantiated
//        private LineDataSet accLineDataSetX = new LineDataSet(null, "ax");
//        private LineDataSet accLineDataSetY = new LineDataSet(null, "ay");
//        private LineDataSet accLineDataSetZ = new LineDataSet(null, "az");
//
//        public LineDataSet getAccLineDataSetX() {
//            return accLineDataSetX;
//        }
//
//        public void setAccLineDataSetX(LineDataSet accLineDataSetX) {
//            this.accLineDataSetX = accLineDataSetX;
//        }
//
//        public LineDataSet getAccLineDataSetY() {
//            return accLineDataSetY;
//        }
//
//        public void setAccLineDataSetY(LineDataSet accLineDataSetY) {
//            this.accLineDataSetY = accLineDataSetY;
//        }
//
//        public LineDataSet getAccLineDataSetZ() {
//            return accLineDataSetZ;
//        }
//
//        public void setAccLineDataSetZ(LineDataSet accLineDataSetZ) {
//            this.accLineDataSetZ = accLineDataSetZ;
//        }
//
//        //The plot-able data is added here
//        public LineData accChartData = new LineData();
//
//        //The millisecond tick counter (NOT REALTIME)
////        float milliSecondTimeAcc = 0.0f;
//        List<AccelerometerData> accelerometerDataList = new ArrayList<>();
////        ArrayList<Float> sessionTimeEntries = new ArrayList<>();
//
//
//        public void getReadingsAcc() throws ExecutionException, InterruptedException {
//            accelerometerDataList = new GetAccNineAxisSensorDataAsyncTask().execute(mSessionId).get();
//            /**An array list of random accerometer data*/
//        }
//
//        //Adds all entries and prepares plot as per formatted settings
//        public void simulateStaticMilliSecondUpdate() throws ExecutionException, InterruptedException {
//            AccelerometerData[] accelerometerData = accelerometerDataList.toArray(new AccelerometerData[0]);
//            timestampData =  getCommonTimestamps();
//            timeOfStart = timestampData[0];
//            for (int row_i = 0; row_i < accelerometerData.length ; row_i++) {
//                float currentTimestamp = timestampData[row_i] - timeOfStart ;
//
//                    accLineDataSetX.addEntry(new Entry(currentTimestamp, accelerometerData[row_i].getAccX()));
//                    accLineDataSetY.addEntry(new Entry(currentTimestamp, accelerometerData[row_i].getAccY()));
//                    accLineDataSetZ.addEntry(new Entry(currentTimestamp, accelerometerData[row_i].getAccZ()));
//
////                accLineDataSetX.addEntry(new Entry(Float.valueOf(timeUpdateIterator.next()),accdata.getAccX()));
////                accLineDataSetY.addEntry(new Entry(timeUpdateIterator.next(),accdata.getAccY()));
////                accLineDataSetZ.addEntry(new Entry(timeUpdateIterator.next(),accdata.getAccY()));
//
////
////                accLineDataSetX.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccX()));
////                accLineDataSetY.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccY()));
////                accLineDataSetZ.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccZ()));2
//
////                milliSecondTimeAcc = milliSecondTimeAcc + milliSecondUpdater;
//            }
//            formatPlotCircles(accLineDataSetX);
//            formatPlotCircles(accLineDataSetY);
//            formatPlotCircles(accLineDataSetZ);
//            registerColorCodes(accLineDataSetX,
//                    accLineDataSetY, accLineDataSetZ);
//            timestampData = null;
//            accelerometerData = null;
//        }
//
//
//        //Associates all chart data with three-axis datasetes
//
//        //Note that each dimensions is associated with a dataset color
//
//        public void displayStaticMilliSecondUpdate() {
//            accChartData.addDataSet(accLineDataSetX);
//            accChartData.addDataSet(accLineDataSetY);
//            accChartData.addDataSet(accLineDataSetZ);
//        }
//
//    }

    //Other nine-axis nested classes are modeled in very similar structure

    //The gyroscope class
//    public class Gyroscope {
//
//        public LineDataSet gyroLineDataSetX = new LineDataSet(null, "gX");
//        private LineDataSet gyroLineDataSetY = new LineDataSet(null, "gY");
//        private LineDataSet gyroLineDataSetZ = new LineDataSet(null, "gZ");
//
//
//        public LineData gyroChartData = new LineData();
//        private List<GyroscopeData> gyroscopeDataList = new ArrayList<GyroscopeData>();
////        float milliSecondTimeGyr = 0.0f;
//
//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//        public void getReadingsGyr() throws ExecutionException, InterruptedException {
//            gyroscopeDataList =  new GetGyroNineAxisSensorDataAsyncTask().execute(mSessionId).get();
////            ArrayList<GyroscopeData> gyrList = new ArrayList<>();
////            for (int i = 0; i < DATA_LIST_SIZE; i++) {
////                gyroListEntries.add(new GyroscopeData());
////            }
////            /**An array list of random accerometer data*/
////            return gyrList;
//        }
//
//        public void simulateStaticMilliSecondUpdate() throws ExecutionException, InterruptedException {
//            GyroscopeData[] gyroscopeData = gyroscopeDataList.toArray(new GyroscopeData[0]);
//            timestampData = getCommonTimestamps();
//            timeOfStart = timestampData[0];
//            for (int row_i = 0; row_i < gyroscopeData.length ; row_i++) {
//                float currentTimestamp = timestampData[row_i] - timeOfStart ;
//
//                gyroLineDataSetX.addEntry(new Entry(currentTimestamp, gyroscopeData[row_i].getGyrX()));
//                gyroLineDataSetY.addEntry(new Entry(currentTimestamp, gyroscopeData[row_i].getGyrY()));
//                gyroLineDataSetZ.addEntry(new Entry(currentTimestamp, gyroscopeData[row_i].getGyrZ()));
//            }
//
//
//
////                gyroLineDataSetX.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrX()));
////                gyroLineDataSetY.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrY()));
////                gyroLineDataSetZ.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrZ()));
////
////                milliSecondTimeGyr = milliSecondTimeGyr + milliSecondUpdater
//
//            formatPlotCircles(gyroLineDataSetX);
//            formatPlotCircles(gyroLineDataSetY);
//            formatPlotCircles(gyroLineDataSetZ);
//            registerColorCodes(gyroLineDataSetX,
//                    gyroLineDataSetY, gyroLineDataSetZ);
//            timestampData = null;
//            gyroscopeData = null;
//        }
//
//        public void displayStaticMilliSecondUpdate() {
//            gyroChartData.addDataSet(gyroLineDataSetX);
//            gyroChartData.addDataSet(gyroLineDataSetY);
//            gyroChartData.addDataSet(gyroLineDataSetZ);
//        }
//
//
//    }

    //The magnetometer class
//    public class Magnetometer {
//
//
//        //LineChart specific datsets instantiated
//        private LineDataSet magLineDataSetX = new LineDataSet(null, "ax");
//        private LineDataSet magLineDataSetY = new LineDataSet(null, "ay");
//        private LineDataSet magLineDataSetZ = new LineDataSet(null, "az");
//
//        public LineDataSet getMagLineDataSetX() {
//            return magLineDataSetX;
//        }
//
//        public void setMagLineDataSetX(LineDataSet magLineDataSetX) {
//            this.magLineDataSetX = magLineDataSetX;
//        }
//
//        public LineDataSet getMagLineDataSetY() {
//            return magLineDataSetY;
//        }
//
//        public void setMagLineDataSetY(LineDataSet magLineDataSetY) {
//            this.magLineDataSetY = magLineDataSetY;
//        }
//
//        public LineDataSet getAccLineDataSetZ() {
//            return magLineDataSetZ;
//        }
//
//        public void setAccLineDataSetZ(LineDataSet accLineDataSetZ) {
//            this.magLineDataSetZ = accLineDataSetZ;
//        }
//
//        //The plot-able data is added here
//        public LineData magnetoChartData = new LineData();
//
//        List<MagnetometerData> magnetometerDataList = new ArrayList<>();
////        ArrayList<Float> sessionTimeEntries = new ArrayList<>();
//
//
//        public void getReadingsMag() throws ExecutionException, InterruptedException {
//            magnetometerDataList = new GetMagnetoNineAxisSensorDataAsyncTask().execute(mSessionId).get();
//            /**An array list of random accerometer data*/
//        }
//
//        //Adds all entries and prepares plot as per formatted settings
//        public void simulateStaticMilliSecondUpdate() throws ExecutionException, InterruptedException {
//            MagnetometerData[] magnetometerData = magnetometerDataList.toArray(new MagnetometerData[0]);
//            timestampData =  getCommonTimestamps();
//            timeOfStart = timestampData[0];
//            for (int row_i = 0; row_i < magnetometerData.length ; row_i++) {
//                float currentTimestamp = timestampData[row_i] - timeOfStart ;
//
//                magLineDataSetX.addEntry(new Entry(currentTimestamp, magnetometerData[row_i].getMagnetoX()));
//                magLineDataSetY.addEntry(new Entry(currentTimestamp, magnetometerData[row_i].getMagnetoY()));
//                magLineDataSetZ.addEntry(new Entry(currentTimestamp, magnetometerData[row_i].getMagnetoZ()));
//
////                accLineDataSetX.addEntry(new Entry(Float.valueOf(timeUpdateIterator.next()),accdata.getAccX()));
////                accLineDataSetY.addEntry(new Entry(timeUpdateIterator.next(),accdata.getAccY()));
////                accLineDataSetZ.addEntry(new Entry(timeUpdateIterator.next(),accdata.getAccY()));
//
////
////                accLineDataSetX.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccX()));
////                accLineDataSetY.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccY()));
////                accLineDataSetZ.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccZ()));2
//
////                milliSecondTimeAcc = milliSecondTimeAcc + milliSecondUpdater;
//            }
//            formatPlotCircles(magLineDataSetX);
//            formatPlotCircles(magLineDataSetY);
//            formatPlotCircles(magLineDataSetZ);
//            registerColorCodes(magLineDataSetX,
//                    magLineDataSetY, magLineDataSetZ);
//            timestampData = null;
//            magnetometerData = null;
//        }
//
//
//        public void displayStaticMilliSecondUpdate() {
//            magnetoChartData.addDataSet(magLineDataSetX);
//            magnetoChartData.addDataSet(magLineDataSetY);
//            magnetoChartData.addDataSet(magLineDataSetZ);
//        }
//
//    }
//
//    private static class GetAccNineAxisSensorDataAsyncTask extends AsyncTask<Integer,Void,Float[]> {
//        @Override
//        protected Float[] doInBackground(Integer... sessions) {
//            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().geHelmetAccelerometerData(sessions);
//        }
//
//        @Override
//        protected void onPostExecute(Float[] result) {
//            super.onPostExecute(result);
//            dataQueryResult = result;
//            System.out.println(dataQueryResult.length);
//        }
//    }

//    private static class GetAccNineAxisSensorDataAsyncTask extends AsyncTask<Long,Void,Float[]> {
//
//
//    @Override
//    protected Float[] doInBackground(Long... longs) {
//        return SessionCdlDb.getInstance(mContext).getSessionDataDAO().geHelmetAccelerometerData(longs);
//    }
//
//    @Override
//        protected void onPostExecute(Float[] result) {
//            super.onPostExecute(result);
//            dataQueryResult = result;
//            System.out.println("size of query result-->"+dataQueryResult.length);
//        }
//    }

    public static class GetDevice1AccNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters, Void, List<SensorDataEntry> > {

        protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
            QueryParameters queryParameters1 = queryParameters[0];
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetAccelerometerData(queryParameters1.session_id,queryParameters1.tStart, queryParameters1.tEnd);
        }

        @Override
        protected void onPostExecute(List<SensorDataEntry> sensorDataEntryList) {
            super.onPostExecute(sensorDataEntryList);
            Log.d("Nine axis", "onPostExecute: size"+sensorDataEntryList.size());
        }
    }


//    public static class GetAccNineAxisSensorDataAsyncTask extends AsyncTask<Long, Void, List<SensorDataEntry> > {
//
//        @Override
//        protected List<SensorDataEntry> doInBackground(Long... longs) {
//            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().getHelmetAccelerometerData(longs);
//        }
//
//        @Override
//        protected void onPostExecute(List<SensorDataEntry> sensorDataEntries) {
//            super.onPostExecute(sensorDataEntries);
//            try {
//                Log.d("NineAxis", "onPostExecute: Entries size" + sensorDataEntries.size());
//            }catch (NullPointerException e){
//                System.out.println("List is empty");
//            }
//        }
//    }


    public static class GetSessionTimestampsAsyncTask extends AsyncTask<Long, Void,Long[]> {

        @Override
        protected Long[] doInBackground(Long... longs) {
            return  SessionCdlDb.getInstance().getSessionDataDAO().getTimestampsForSession(longs);
        }
    }

    public static class GetDevice1GyroNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,Void,List<SensorDataEntry>>{

        protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
            QueryParameters queryParameters1 = queryParameters[0];
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetGyroscopeData(queryParameters1.session_id,queryParameters1.tStart, queryParameters1.tEnd);
        }
    }

    public static class GetDevice1MagnetoNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,Void,List<SensorDataEntry>>{

        protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
            QueryParameters queryParameters1 = queryParameters[0];
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetMagnetometerData(queryParameters1.session_id,queryParameters1.tStart, queryParameters1.tEnd);
        }
    }
}
