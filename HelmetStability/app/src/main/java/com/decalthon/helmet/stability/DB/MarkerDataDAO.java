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

    @Insert
    void insertMarkerData(MarkerData[] markerDatas);

    @Query("select note from marker_data where marker_num = (:marker_num) order by marker_timestamp")
    String getMarkerNote(Integer[] marker_num);

    @Query("select * from marker_data where session_id = (:session_id) order by marker_timestamp")
    List<MarkerData> getMarkerData(long session_id);

    @Update
    void updateMarkerData(MarkerData[] markerData);

    @Delete
    void removeMarkerData(MarkerData[] markerData);

    @Query("delete from marker_data where session_id = (:session_id)")
    void deleteAll(long session_id);

    @Query("delete from marker_data")
    void deleteAll();

    @Query("delete from marker_data where session_id = (:session_id) and marker_num > 12")
    void deleteByMarkerNum(long session_id);

    // Below are fake data
    @Query("Update marker_data SET marker_timestamp = 1583740522233, lat = 12.8999248369141, lng = 77.4999616259766, marker_type = 64   where marker_num = 271")
    void update0();
    @Query("Update marker_data SET marker_timestamp = 1583740550820, lat = 12.9053417070312, lng = 77.4852521533203, marker_type = 8 where marker_num = 5")
    void update1();
    @Query("Update marker_data SET marker_timestamp = 1583740584580, lat = 12.905368409912, lng = 77.4806745166016,  marker_type = 4  where marker_num = 7")
    void update2();
    @Query("Update marker_data SET marker_timestamp = 1583740634700, lat = 12.905353151123, lng = 77.4805219287109, marker_type = 1   where marker_num = 9")
    void update3();
    @Query("Update marker_data SET marker_timestamp = 1583740670360, lat = 12.9040294511719, lng = 77.4719083422852, marker_type = 8   where marker_num = 11")
    void update4();
    @Query("Update marker_data SET marker_timestamp = 1583740836073, lat = 12.9028507097168, lng = 77.4687726611328, marker_type = 128   where marker_num = 272")
    void update5();

}
