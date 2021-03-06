package com.decalthon.helmet.stability.model.nineaxismodels;

import androidx.room.ColumnInfo;

public class AccelerometerData {

    @ColumnInfo(name = "ax_9axis_dev1")
    public Float accX;
    @ColumnInfo(name = "ay_9axis_dev1")
    public  Float accY;
    @ColumnInfo(name = "az_9axis_dev1")
    public Float accZ;

    public AccelerometerData() {

    }

    public Float getAccX() {
        return accX;
    }

    public void setAccX(Float accX) {
        this.accX = accX;
    }

    public Float getAccY() {
        return accY;
    }

    public void setAccY(Float accY) {
        this.accY = accY;
    }

    public Float getAccZ() {
        return accZ;
    }

    public void setAccZ(Float accZ) {
        this.accZ = accZ;
    }
}
