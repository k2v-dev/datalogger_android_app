package com.decalthon.helmet.stability.model.DeviceModels;

import com.decalthon.helmet.stability.Utilities.Constants;

// Pojo class for Helmet data
public class HelmetData {
    public float[] temp = new float[Constants.DEV1_NUM_SENSORS]; // temperature array of 8 sensors
    public float[] humdity = new float[Constants.DEV1_NUM_SENSORS]; // humidity array of 8 sensors

    // For print each sensor's temperature and humidity
    public void print(){
        for(int i=0 ; i< 8 ; i++){
            System.out.print(" temp="+temp[i]+", humidity="+humdity[i]);
        }
        System.out.println(" ");
    }
}
