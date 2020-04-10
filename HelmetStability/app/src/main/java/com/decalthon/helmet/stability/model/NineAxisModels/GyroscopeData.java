package com.decalthon.helmet.stability.model.NineAxisModels;

import java.util.concurrent.ThreadLocalRandom;

public  class GyroscopeData {
    float gyrX;
    float gyrY;
    float gyrZ;

    public GyroscopeData() {
        gyrX = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
        gyrY = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
        gyrZ = (float) (ThreadLocalRandom.current().nextFloat() * 200) - 100;
        //        time = (float)Math.random()*TIME_FRAME;
        //Instead use time in sorted order

    }

    public float getGyrX() {

        return this.gyrX;
    }

    public float getGyrY() {

        return this.gyrY;
    }

    public float getGyrZ() {

        return this.gyrZ;
    }

}
