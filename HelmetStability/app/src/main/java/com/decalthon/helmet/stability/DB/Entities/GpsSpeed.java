package com.decalthon.helmet.stability.DB.Entities;

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


    @ColumnInfo(name="lon")
    public float longitude;

    public float getLongitude() {
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
    public float timestamp;

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
