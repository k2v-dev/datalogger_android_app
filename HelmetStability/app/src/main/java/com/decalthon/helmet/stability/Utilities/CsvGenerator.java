package com.decalthon.helmet.stability.Utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.Fragments.GPSSpeedFragment;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CsvGenerator {
    private static final String TAG = CsvGenerator.class.getSimpleName();
    private static final String LOG_DIR = "LOG_FILES";
    String dateFilePattern = "yyyyMMdd_HHmmss";
    SimpleDateFormat dateFileFormat = new SimpleDateFormat(dateFilePattern);
    SessionCdlDb sessionCdlDb ;
    SessionSummary sessionSummary;
    List<SensorDataEntity> sensorDataEntities;
    List<ButtonBoxEntity> buttonBoxEntities;
    List<GpsSpeed> gpsSpeeds;
    List<MarkerData> markerDatas;
    ProfileReq profileReq;
    String userId = "";
    String prefix = "";
    File roo_dir;
    ProfilePreferences profilePreferences;
    Map<String, String> genderMap = new HashMap<>();
    String PATH = null;
    public CsvGenerator(Context context) {
        sessionCdlDb = SessionCdlDb.getInstance(context);
        userId = UserPreferences.getInstance(context).getUserID();
        profilePreferences = ProfilePreferences.getInstance(context);
        genderMap.put("M", "Male");
        genderMap.put("F", "Female");
        genderMap.put("OTHER", "Other");
        PATH = context.getPackageName() + File.separator + LOG_DIR;
    }

    public void generateCSV(int session_id){
        roo_dir = FileUtilities.createDirIfNotExists(PATH);
        if (roo_dir==null)
        {
            return;
        }
        new loadData().execute((long)session_id);
    }

    public void generateSensorDataFile(){

        String data = "UserID__,Session_Name__,HourMinutesSecondsMilliseconds__,Data_packet_number_after_synchronization__Count,Device1_x_axis_accelerometer_of_9axis_IMU__m_per_sec2," +
                "Device1_y_axis_accelerometer_of_9axis_IMUm_per_sec2,Device1_z_axis_accelerometer_of_9axis_IMUm_per_sec2,Device1_x_axis_gyroscope_of_9axis_IMU__deg_per_sec," +
                "Device1_y_axis_gyroscope_of_9axis_IMU__deg_per_sec,Device1_z_axis_gyroscope_of_9axis_IMU__deg_per_sec,Device1_x_axis_magnetometer_of_9axis_IMU__mGauss," +
                "Device1_y_axis_magnetometer_of_9axis_IMU__mGauss,Device1_z_axis_magnetometer_of_9axis_IMU__mGauss,Device1_x_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2," +
                "Device1_y_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,Device1_z_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,Device2_x_axis_accelerometer_of_9axis_IMU__m_per_sec2," +
                "Device2_y_axis_accelerometer_of_9axis_IMU__m_per_sec2,Device2_z_axis_accelerometer_of_9axis_IMU__m_per_sec2,Device2_x_axis_gyroscope_of_9axis_IMU__deg_per_sec," +
                "Device2_y_axis_gyroscope_of_9axis_IMU__deg_per_sec,Device2_z_axis_gyroscope_of_9axis_IMU__deg_per_sec,Device2_x_axis_magnetometer_of_9axis_IMU__mGauss," +
                "Device2_y_axis_magnetometer_of_9axis_IMU__mGauss,Device2_z_axis_magnetometer_of_9axis_IMU__mGauss,Device2_x_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2," +
                "Device2_y_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,Device2_z_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,Latitude_as_obtained_from smartphones_GPS__deg,Longitude_as_obtained_from smartphones_GPS__deg," +
                "Altitude_as obtained_from_smartphones_GPS__m,Speed_as_calculated_using_smartphones_GPS_data__km_per_hr,Users_marker_number_upto_255__Count,Users_sensory_input_upto_255__Count," +
                "Users_marker_NOTE__,ButtonBox_x_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,ButtonBox_y_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2," +
                "ButtonBox_z_axis_accelerometer_of_3axis_100g_IMU__m_per_sec2,Terrain_information_obtained_from_3axis_IMU_data_of_ButtonBox__m_per_sec2,Helmets_sagittal_slippage_angle__deg,Helmets_frontal_slippage_angle__deg\n";

        StringBuilder strBuilder = new StringBuilder(data);

       try{

           String fileName = prefix + "_sensor_data.csv";

           File logFile = new File(roo_dir, fileName);
           FileWriter writer = new FileWriter(logFile,true);

           writer.append(addSeparator("User_Profile, "));writer.append(addComma());
           writer.append("User Id,"+userId);writer.append(addComma());
           writer.append("Gender,"+genderMap.get(profilePreferences.getGender()+""));writer.append(addComma());
           writer.append("Height_cm,"+(int)profilePreferences.getHeight());writer.append(addComma());
           writer.append("Weight_kg,"+(int)profilePreferences.getWeight());writer.append(addComma());
           writer.append("Age_yrs,"+profilePreferences.getAge());writer.append(addComma());

           writer.append(addSeparator("Session_Summary, "));writer.append(addComma());
           Date date = new Date(sessionSummary.getDate());
           String date_str = dateFileFormat.format(date);
           float total_MB = ((float)sessionSummary.getTotal_data())/(1024.0f*1024.0f);
           writer.append("Session_Name,"+sessionSummary.getName());writer.append(addComma());
           writer.append("Session_Start_Time,"+date_str);writer.append(addComma());
           writer.append("Duration,"+getDurationInString((long)sessionSummary.getDuration()));writer.append(addComma());
           writer.append("Activity_type,"+sessionSummary.getActivity_type());writer.append(addComma());
           writer.append("Sampling_rate_Hz,100");writer.append(addComma());
           writer.append("Raw_data_size_MB,"+String.format(Locale.getDefault(),"%3.2f", total_MB));writer.append(addComma());
           writer.append("Number_of_Columns,38");writer.append(addComma());
           writer.append("Additional_Note,\""+sessionSummary.getNote()+"\"");writer.append(addComma());
//           writer.append("\n\n\n");

           writer.append(addSeparator("Sensors_Data"));
           writer.append(strBuilder.toString());
           writer.flush();
           GpsSpeed gpsSpeed = new GpsSpeed();
           ButtonBoxEntity buttonBoxEntity = buttonBoxEntities.remove(0);
           int count = 1;
           float time_diff =  buttonBoxEntities.get(0).dateMillis - sensorDataEntities.get(0).dateMillis;
           boolean firstTime = true;
           for(int i=0; i< sensorDataEntities.size(); i++){
               strBuilder.delete(0, strBuilder.length());
               SensorDataEntity sensorDataEntity = sensorDataEntities.get(i);
               if(firstTime && (buttonBoxEntity.dateMillis < sensorDataEntity.dateMillis )){
                   Log.d(TAG, "..continue");
                   continue;
               }
               firstTime = false;
               if(buttonBoxEntities.size() > 0 ){
                   ButtonBoxEntity nxtbuttonBoxEntity = buttonBoxEntities.get(0);
                   if((Math.abs(nxtbuttonBoxEntity.dateMillis-sensorDataEntity.dateMillis) <  15)){
                       buttonBoxEntity = buttonBoxEntities.remove(0);
                       //Log.d(TAG, "remove BBox = "+buttonBoxEntity.dateMillis);
                   }
               }
               strBuilder.append(userId).append(",").append(sessionSummary.getName()).append(",");
               appendSensorDataEntity(strBuilder, sensorDataEntity, count);
               // Criterial for get next GpsSpeed's object
               if(gpsSpeeds.size() > 0 ){
                   GpsSpeed nextGpsSpeed = gpsSpeeds.get(0);
                   if((Math.abs(gpsSpeed.timestamp-sensorDataEntity.dateMillis) > 500) && (Math.abs(nextGpsSpeed.timestamp-sensorDataEntity.dateMillis) < 1000)){
                       gpsSpeed = gpsSpeeds.remove(0);
                   }
               }
               appendGpsSpeed(strBuilder, gpsSpeed);

               MarkerData markerData = new MarkerData();
               if(markerDatas.size() > 0 ){
                   MarkerData nxtMarkerData = markerDatas.get(0);
                   if((Math.abs(buttonBoxEntity.dateMillis - nxtMarkerData.marker_timestamp) < 500)){
                       markerData = markerDatas.remove(0);
                   }
               }
               appendMarkerData(strBuilder, markerData);
               appendButtonBoxEntity(strBuilder, buttonBoxEntity);
               Helper.format3(strBuilder, sensorDataEntity.sagital_slippage);strBuilder.append(",");
               Helper.format3(strBuilder, sensorDataEntity.frontal_slippage);strBuilder.append("\n");
               writer.append(strBuilder);
               if(i%1000==0){
                   writer.flush();
                   System.out.println("i=="+i);
               }
               count++;
           }

           writer.flush();
           writer.close();
       }catch (Exception ex){
            ex.printStackTrace();
       }
        Log.d(TAG, "File logging is completed");
    }



    public void generateSessionSummaryFile(SessionSummary sessionSummary){
        try{
            String fileName = prefix + "_session_summary.csv";

            File logFile = new File(roo_dir, fileName);
            String header = "UserID__,Session_Name__,Session_Start_Time__YYYYMMDD_HHMMSS,Duration__HHMMSSMIL,Sampling_rate__Hz,Raw_data_size__MB,Number_of_Columns__Count,Additional_Note__Text\n";

            FileWriter writer = new FileWriter(logFile,true);
            Date date = new Date(sessionSummary.getDate());
            String date_str = dateFileFormat.format(date);
            float total_MB = ((float)sessionSummary.getTotal_data())/(1024.0f*1024.0f);

            String data = userId+","+sessionSummary.getName()+","+date_str+","+getDurationHHMMSSMil((long)sessionSummary.getDuration())+","+"100,"+String.format(Locale.getDefault(),"%3.2f",total_MB)+",38,"+sessionSummary.getNote();
            writer.append(header);
            writer.append(data);
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void generateUserFile(){
        try {
            String fileName = prefix + "_user_data.csv";

            File logFile = new File(roo_dir, fileName);

            String header = "UserID__,Gender__,Height__cm,Weight__kg,Age__yrs\n";

            String data = userId+","+genderMap.get(profilePreferences.getGender()+"")+","+(int)profilePreferences.getHeight()+","+(int)profilePreferences.getWeight()+","+profilePreferences.getAge();

            FileWriter writer = new FileWriter(logFile,true);

            writer.append(header);
            writer.append(data);
            writer.flush();
            writer.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private class loadData extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            long session_id =  longs[0];
            sessionSummary = sessionCdlDb.getSessionDataDAO().getSessionSummaryById(session_id);

            sensorDataEntities = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket(session_id);
            buttonBoxEntities = sessionCdlDb.getSessionDataDAO().getButtonBoxEntityPacket(session_id);

            if(sessionSummary.getDuration() < 1){
                long end_ts = sensorDataEntities.get(sensorDataEntities.size()-1).dateMillis;
                sessionSummary.setDuration(end_ts - sessionSummary.getDate());
                sessionCdlDb.getSessionDataDAO().updateSessionSummary(sessionSummary);
            }
            // For testing purpose
            if(sessionSummary.getName() == null || sessionSummary.getName().length()==0){
                sessionSummary.setName("Cycling_Outdoor");
            }
            gpsSpeeds = sessionCdlDb.gpsSpeedDAO().getGpsSpeed(sensorDataEntities.get(0).dateMillis-500, sensorDataEntities.get(sensorDataEntities.size()-1).dateMillis+500);
            markerDatas = sessionCdlDb.getMarkerDataDAO().getMarkerData(session_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Date date = new Date(sessionSummary.getDate());
            prefix = userId+"_"+dateFileFormat.format(date)+"_";
//            new GenerateFile().execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    generateUserFile();
                    generateSessionSummaryFile(sessionSummary);
                    generateSensorDataFile();
                }
            }).start();
        }
    }

//    private class GenerateFile extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//    }

    private String addComma(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 38; i++) {
            stringBuilder.append(", ");
        }
        return stringBuilder.toString()+"\n";
    }

    private String addSeparator(String title){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            stringBuilder.append("_");
        }
        stringBuilder.append(" ").append(title);
        return stringBuilder.toString()+"\n";
    }

    private void appendSensorDataEntity(StringBuilder sb, SensorDataEntity sensorDataEntity, int count){
        sb.append(getTimestampInStr(sensorDataEntity.dateMillis)).append(",");
        sb.append(count).append(",");
        Helper.format3(sb, sensorDataEntity.ax_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.ay_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.az_9axis_dev1);sb.append(",");
        Helper.format3(sb, sensorDataEntity.gx_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.gy_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.gz_9axis_dev1);sb.append(",");
        Helper.format3(sb, sensorDataEntity.mx_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.my_9axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.mz_9axis_dev1);sb.append(",");
        Helper.format3(sb, sensorDataEntity.ax_3axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.ay_3axis_dev1);sb.append(",");Helper.format3(sb, sensorDataEntity.az_3axis_dev1);sb.append(",");
        Helper.format3(sb, sensorDataEntity.ax_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.ay_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.az_9axis_dev2);sb.append(",");
        Helper.format3(sb, sensorDataEntity.gx_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.gy_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.gz_9axis_dev2);sb.append(",");
        Helper.format3(sb, sensorDataEntity.mx_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.my_9axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.mz_9axis_dev2);sb.append(",");
        Helper.format3(sb, sensorDataEntity.ax_3axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.ay_3axis_dev2);sb.append(",");Helper.format3(sb, sensorDataEntity.az_3axis_dev2);sb.append(",");
    }

    private void appendGpsSpeed(StringBuilder sb, GpsSpeed gpsSpeed){
        Helper.format6(sb,gpsSpeed.latitude);sb.append(",");Helper.format6(sb,gpsSpeed.longitude);sb.append(",");
        Helper.format3(sb,gpsSpeed.altitude);sb.append(",");Helper.format3(sb,gpsSpeed.speed);sb.append(",");
    }

    private void appendMarkerData(StringBuilder sb, MarkerData markerData){
        Helper.format6(sb, markerData.markerNumber);sb.append(",");sb.append(markerData.getMarkerType());sb.append(",\"");
        sb.append(markerData.getNote());sb.append("\",");
    }

    private void appendButtonBoxEntity(StringBuilder sb, ButtonBoxEntity buttonBoxEntity){
        float terrain_info = (buttonBoxEntity.ax_3axis*buttonBoxEntity.ax_3axis) + (buttonBoxEntity.ay_3axis*buttonBoxEntity.ay_3axis) + (buttonBoxEntity.az_3axis*buttonBoxEntity.az_3axis);
        Helper.format3(sb, buttonBoxEntity.ax_3axis);sb.append(",");Helper.format3(sb, buttonBoxEntity.ay_3axis);sb.append(",");Helper.format3(sb, buttonBoxEntity.az_3axis);sb.append(",");
        Helper.format3(sb, terrain_info);sb.append(",");
    }

    private String getTimestampInStr(long duration){
        int day = (int)TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) - (day *24);
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - (TimeUnit.MILLISECONDS.toHours(duration)* 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) *60);
        long ms = TimeUnit.MILLISECONDS.toMillis(duration) - (TimeUnit.MILLISECONDS.toSeconds(duration) *1000);;
        return hours+""+minute+""+second+""+ms;
    }

    private String getDurationInString(long duration){
        long hours = TimeUnit.MILLISECONDS.toHours(duration) ;
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - (TimeUnit.MILLISECONDS.toHours(duration)* 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) *60);
        long ms = TimeUnit.MILLISECONDS.toMillis(duration) - (TimeUnit.MILLISECONDS.toSeconds(duration) *1000);;
        return hours+"hr_"+minute+"min_"+second+"sec_"+ms+"millisec";
    }

    private String getDurationHHMMSSMil(long duration){
        long hours = TimeUnit.MILLISECONDS.toHours(duration) ;
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - (TimeUnit.MILLISECONDS.toHours(duration)* 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) *60);
        long ms = TimeUnit.MILLISECONDS.toMillis(duration) - (TimeUnit.MILLISECONDS.toSeconds(duration) *1000);
        return String.format(Locale.getDefault(), "%02d%02d%02d%03d", hours, minute, second, ms);
    }

}
