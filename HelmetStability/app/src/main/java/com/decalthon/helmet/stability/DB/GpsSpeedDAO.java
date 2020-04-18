package com.decalthon.helmet.stability.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;

import java.util.List;

//This annotation is used by the database instance to define all CRUD operations
@Dao
public interface GpsSpeedDAO {
    @Insert
    public void insertSpeed(GpsSpeed gpsSpeed);

    @Update
    public void updateSpeed(GpsSpeed gpsSpeed);

    @Query("select * from gps_speed where gps_timestamp > (:start_ts) AND gps_timestamp < (:end_ts) order by gps_timestamp")
    public List<GpsSpeed> getGpsSpeed(long start_ts, long end_ts);

    @Query("select * from gps_speed order by ABS(gps_timestamp - (:ts)) limit 1")
    public List<GpsSpeed> getGpsSpeed(long ts);

    @Query("Update gps_speed SET gps_speed = abs(random() % 20)*0.1 + 3, lat = lat +13.0 + 12.9, lon = lon - 231.4 + 77.5, altitude = abs(random() % 10) + 895.0")
    public void updateSpeed();

    @Delete
    public void deletegGPS(GpsSpeed gpsSpeed);

    @Query("delete from gps_speed")
    public void deleteAll();


}
