package com.decalthon.helmet.stability.model.NineAxisModels;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.concurrent.ThreadLocalRandom;

public class AccelerometerData {
    float accX;
    float accY;
    float accZ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AccelerometerData() {
        accX = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
        accY = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
        accZ = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
    }

    public float getAccX() {

        return this.accX;
    }

    public float getAccY() {

        return this.accY;
    }

    public float getAccZ() {

        return this.accZ;
    }
}
