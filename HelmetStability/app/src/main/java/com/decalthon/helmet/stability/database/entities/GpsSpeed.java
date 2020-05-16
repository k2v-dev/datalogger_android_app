package com.decalthon.helmet.stability.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

//This entity is created in the GPSSpeed DB.

@Entity(tableName = "gps_speed")
public class GpsSpeed {

    public GpsSpeed(){
//        System.out.println("GPS entity instantiated");
    }

    //Each @ColumnInfo annotation defines a column with default names

    //NOTE: Any change in schema has to be added as a Migration


    @ColumnInfo(name="marker_id")
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="lat")
    public float latitude;

    public float getLatitude() {
        return latitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setAccuracy_location(float accuracy_location) {
        this.accuracy_location = accuracy_location;
    }

    public float getAccuracy_speed() {
        return accuracy_speed;
    }

    public void setAccuracy_speed(float accuracy_speed) {
        this.accuracy_speed = accuracy_speed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getLocaleDate() {
        return localeDate;
    }

    public void setLocaleDate(Date localeDate) {
        this.localeDate = localeDate;
    }

    @ColumnInfo(name="lon")
    public float longitude;

    public float getLongitude() {
        return longitude;
    }

    public float altitude;

    public float getAltitude() {
        return longitude;
    }

    @ColumnInfo(name="gps_speed", defaultValue = "0")
    public float speed;

    public float getSpeed() {
        return speed;
    }

    @ColumnInfo(name="loc_acc")
    public float accuracy_location;

    @Ignore
    public float getAccuracy_location() {
        return accuracy_location;
    }

    @Ignore
    public float accuracy_speed;

    @ColumnInfo(name="gps_timestamp")
    public long timestamp;

    @ColumnInfo(name="date")
    public String date;


//    @ColumnInfo(name="test")
//    public String test;
//
//    @ColumnInfo(name="test12")
//    public String test12;



    @Ignore
    public Date localeDate;

    @Ignore
    public static String getHeader() {
        StringBuffer strBuffer = new StringBuffer("");
        strBuffer.append(String.format("%6s;%6s;%6s;%20s;%20s;\n","Lat","Lon","Speed","Accuracy_location","Accuracy_Speed"));
        return  strBuffer.toString();
    }

    @Ignore
    public String toString() {
        StringBuffer strBuffer = new StringBuffer("");
        strBuffer.append(String.format("%6.2f;", latitude));
        strBuffer.append(String.format("%6.2f;", longitude));
        strBuffer.append(String.format("%6.3f;", speed));
        strBuffer.append(String.format("%19.3f;",accuracy_location));
        strBuffer.append(String.format("%19.3f;",accuracy_speed));
        return strBuffer.toString();
    }
}
