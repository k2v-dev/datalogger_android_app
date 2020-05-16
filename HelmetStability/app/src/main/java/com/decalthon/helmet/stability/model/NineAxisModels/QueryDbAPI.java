package com.decalthon.helmet.stability.model.nineaxismodels;

import android.content.Context;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetDevice1ThreeAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetDevice2AccNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetDevice2GyroNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetDevice2MagnetoNineAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetDevice2ThreeAxisSensorDataAsyncTask;
import com.decalthon.helmet.stability.fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class QueryDbAPI {
    public List<SensorDataEntry> getReadings(QueryParameters queryParameters) throws ExecutionException, InterruptedException {
//    private static List<SensorDataEntry> getReadings(String graphType,
//                                                     String axes,
//                                                     String device_id) throws ExecutionException, InterruptedException {
        List<SensorDataEntry> sensorDataReadings = new ArrayList<>();

        Context mContext = MainActivity.shared().getApplicationContext();

        String DEV_1 = mContext.getString(R.string.device1_tv);
        String DEV_2 = mContext.getString(R.string.device2_tv);

        if(queryParameters.axes.equalsIgnoreCase(mContext.getString(R.string.three_axis))){
            if(queryParameters.device_id.equals(DEV_1)) {
                sensorDataReadings =
                        new GetDevice1ThreeAxisSensorDataAsyncTask().execute(queryParameters).get();
            }else{
                sensorDataReadings =
                        new GetDevice2ThreeAxisSensorDataAsyncTask().execute(queryParameters).get();
            }
        }else if(queryParameters.axes.equalsIgnoreCase(mContext.getString(R.string.nine_axis))){
            if(queryParameters.device_id.equals(DEV_1)) {
                if(queryParameters.graphType.equalsIgnoreCase("Acceleration")){
                    sensorDataReadings =
                            new NineAxis.GetDevice1AccNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }else if(queryParameters.graphType.equalsIgnoreCase("Gyroscope")){
                    sensorDataReadings =
                            new NineAxis.GetDevice1GyroNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }else if(queryParameters.graphType.equalsIgnoreCase(("Magnetometer"))){
                    sensorDataReadings =
                            new NineAxis.GetDevice1MagnetoNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }
            }else{
                if(queryParameters.graphType.equalsIgnoreCase("Acceleration")){
                    sensorDataReadings =
                            new GetDevice2AccNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }else if(queryParameters.graphType.equalsIgnoreCase("Gyroscope")){
                    sensorDataReadings =
                            new GetDevice2GyroNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }else if(queryParameters.graphType.equalsIgnoreCase(("Magnetometer"))){
                    sensorDataReadings =
                            new GetDevice2MagnetoNineAxisSensorDataAsyncTask().execute(queryParameters).get();
                }
            }
        }

        return sensorDataReadings;
    }

    public List<SensorDataEntry> reloadData(QueryParameters queryParameters){
        List<SensorDataEntry> sensorDataReadings = new ArrayList<>();
        Map<Integer, SensorDataEntry> maps = null;
        try{
            if(queryParameters.chartType == ChartType.ACC){
                maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).accChartData;
            }else if(queryParameters.chartType == ChartType.GYR){
                maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).gyrChartData;
            }else if(queryParameters.chartType == ChartType.MAG){
                maps = CustomGraphFragment.SAVE_TAB_DATA.get(queryParameters.fragmentType).magChartData;
            }

            if(maps != null && maps.size() > 0){
                for (Map.Entry<Integer, SensorDataEntry> entryE:maps.entrySet()
                ) {
                    sensorDataReadings.add(entryE.getValue());
                }
            }
        }catch (Exception ex){

        }
        return sensorDataReadings;
    }
}
