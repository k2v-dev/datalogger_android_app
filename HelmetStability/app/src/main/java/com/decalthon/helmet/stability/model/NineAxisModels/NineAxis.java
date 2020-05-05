package com.decalthon.helmet.stability.model.NineAxisModels;


import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks.GetDevice1ThreeAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks.GetDevice2AccNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks.GetDevice2GyroNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks.GetDevice2MagnetoNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks.GetDevice2ThreeAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
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
    public void registerColorCodes(LineDataSet xAxis,
                                   LineDataSet yAxis, LineDataSet zAxis) {
        xAxis.setColor(Color.RED);
        yAxis.setColor(Color.GREEN);
        zAxis.setColor(Color.BLUE);
    }

    //Plot circles are removed for all plots to save screens space
    public static void formatPlotCircles(LineDataSet anyDataSet) {
        anyDataSet.setDrawCircles(false);
        anyDataSet.setDrawFilled(false);
        anyDataSet.setDrawValues(false);
    }


    public void drawGraph(String graphType, LineChart plottable,
                          String device_id, String axes) throws ExecutionException,
            InterruptedException {

        LineDataSet lineDataSetX = new LineDataSet(null, graphType.charAt(0)+"x");
        LineDataSet lineDataSetY = new LineDataSet(null, graphType.charAt(0)+"y");
        LineDataSet lineDataSetZ = new LineDataSet(null, graphType.charAt(0)+"z");

        LineData lineData = new LineData();

        List<SensorDataEntry> sensorDataReadings = getReadings(graphType,axes
                ,device_id);
        timeOfStart = sensorDataReadings.get(0).getTime();
        if(sensorDataReadings != null){
            for(SensorDataEntry sensorDataEntry : sensorDataReadings){
                lineDataSetX.addEntry(new
                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
                        sensorDataEntry.getXval()));
                lineDataSetY.addEntry(new
                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
                        sensorDataEntry.getYval()));
                lineDataSetZ.addEntry(new
                        Entry((sensorDataEntry.getTime() - timeOfStart)/1000.0f,
                        sensorDataEntry.getZval()));
            }
        }
        formatPlotCircles(lineDataSetX);
        formatPlotCircles(lineDataSetY);
        formatPlotCircles(lineDataSetZ);

        registerColorCodes(lineDataSetX,lineDataSetY, lineDataSetZ);

        lineData.addDataSet(lineDataSetX);
        lineData.addDataSet(lineDataSetY);
        lineData.addDataSet(lineDataSetZ);

        plottable.setData(lineData);
        prepareChart(plottable);
    }

    private void prepareChart(LineChart anyLineChart) {

        /*Chart specific settings*/
        anyLineChart.setNoDataText("Loading");
        anyLineChart.setDragEnabled(true);
        anyLineChart.setDrawGridBackground(false);
        anyLineChart.setPinchZoom(true);
        anyLineChart.getLegend().setEnabled(true);
        anyLineChart.getXAxis().setDrawGridLines(true);
        anyLineChart.getXAxis().setDrawAxisLine(true);

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
        for(int i = 0; i < 7; i++) {
            anyLineChart.zoom(i,0,0,0, YAxis.AxisDependency.LEFT);
        }
//        anyLineChart.zoom(7,0,0,0,YAxis.AxisDependency.LEFT);

        /*The invalidate() library method refreshes the chart and
         * graph contents*/
        anyLineChart.invalidate();
    }

    private void prepareLegend(LineChart anyLineChart) {
        Context mContext = MainActivity.shared().getApplicationContext();
        Legend legend = anyLineChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        anyLineChart.getDescription().setText(mContext.getString(R.string.time_measure));
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
    }

    private static List<SensorDataEntry> getReadings(String graphType,
                                                     String axes,
                                                     String device_id) throws ExecutionException, InterruptedException {
        List<SensorDataEntry> sensorDataReadings = new ArrayList<>();

        Context mContext = MainActivity.shared().getApplicationContext();

        String DEV_1 = mContext.getString(R.string.device1_tv);
        String DEV_2 = mContext.getString(R.string.device2_tv);

        if(axes.equalsIgnoreCase(mContext.getString(R.string.three_axis))){
            if(device_id.equals(DEV_1)) {
                sensorDataReadings =
                        new GetDevice1ThreeAxisSensorDataAsyncTask().execute((long) 4).get();
            }else{
                sensorDataReadings =
                        new GetDevice2ThreeAxisSensorDataAsyncTask().execute((long) 4).get();
            }
        }else if(axes.equalsIgnoreCase(mContext.getString(R.string.nine_axis))){
            if(device_id.equals(DEV_1)) {
                if(graphType.equalsIgnoreCase("Acceleration")){
                    sensorDataReadings =
                            new GetDevice1AccNineAxisSensorDataAsyncTask().execute((long)4).get();
                }else if(graphType.equalsIgnoreCase("Gyroscope")){
                    sensorDataReadings =
                            new GetDevice1GyroNineAxisSensorDataAsyncTask().execute((long)4).get();
                }else if(graphType.equalsIgnoreCase(("Magnetometer"))){
                    sensorDataReadings =
                            new GetDevice1MagnetoNineAxisSensorDataAsyncTask().execute((long)4).get();
                }
            }else{
                if(graphType.equalsIgnoreCase("Acceleration")){
                    sensorDataReadings =
                            new GetDevice2AccNineAxisSensorDataAsyncTask().execute((long)4).get();
                }else if(graphType.equalsIgnoreCase("Gyroscope")){
                    sensorDataReadings =
                            new GetDevice2GyroNineAxisSensorDataAsyncTask().execute((long)4).get();
                }else if(graphType.equalsIgnoreCase(("Magnetometer"))){
                    sensorDataReadings =
                            new GetDevice2MagnetoNineAxisSensorDataAsyncTask().execute((long)4).get();
                }
            }
        }
        return sensorDataReadings;
    }



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

    public static class GetDevice1AccNineAxisSensorDataAsyncTask extends AsyncTask<Long, Void, List<SensorDataEntry> > {

        @Override
        protected List<SensorDataEntry> doInBackground(Long... longs) {
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetAccelerometerData(longs);
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

    public static class GetDevice1GyroNineAxisSensorDataAsyncTask extends AsyncTask<Long,Void,List<SensorDataEntry>>{

        @Override
        protected List<SensorDataEntry> doInBackground(Long... longs) {
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetGyroscopeData(longs);
        }
    }

    public static class GetDevice1MagnetoNineAxisSensorDataAsyncTask extends AsyncTask<Long,Void,List<SensorDataEntry>>{

        @Override
        protected List<SensorDataEntry> doInBackground(Long... longs) {
            return SessionCdlDb.getInstance().getSessionDataDAO().getHelmetMagnetometerData(longs);
        }
    }
}
