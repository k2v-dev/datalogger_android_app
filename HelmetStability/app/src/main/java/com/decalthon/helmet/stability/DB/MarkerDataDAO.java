package com.decalthon.helmet.stability.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.decalthon.helmet.stability.DB.Entities.MarkerData;

@Dao
public interface MarkerDataDAO {

    @Insert
    void insertMarkerData(MarkerData markerData);

    @Query("select note from marker_data where marker_num = (:marker_num)")
    String getMarkerNote(Integer[] marker_num);

    @Update
    void updateMarkerData(MarkerData[] markerData);

    @Delete
    void removeMarkerData(MarkerData[] markerData);
}
