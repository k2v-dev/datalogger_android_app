package com.decalthon.helmet.stability.database;


import androidx.room.Dao;
import androidx.room.Embedded;

import com.decalthon.helmet.stability.database.entities.GpsSpeed;

@Dao
public class SessionCDL {
    @Embedded
    public GpsSpeed gpsSpeed;

//    @Relation(parentColumn = "marker_id", entityColumn ="marker_num")
//    public MarkerData markerData;

}
