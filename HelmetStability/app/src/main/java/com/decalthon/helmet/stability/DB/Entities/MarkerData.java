package com.decalthon.helmet.stability.DB.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


//This class is reached when a new marker tag has been generated
@Entity(tableName = "marker_data")
public class MarkerData {
//    @ColumnInfo(name = "id")
//
//    @ForeignKey(entity = GpsSpeed.class, parentColumns = "id", childColumns = "id" )
//    public int id;

    @PrimaryKey( autoGenerate = true )
    @ColumnInfo(name = "marker_num")
    public int markerNumber;

    @ColumnInfo(name = "marker_timestamp")
    public long marker_timestamp;

    @ColumnInfo(name = "marker_type")
    public String markerType;

    @ColumnInfo(name = "note")
    public String note;

    public MarkerData(){

    }

    @Ignore
    public MarkerData(long marker_timestamp,
                      int markerNumber,
                      String markerType,
                      String note) {

        this.marker_timestamp = marker_timestamp;
        this.markerNumber = markerNumber;
        this.markerType = markerType;
        this.note = note;
    }

    public void setMarkerNumber(int markerNumber) {
        this.markerNumber = markerNumber;
    }

    public long getMarker_timestamp() {
        return marker_timestamp;
    }

    public String getMarkerType() {
        return markerType;
    }

    public String getNote() {
        return note;
    }

    public int getMarkerNumber() {
        return markerNumber;
    }

    public enum MarkerType {
        VISITED,NOT_VISITED;
    }
}
