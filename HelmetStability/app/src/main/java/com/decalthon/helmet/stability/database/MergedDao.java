package com.decalthon.helmet.stability.database;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface MergedDao {
    @Transaction
    @Query("Select * from gps_speed")
    List<SessionCDL> getGpsMarkerMerge();
}
