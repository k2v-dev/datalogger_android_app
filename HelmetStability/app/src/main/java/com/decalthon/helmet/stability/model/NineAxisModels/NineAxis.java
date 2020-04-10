package com.decalthon.helmet.stability.model.NineAxisModels;


import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.decalthon.helmet.stability.Utilities.Constants;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import static com.decalthon.helmet.stability.Utilities.Constants.DATA_LIST_SIZE;


public class NineAxis {

    String type;

    //Used to plot a point of each color in every millisecond time gap
    private static float milliSecondUpdater = Constants.UPDATER_STATIC;

    //Default constructor
    public NineAxis(){

    }

    //Color codes are associated with axes for each dataset
    public void registerColorCodes(LineDataSet xAxis,
                                   LineDataSet yAxis, LineDataSet zAxis){
        xAxis.setColor(Color.RED);
        yAxis.setColor(Color.GREEN);
        zAxis.setColor(Color.BLUE);
    }

    //Plot circles are removed for all plots to save screens space
    public void formatPlotCircles(LineDataSet anyDataSet){
        anyDataSet.setDrawCircles(false);
        anyDataSet.setDrawFilled(false);
    }

    //The accelerometer class
    public class Accelerometer {

        //LineChart specific datsets instantiated
        public LineDataSet accLineDataSetX = new LineDataSet(null, "AccX");
        LineDataSet accLineDataSetY = new LineDataSet(null, "AccY");
        LineDataSet accLineDataSetZ = new LineDataSet(null, "AccZ");

        //The plot-able data is added here
        public LineData accChartData = new LineData();

        //The millisecond tick counter (NOT REALTIME)
        float milliSecondTimeAcc= 0.0f;
        ArrayList<AccelerometerData> accListEntries = new ArrayList<>();


        //Acquires random entries in the range -100.0f to 100.0f
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ArrayList<AccelerometerData> getReadingsAcc() {
            ArrayList<AccelerometerData> accList = new ArrayList<>();
            for (int i = 0; i < DATA_LIST_SIZE; i++) {
                accListEntries.add(new AccelerometerData());
            }
            /**An array list of random accerometer data*/
            return  accList;
        }

        //Adds all entries and prepares plot as per formatted settings
        public void simulateStaticMilliSecondUpdate(){
            for (AccelerometerData accdata : accListEntries) {


                accLineDataSetX.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccX()));
                accLineDataSetY.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccY()));
                accLineDataSetZ.addEntry(new Entry(milliSecondTimeAcc, accdata.getAccZ()));

                milliSecondTimeAcc = milliSecondTimeAcc + milliSecondUpdater;
            }

            formatPlotCircles(accLineDataSetX);
            formatPlotCircles(accLineDataSetY);
            formatPlotCircles(accLineDataSetZ);
            registerColorCodes(accLineDataSetX,
                    accLineDataSetY,accLineDataSetZ);
        }


        //Associates all chart data with three-axis datasetes

        //Note that each dimensions is associated with a dataset color

        public void displayStaticMilliSecondUpdate () {
            accChartData.addDataSet(accLineDataSetX);
            accChartData.addDataSet(accLineDataSetY);
            accChartData.addDataSet(accLineDataSetZ);
        }

    }

    //Other nine-axis nested classes are modeled in very similar structure

    //The gyroscope class
    public class Gyroscope {

        public  LineDataSet gyroLineDataSetX = new LineDataSet(null, "gX");
        private LineDataSet gyroLineDataSetY = new LineDataSet(null, "gY");
        private LineDataSet gyroLineDataSetZ = new LineDataSet(null, "gZ");
        public LineData gyroChartData = new LineData();
        private ArrayList<GyroscopeData> gyroListEntries = new ArrayList<GyroscopeData>();
        float milliSecondTimeGyr= 0.0f;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ArrayList<GyroscopeData> getReadingsGyr() {
            ArrayList<GyroscopeData> gyrList = new ArrayList<>();
            for (int i = 0; i < DATA_LIST_SIZE; i++) {
                gyroListEntries.add(new GyroscopeData());
            }
            /**An array list of random accerometer data*/
            return gyrList;
        }

        public void simulateStaticMilliSecondUpdate() {
            for (GyroscopeData gyrdata : gyroListEntries) {


                gyroLineDataSetX.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrX()));
                gyroLineDataSetY.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrY()));
                gyroLineDataSetZ.addEntry(new Entry(milliSecondTimeGyr, gyrdata.getGyrZ()));

                milliSecondTimeGyr = milliSecondTimeGyr + milliSecondUpdater;
            }

            formatPlotCircles(gyroLineDataSetX);
            formatPlotCircles(gyroLineDataSetY);
            formatPlotCircles(gyroLineDataSetZ);
            registerColorCodes(gyroLineDataSetX,
                    gyroLineDataSetY, gyroLineDataSetZ);
        }
        public void displayStaticMilliSecondUpdate () {
            gyroChartData.addDataSet(gyroLineDataSetX);
            gyroChartData.addDataSet(gyroLineDataSetY);
            gyroChartData.addDataSet(gyroLineDataSetZ);
        }

    }

    //The magnetometer class
    public class Magnetometer {
        private MagnetometerData magnetometerData;
        public LineDataSet magnetoLineDataSetX = new LineDataSet(null, "aX");
        private LineDataSet magnetoLineDataSetY = new LineDataSet(null, "aY");
        private LineDataSet magnetoLineDataSetZ = new LineDataSet(null, "aZ");
        public LineData magnetoChartData = new LineData();
        private ArrayList<MagnetometerData> magnetoListEntries = new ArrayList<>();

        float milliSecondTimeMag = 0.0f;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ArrayList<MagnetometerData> getReadingsMag() {
            ArrayList<MagnetometerData> magList = new ArrayList<>();
            for (int i = 0; i < DATA_LIST_SIZE; i++) {
                magnetoListEntries.add(new MagnetometerData());
            }
            /**An array list of random accerometer data*/
            return magList;
        }

        public void simulateStaticMilliSecondUpdate() {
            for (MagnetometerData magdata : magnetoListEntries) {


                magnetoLineDataSetX.addEntry(new Entry(milliSecondTimeMag, magdata.getMagnetoX()));
                magnetoLineDataSetY.addEntry(new Entry(milliSecondTimeMag, magdata.getMagnetoY()));
                magnetoLineDataSetZ.addEntry(new Entry(milliSecondTimeMag, magdata.getMagnetoZ()));

                milliSecondTimeMag = milliSecondTimeMag + milliSecondUpdater;
            }

            formatPlotCircles(magnetoLineDataSetX);
            formatPlotCircles(magnetoLineDataSetY);
            formatPlotCircles(magnetoLineDataSetZ);
            registerColorCodes(magnetoLineDataSetX,
                    magnetoLineDataSetY, magnetoLineDataSetZ);
        }

        public void displayStaticMilliSecondUpdate () {
            magnetoChartData.addDataSet(magnetoLineDataSetX);
            magnetoChartData.addDataSet(magnetoLineDataSetY);
            magnetoChartData.addDataSet(magnetoLineDataSetZ);
        }

    }

}

