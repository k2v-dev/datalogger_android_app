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
    public String note = "";

    @ColumnInfo(name = "note_timestamp")
    public long note_timestamp;

    @ColumnInfo(name = "lng")
    public float lng;

    @ColumnInfo(name = "lat")
    public float lat;

    @ColumnInfo(name = "session_id")
    public long session_id;


    public MarkerData(){
        note = "";
        markerType = "";
    }

    @Ignore
    public MarkerData(long marker_timestamp,
                      String markerType,
                      String note, long session_id) {

        this.marker_timestamp = marker_timestamp;
//        this.markerNumber = markerNumber;
        this.markerType = markerType;
        this.note = note;
        this.session_id = session_id;
    }

    public void setMarkerNumber(int markerNumber) {
        this.markerNumber = markerNumber;
    }

    public long getMarker_timestamp() {
        return marker_timestamp;
    }

    public String getMarkerType() {
        if(markerType == null){
            return " ";
        }
        return markerType;
    }

    public String getNote() {
        if(note == null){
            return " ";
        }
        return note;
    }

    public int getMarkerNumber() {
        return markerNumber;
    }

    public enum MarkerType {
        VISITED,NOT_VISITED;
    }
}
