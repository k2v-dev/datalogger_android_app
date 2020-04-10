package com.decalthon.helmet.stability.DB;


import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;

@Dao
public class SessionCDL {
    @Embedded
    public GpsSpeed gpsSpeed;

    @Relation(parentColumn = "marker_id", entityColumn ="marker_num")
    public MarkerData markerData;

}
