package com.decalthon.helmet.stability.model.nineaxismodels;

import androidx.room.ColumnInfo;

public  class MagnetometerData {
    @ColumnInfo(name = "mx_9axis_dev1")
    float magnetoX;

    @ColumnInfo(name = "my_9axis_dev1")
    float magnetoY;

    @ColumnInfo(name = "mz_9axis_dev1")
    float magnetoZ;

    public void setMagnetoX(float magnetoX) {
        this.magnetoX = magnetoX;
    }

    public void setMagnetoY(float magnetoY) {
        this.magnetoY = magnetoY;
    }

    public void setMagnetoZ(float magnetoZ) {
        this.magnetoZ = magnetoZ;
    }

    public MagnetometerData(){
//        magnetoX = (float) (ThreadLocalRandom.current().nextFloat()*200)-100;
//        magnetoY = (float)(ThreadLocalRandom.current().nextFloat()*200)-100;
//        magnetoZ = (float)(ThreadLocalRandom.current().nextFloat()*200)-100;
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
