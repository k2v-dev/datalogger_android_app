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
import com.decalthon.helmet.stability.model.NineAxisModels.AccelerometerData;
import com.decalthon.helmet.stability.model.NineAxisModels.GyroscopeData;
import com.decalthon.helmet.stability.model.NineAxisModels.MagnetometerData;
import com.decalthon.helmet.stability.model.NineAxisModels.NineAxis;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;

import java.util.List;

@Dao
public interface SessionDataDao {

//    @Insert
//    public void insertSessionPacket(SensorDataEntity sensorDataEntity);

    //SensorData
    @Insert
    public void insertSessionPacket(SensorDataEntity[] sensorDataEntities);

    @Insert
    public void insertSessionPacket(SensorDataEntity sensorDataEntity);

    @Update
    public void updateSessionPacket(SensorDataEntity[] sensorDataEntity);

    @Query("select * from SensorDataEntity where session_id = (:session_id) order by packet_number")
    public List<SensorDataEntity> getSessionEntityPacket(long session_id);


    //ButtonBox
    @Insert
    public void insertButtonBoxPacket(ButtonBoxEntity[] buttonBoxEntities);

    @Insert
    public void insertButtonBoxPacket(ButtonBoxEntity buttonBoxEntity);

    @Update
    public void updateButtonBoxPackets(ButtonBoxEntity[] sensorDataEntities);

    @Update
    public void updateButtonBoxPacket(ButtonBoxEntity sensorDataEntity);

    @Query("delete from ButtonBoxEntity where session_id = (:session_id) and dateMillis > 1587120737990")
    public void deleteButtonBoxEntity(long session_id);

    @Query("select * from ButtonBoxEntity where session_id = (:session_id) order by dateMillis")
    public List<ButtonBoxEntity> getButtonBoxEntityPacket(long session_id);

    @Update
    public void updateSessionSummary(SessionSummary  sessionSummary);

    @Update
    public void updateSessionSummaries(SessionSummary [] sessionSummary);

    @Insert
    public void insertSessionSummary(SessionSummary []  sessionSummaries);

    @Delete
    public void deleteSessionPacket(GpsSpeed gpsSpeed);

    @Query( "select * from SessionSummary order by timestamp desc limit 7")
    public List<SessionSummary> getLastSevenSessionSummaries();

    @Query("select * from SessionSummary where timestamp in (:timestamps)")
    public List<SessionSummary> getSummaryList(Long... timestamps);

    @Query("select * from SessionSummary where timestamp = (:timestamp)")
    public SessionSummary getSessionSummary(Long timestamp);

    @Query("select * from SessionSummary where session_id = (:session_id)")
    public SessionSummary getSessionSummaryById(long session_id);

//    @Query("select  ax_9axis_dev1  from SensorDataEntity where session_id in (:session_ids)")
//    Float[] geHelmetAccelerometerData(Integer [] session_ids);

//    @Query("select dateMillis as timestamp,  ax_9axis_dev1 as xval,ay_9axis_dev1 as yval ,az_9axis_dev1  as zval  from SensorDataEntity where session_id in (:session_ids) ")
//    List<SensorDataEntry> getHelmetAccelerometerData(Long[] session_ids);

//    @Query("select dateMillis as timestamp,  ax_9axis_dev1 as xval from SensorDataEntity where session_id in (:session_ids) ")
//    List<SensorDataEntry> getHelmetAccelerometerData(Long[] session_ids);

    @Query("select  dateMillis as timestamp, gx_9axis_dev1 as xval,gy_9axis_dev1 as yval, gz_9axis_dev1 as zval from SensorDataEntity where session_id in (:session_ids) ")
    List<SensorDataEntry> getHelmetGyroscopeData(Long[] session_ids);

    @Query("select  dateMillis as timestamp, mx_9axis_dev1 as xval, my_9axis_dev1 as yval ,mz_9axis_dev1 as zval  from SensorDataEntity where session_id in (:session_ids) ")
    List<SensorDataEntry> getHelmetMagnetometerData(Long[] session_ids);

    @Query("select  dateMillis as timestamp, ax_9axis_dev1 as xval,ay_9axis_dev1 as yval ,az_9axis_dev1 as zval from SensorDataEntity where session_id in (:session_ids)")
    List<SensorDataEntry> getHelmetAccelerometerData(Long[] session_ids);

    @Query("select dateMillis from SensorDataEntity where session_id = (:session_id)")
    Long[] getTimestampsForSession(Long[] session_id);

    // Delete all
    @Query("delete from marker_data")
    void deleteAll();

    @Query("delete from SessionSummary where session_id = (:session_id)")
    void deleteSessionSummary(long session_id);

    @Query("delete from SensorDataEntity where session_id = (:session_id)")
    void deleteSensorData(long session_id);

    @Query("select MAX(packet_number) from SensorDataEntity where session_id = (:session_id)")
    long getLastPktNumSD(long session_id);

    @Query("select MAX(packet_number) from ButtonBoxEntity where session_id = (:session_id)")
    long getLastPktNumBB(long session_id);

    @Query("delete from ButtonBoxEntity where session_id = 4 and packet_number > 2100")
    void setSessionSummary();
    @Query("delete from ButtonBoxEntity where session_id = 2 and packet_number > 7200")
    void setSessionSummary1();

}
