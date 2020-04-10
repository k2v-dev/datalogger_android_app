package com.decalthon.helmet.stability.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

import java.util.List;

@Dao
public interface SessionDataDao {

//    @Insert
//    public void insertSessionPacket(SensorDataEntity sensorDataEntity);

    //SensorData
    @Insert
    public void insertSessionPacket(SensorDataEntity [] sensorDataEntity);

    @Update
    public void updateSessionPacket(SensorDataEntity sensorDataEntity);

    @Query("select * from SensorDataEntity")
    public SensorDataEntity getSessionEntityPacket();

    //ButtonBox
    @Insert
    public void insertButtonBoxPacket(ButtonBoxEntity[] sensorDataEntity);

    @Update
    public void updateButtonBoxPacket(ButtonBoxEntity sensorDataEntity);

    @Query("select * from ButtonBoxEntity")
    public ButtonBoxEntity getButtonBoxEntityPacket();

    @Update
    public void updateSessionSummary(SessionSummary [] sessionSummary);

    @Insert
    public void insertSessionSummary(SessionSummary []  sessionSummaries);

    @Delete
    public void deleteSessionPacket(GpsSpeed gpsSpeed);

    @Query("select * from SessionSummary where timestamp in (:timestamps)")
    public List<SessionSummary> getSummaryList(Long... timestamps);

    @Query("select * from SessionSummary where timestamp = (:timestamp)")
    public SessionSummary getSessionSummary(Long timestamp);

    @Query("select * from SessionSummary where session_id = (:session_id)")
    public SessionSummary getSessionSummaryById(int session_id);

}
