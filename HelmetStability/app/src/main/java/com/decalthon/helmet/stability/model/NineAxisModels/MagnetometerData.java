package com.decalthon.helmet.stability.model.NineAxisModels;

import java.util.concurrent.ThreadLocalRandom;

public  class MagnetometerData {
    float magnetoX;
    float magnetoY;
    float magnetoZ;

    public MagnetometerData(){
        magnetoX = (float) (ThreadLocalRandom.current().nextFloat()*200)-100;
        magnetoY = (float)(ThreadLocalRandom.current().nextFloat()*200)-100;
        magnetoZ = (float)(ThreadLocalRandom.current().nextFloat()*200)-100;
    }

    public float getMagnetoX() {

        return this.magnetoX;
    }
    public float getMagnetoY(){

        return  this.magnetoY;
    }
    public float getMagnetoZ() {

        return this.magnetoZ;
    }

}
