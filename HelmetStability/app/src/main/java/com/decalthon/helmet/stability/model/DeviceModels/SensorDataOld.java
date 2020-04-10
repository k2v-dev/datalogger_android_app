package com.decalthon.helmet.stability.model.DeviceModels;

import androidx.annotation.NonNull;

import com.decalthon.helmet.stability.Utilities.Constants;


// It has data of helmet , sensory watch and heart rate belt.
// Use for write to log file
// These data are updated in every second and write to log file
public class SensorDataOld {
    public String connectionStr = "";
    public float[] dev1_humidity =  new float[Constants.DEV1_NUM_SENSORS];
    public float[] dev1_temp =  new float[Constants.DEV1_NUM_SENSORS];
    public short dev2_sensation_t;
    public short dev2_sensation_h;
    public String dev2_sensation_t_str;
    public String dev2_sensation_h_str;
    public short dev3_hr ;
    public float speed;
    public float speedAccuracy;
    public float locationAccuracy;

    @NonNull
    @Override
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        for(int i=0; i < Constants.DEV1_NUM_SENSORS; i++){
            strBuffer.append(String.format("%5.1f;%5.1f;",  dev1_humidity[i], dev1_temp[i]));
        }
        strBuffer.append(String.format("%12d;%12d;", dev2_sensation_t, dev2_sensation_h));
        strBuffer.append(String.format("%5d;", dev3_hr));
        strBuffer.append(String.format("%5.1f;", speed));
        strBuffer.append(String.format("%5.1f",speedAccuracy));
        strBuffer.append(String.format("%5.1f",locationAccuracy));
        return strBuffer.toString();
    }

    // Create header string which will be write to log file at time of log file's creation
    public static String getHeader() {
        StringBuffer strBuffer = new StringBuffer("");
        for(int i=0; i < Constants.DEV1_NUM_SENSORS; i++){
            strBuffer.append(String.format("%5s;%5s;", "Hr_"+i, "T_"+i));
        }
        strBuffer.append(String.format("%12s;%12s;", "Sensation_T", "Sensation_H"));
        strBuffer.append(String.format("%5s;", "HR"));
        strBuffer.append(String.format("%5s;", "GPS Speed"));
        strBuffer.append(String.format("%5s;", "GPS Speed Accuracy"));
        strBuffer.append(String.format("%5s;", "GPS Location Accuracy"));

        return strBuffer.toString();
    }

}
