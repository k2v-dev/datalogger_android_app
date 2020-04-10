package com.decalthon.helmet.stability.model.DeviceModels;

public class BatteryLevel {
    public final int level;
    public String device_id;
    public BatteryLevel( int level , String device_id )
    {
        this.level = level;
        this.device_id = device_id;
    }

}
