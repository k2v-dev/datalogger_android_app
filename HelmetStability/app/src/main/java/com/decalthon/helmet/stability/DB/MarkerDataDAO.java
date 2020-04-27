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
    @Query("Update marker_data SET marker_timestamp = 1587043915773, lat = 12.9828758239746, lng = 77.4828796386719, marker_type = 8 where marker_num = 5")
    void update1();
    @Query("Update marker_data SET marker_timestamp = 1587043937103, lat = 12.9815673828125, lng = 77.4822235107422,  marker_type = 4  where marker_num = 7")
    void update2();
    @Query("Update marker_data SET marker_timestamp = 1587045965943, lat = 12.9890937805176, lng = 77.4639282226563, marker_type = 1   where marker_num = 9")
    void update3();
    @Query("Update marker_data SET marker_timestamp = 1587046162673, lat = 12.9866371154785, lng = 77.452621459961, marker_type = 8   where marker_num = 11")
    void update4();

}
