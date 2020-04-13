package com.decalthon.helmet.stability.model.NineAxisModels;

import androidx.room.ColumnInfo;

import java.util.concurrent.ThreadLocalRandom;

public  class GyroscopeData {
    @ColumnInfo(name = "gx_9axis_dev1")
    private float gyrX;

    @ColumnInfo(name = "gy_9axis_dev1")
    private float gyrY;

    @ColumnInfo(name = "gz_9axis_dev1")
    private float gyrZ;

    public GyroscopeData() {

    }

    public float getGyrX() {
        return gyrX;
    }

    public void setGyrX(float gyrX) {
        this.gyrX = gyrX;
    }

    public float getGyrY() {
        return gyrY;
    }

    public void setGyrY(float gyrY) {
        this.gyrY = gyrY;
    }

    public float getGyrZ() {
        return gyrZ;
    }

    public void setGyrZ(float gyrZ) {
        this.gyrZ = gyrZ;
    }
}
