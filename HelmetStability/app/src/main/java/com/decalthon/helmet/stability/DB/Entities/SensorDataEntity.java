package com.decalthon.helmet.stability.DB.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

//This class is reached when a new packet table  has been generated
//@Entity(primaryKeys = {"packet_number","dateMillis"})

@Entity(foreignKeys = @ForeignKey(entity = SessionSummary.class, parentColumns = "session_id",childColumns = "session_id", onDelete = CASCADE))
@ForeignKey(entity = SessionSummary.class, parentColumns = "session_id", childColumns = "packet_number")
public class SensorDataEntity {

    public long packet_number;

    public long session_id;

    @Ignore
    public Date date;

    @PrimaryKey
    public long dateMillis;

    public float ax_9axis_dev1;

    public float ay_9axis_dev1;

    public float az_9axis_dev1;

    public float gx_9axis_dev1;

    public float gy_9axis_dev1;

    public float gz_9axis_dev1;

    public float mx_9axis_dev1;

    public float my_9axis_dev1;

    public float mz_9axis_dev1;

    public float ax_3axis_dev1;

    public float ay_3axis_dev1;

    public float az_3axis_dev1;

    // Commented below 3 line , It will be used in future
//    float gps_aux;
//    int hr;
//    Date hr_date;

    public float ax_9axis_dev2;
    public float ay_9axis_dev2;
    public float az_9axis_dev2;
    public float gx_9axis_dev2;
    public float gy_9axis_dev2;
    public float gz_9axis_dev2;
    public float mx_9axis_dev2;
    public float my_9axis_dev2;
    public float mz_9axis_dev2;
    public float ax_3axis_dev2;
    public float ay_3axis_dev2;
    public float az_3axis_dev2;

    public float frontal_slippage;
    public float sagital_slippage;

    public float getFrontal_slippage() {
        return frontal_slippage;
    }

    public void setFrontal_slippage(float frontal_slippage) {
        this.frontal_slippage = frontal_slippage;
    }

    public float getSagital_slippage() {
        return sagital_slippage;
    }

    public void setSagital_slippage(float sagital_slippage) {
        this.sagital_slippage = sagital_slippage;
    }

    public long getPacket_number() {
        return packet_number;
    }

    public void setPacket_number(long packet_number) {
        this.packet_number = packet_number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        dateMillis = this.date.getTime();
        this.dateMillis = dateMillis;
    }

    public float getAx_9axis_dev1() {
         return ax_9axis_dev1;
    }

    public void setAx_9axis_dev1(float ax_9axis_dev1) {
        this.ax_9axis_dev1 = ax_9axis_dev1;
    }

    public float getAy_9axis_dev1() {
        return ay_9axis_dev1;
    }

    public void setAy_9axis_dev1(float ay_9axis_dev1) {
        this.ay_9axis_dev1 = ay_9axis_dev1;
    }

    public float getAz_9axis_dev1() {
        return az_9axis_dev1;
    }

    public void setAz_9axis_dev1(float az_9axis_dev1) {
        this.az_9axis_dev1 = az_9axis_dev1;
    }

    public float getGx_9axis_dev1() {
        return gx_9axis_dev1;
    }

    public void setGx_9axis_dev1(float gx_9axis_dev1) {
        this.gx_9axis_dev1 = gx_9axis_dev1;
    }

    public float getGy_9axis_dev1() {
        return gy_9axis_dev1;
    }

    public void setGy_9axis_dev1(float gy_9axis_dev1) {
        this.gy_9axis_dev1 = gy_9axis_dev1;
    }

    public float getGz_9axis_dev1() {
        return gz_9axis_dev1;
    }

    public void setGz_9axis_dev1(float gz_9axis_dev1) {
        this.gz_9axis_dev1 = gz_9axis_dev1;
    }

    public float getMx_9axis_dev1() {
        return mx_9axis_dev1;
    }

    public void setMx_9axis_dev1(float mx_9axis_dev1) {
        this.mx_9axis_dev1 = mx_9axis_dev1;
    }

