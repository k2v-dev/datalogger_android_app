package com.decalthon.helmet.stability.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;

//This annotation is used by the database instance to define all CRUD operations
@Dao
public interface GpsSpeedDAO {
    @Insert
    public void insertSpeed(GpsSpeed gpsSpeed);

    @Update
    public void updateSpeed(GpsSpeed gpsSpeed);

    @Query("select * from gps_speed")
    public GpsSpeed queryGpsSpeed();

    @Delete
    public void deleteContact(GpsSpeed gpsSpeed);
}
