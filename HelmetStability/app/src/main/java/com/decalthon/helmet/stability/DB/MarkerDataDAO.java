package com.decalthon.helmet.stability.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.decalthon.helmet.stability.DB.Entities.MarkerData;

import java.util.List;

@Dao
public interface MarkerDataDAO {

    @Insert
    void insertMarkerData(MarkerData markerData);

    @Query("select note from marker_data where marker_num = (:marker_num) order by marker_timestamp")
    String getMarkerNote(Integer[] marker_num);

    @Query("select * from marker_data where session_id = (:session_id)")
    List<MarkerData> getMarkerData(long session_id);

    @Update
    void updateMarkerData(MarkerData[] markerData);

    @Delete
    void removeMarkerData(MarkerData[] markerData);

    @Query("delete from marker_data")
    void deleteAll();
}
