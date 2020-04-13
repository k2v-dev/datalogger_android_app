package com.decalthon.helmet.stability.model.NineAxisModels;

import android.graphics.drawable.Drawable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

import com.github.mikephil.charting.data.Entry;

public class SensorDataEntry {

    @ColumnInfo(name = "timestamp")
    private long time;
    private float xval;
    private float yval;
    private float zval;

    @Ignore
    private Entry xentry, yentry, zentry;

    public SensorDataEntry(){

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Entry getXentry() {
        return xentry;
    }

    public void setXentry(Entry xentry) {
        this.xentry = xentry;
    }

    public Entry getYentry() {
        return yentry;
    }

    public void setYentry(Entry yentry) {
        this.yentry = yentry;
    }

    public Entry getZentry() {
        return zentry;
    }

    public void setZentry(Entry zentry) {
        this.zentry = zentry;
    }

    public float getXval() {
        return xval;
    }

    public void setXval(float xval) {
        this.xval = xval;
    }

    public float getYval() {
        return yval;
    }

    public void setYval(float yval) {
        this.yval = yval;
    }

    public float getZval() {
        return zval;
    }

    public void setZval(float zval) {
        this.zval = zval;
    }
}