    public float getMy_9axis_dev1() {
        return my_9axis_dev1;
    }

    public void setMy_9axis_dev1(float my_9axis_dev1) {
        this.my_9axis_dev1 = my_9axis_dev1;
    }

    public float getMz_9axis_dev1() {
        return mz_9axis_dev1;
    }

    public void setMz_9axis_dev1(float mz_9axis_dev1) {
        this.mz_9axis_dev1 = mz_9axis_dev1;
    }

    public float getAx_3axis_dev1() {
        return ax_3axis_dev1;
    }

    public void setAx_3axis_dev1(float ax_3axis_dev1) {
        this.ax_3axis_dev1 = ax_3axis_dev1;
    }

    public float getAy_3axis_dev1() {
        return ay_3axis_dev1;
    }

    public void setAy_3axis_dev1(float ay_3axis_dev1) {
        this.ay_3axis_dev1 = ay_3axis_dev1;
    }

    public float getAz_3axis_dev1() {
        return az_3axis_dev1;
    }

    public void setAz_3axis_dev1(float az_3axis_dev1) {
        this.az_3axis_dev1 = az_3axis_dev1;
    }

    public float getAx_9axis_dev2() {
        return ax_9axis_dev2;
    }

    public void setAx_9axis_dev2(float ax_9axis_dev2) {
        this.ax_9axis_dev2 = ax_9axis_dev2;
    }

    public float getAy_9axis_dev2() {
        return ay_9axis_dev2;
    }

    public void setAy_9axis_dev2(float ay_9axis_dev2) {
        this.ay_9axis_dev2 = ay_9axis_dev2;
    }

    public float getAz_9axis_dev2() {
        return az_9axis_dev2;
    }

    public void setAz_9axis_dev2(float az_9axis_dev2) {
        this.az_9axis_dev2 = az_9axis_dev2;
    }

    public float getGx_9axis_dev2() {
        return gx_9axis_dev2;
    }

    public void setGx_9axis_dev2(float gx_9axis_dev2) {
        this.gx_9axis_dev2 = gx_9axis_dev2;
    }

    public float getGy_9axis_dev2() {
        return gy_9axis_dev2;
    }

    public void setGy_9axis_dev2(float gy_9axis_dev2) {
        this.gy_9axis_dev2 = gy_9axis_dev2;
    }

    public float getGz_9axis_dev2() {
        return gz_9axis_dev2;
    }

    public void setGz_9axis_dev2(float gz_9axis_dev2) {
        this.gz_9axis_dev2 = gz_9axis_dev2;
    }

    public float getMx_9axis_dev2() {
        return mx_9axis_dev2;
    }

    public void setMx_9axis_dev2(float mx_9axis_dev2) {
        this.mx_9axis_dev2 = mx_9axis_dev2;
    }

    public float getMy_9axis_dev2() {
        return my_9axis_dev2;
    }

    public void setMy_9axis_dev2(float my_9axis_dev2) {
        this.my_9axis_dev2 = my_9axis_dev2;
    }

    public float getMz_9axis_dev2() {
        return mz_9axis_dev2;
    }

    public void setMz_9axis_dev2(float mz_9axis_dev2) {
        this.mz_9axis_dev2 = mz_9axis_dev2;
    }

    public float getAx_3axis_dev2() {
        return ax_3axis_dev2;
    }

    public void setAx_3axis_dev2(float ax_3axis_dev2) {
        this.ax_3axis_dev2 = ax_3axis_dev2;
    }

    public float getAy_3axis_dev2() {
        return ay_3axis_dev2;
    }

    public void setAy_3axis_dev2(float ay_3axis_dev2) {
        this.ay_3axis_dev2 = ay_3axis_dev2;
    }

    public float getAz_3axis_dev2() {
        return az_3axis_dev2;
    }

    public void setAz_3axis_dev2(float az_3axis_dev2) {
        this.az_3axis_dev2 = az_3axis_dev2;
    }
}
