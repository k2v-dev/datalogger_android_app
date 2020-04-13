package com.decalthon.helmet.stability.DB;

import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.BLE.ButtonBox_Parser;
import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.Fragments.HomeFragment;
import com.decalthon.helmet.stability.Utilities.Constants;

import java.io.BufferedReader;
import java.util.List;

public class DatabaseHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

//    public static void generateMarkerData()
    private static SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());

    public static class UpdateMarkerData extends  AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... longs) {
            List<MarkerData> markerDatas = sessionCdlDb.getMarkerDataDAO().getMarkerData(longs[0]);
            MarkerData[] markerDataArr = new MarkerData[markerDatas.size()];
            long t1 = System.currentTimeMillis();
            for(int i=0; i< markerDatas.size();i++){
                markerDataArr[i] = markerDatas.get(i);
                List<GpsSpeed> gpsSpeeds = sessionCdlDb.gpsSpeedDAO().getGpsSpeed(markerDataArr[i].marker_timestamp);
                markerDataArr[i].lat = gpsSpeeds.get(0).latitude;
                markerDataArr[i].lng = gpsSpeeds.get(0).longitude;
            }
            sessionCdlDb.getMarkerDataDAO().updateMarkerData(markerDataArr);
            long t2 = System.currentTimeMillis();
            System.out.println("UpdateMarkerData:: Total time = "+ (t2-t1));

            return null;
        }
    }

    public static class UpdateGPS extends  AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<GpsSpeed> gpsSpeeds = sessionCdlDb.gpsSpeedDAO().getGpsSpeed(0, 1596327229640l);
            sessionCdlDb.gpsSpeedDAO().updateSpeed();
            System.out.println("---UpdateGPS---");
            return null;
        }
    }

    private static class UpdateSensorData extends AsyncTask<Long,Void,Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
            List<SensorDataEntity> sensorDataEntityList = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket(1);
            long ts_ms = 1586326298640l;//1586268823340

            SensorDataEntity[] sensorDataEntities = new SensorDataEntity[sensorDataEntityList.size()];
            for (int i= 0; i< sensorDataEntityList.size(); i++){
                sensorDataEntities[i] = sensorDataEntityList.get(i);
                long ts_ms_i = ts_ms + i*10;
                sensorDataEntities[i].dateMillis = ts_ms_i;
                sensorDataEntities[i].ax_9axis_dev2 = sensorDataEntities[i].ax_9axis_dev1;
                sensorDataEntities[i].ay_9axis_dev2 = sensorDataEntities[i].ay_9axis_dev1;
                sensorDataEntities[i].az_9axis_dev2 = sensorDataEntities[i].az_9axis_dev1;

                sensorDataEntities[i].gx_9axis_dev2 = sensorDataEntities[i].gx_9axis_dev1;
                sensorDataEntities[i].gy_9axis_dev2 = sensorDataEntities[i].gy_9axis_dev1;
                sensorDataEntities[i].gz_9axis_dev2 = sensorDataEntities[i].gz_9axis_dev1;

                sensorDataEntities[i].mx_9axis_dev2 = sensorDataEntities[i].mx_9axis_dev1;
                sensorDataEntities[i].my_9axis_dev2 = sensorDataEntities[i].my_9axis_dev1;
                sensorDataEntities[i].mz_9axis_dev2 = sensorDataEntities[i].mz_9axis_dev1;
            }

            try {
                sessionCdlDb.getSessionDataDAO().insertSessionPacket(sensorDataEntities);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                if(sensorDataEntities != null && sensorDataEntities.length > 0){
                    Log.d(TAG, "packet #:"+sensorDataEntities[0].packet_number+", time="+sensorDataEntities[0].dateMillis);
                }
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class UpdateSessionSummary  extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
            SessionSummary sessionSummary = sessionCdlDb.getSessionDataDAO().getSessionSummary(1583740522230l);
            if(sessionSummary != null){
                sessionSummary.setDate(1586326298640l);
                SessionSummary[] sessionSummaries = new SessionSummary[1];
                sessionSummaries[0] = sessionSummary;
                sessionCdlDb.getSessionDataDAO().updateSessionSummaries(sessionSummaries);
            }
            return null;
        }
    }
    private static class DeleteSensorData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
            List<SensorDataEntity> sensorDataEntityList = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket(1);
            sessionCdlDb.getSessionDataDAO().deleteSessionPackets();
            return null;
        }
    }


    public static void insertGPS(){
        try{
            new DeleteAllGps().execute();
            String temp;
            java.io.InputStream iStream = MainActivity.shared().getApplicationContext().getAssets().open("ride4.txt");
            BufferedReader bufRead = new BufferedReader(new java.io.InputStreamReader(iStream));
            double latitude, longitude;
            long ts_ms = 1586326298640l;//1586268823340
            long i = 0;
            while ((temp = bufRead.readLine()) != null) {
                String[] coords = temp.split(",");
                latitude = Double.parseDouble(coords[0]);
                longitude = Double.parseDouble(coords[1]);
                GpsSpeed gpsSpeed = new GpsSpeed();
                gpsSpeed.latitude = (float)latitude;
                gpsSpeed.longitude = (float)longitude;
                gpsSpeed.timestamp = (ts_ms + i*1000);
                i++;
                new InsertGPSData().execute(gpsSpeed);
                //gpsSpeed.date = new Date((long)gpsSpeed.timestamp);
//            sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeed);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static class GetButtonEntities extends  AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //List<TestAcc> testAccs = sessionCdlDb.getSessionDataDAO().getSensorDataPackets(1);
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
                Integer[] marker_nums = new Integer[5];
                marker_nums[0] = 49;marker_nums[1] = 50;marker_nums[2] = 51; marker_nums[3] = 52;marker_nums[3] = 53;

                List<MarkerData>  markerDatas = sessionCdlDb.getMarkerDataDAO().getMarkerData(1);
                MarkerData[] markerDataArr = new MarkerData[markerDatas.size()];
//                int i = 0;
//                for(MarkerData markerData: markerDatas){
//                    markerData.session_id = 1;
//                    markerDataArr[i] = markerData;
//                    i++;
//                }
//                sessionCdlDb.getMarkerDataDAO().updateMarkerData(markerDataArr);
//                sessionCdlDb.getSessionDataDAO().deleteAll();
//                int prevBtnType = -1;
//                List<ButtonBoxEntity> buttonBoxEntities = sessionCdlDb.getSessionDataDAO().getButtonBoxEntityPacket(1);
//                for (ButtonBoxEntity buttonBoxEntity : buttonBoxEntities) {
//                    if(prevBtnType != buttonBoxEntity.button_type){
//                        prevBtnType = buttonBoxEntity.button_type;
//                        MarkerData markerData = new MarkerData(buttonBoxEntity.dateMillis, Constants.MARKER_MAPS.get(prevBtnType), "");
//                        markerData.session_id = 1;
//                        new InsertMarkerDataAsyncTask().execute(markerData);
//                    }
//                }
//                MarkerData markerData = new MarkerData(buttonBoxEntities.get(buttonBoxEntities.size()-1).dateMillis, Constants.MARKER_MAPS.get(255), "");
//                markerData.session_id = 1;
//                new InsertMarkerDataAsyncTask().execute(markerData);

            }catch (android.database.sqlite.SQLiteConstraintException e){
                e.printStackTrace();
            }
            return null;
        }
    }


    private static class InsertMarkerDataAsyncTask extends AsyncTask<MarkerData,Void,Void> {

        @Override
        protected Void doInBackground(MarkerData... markerData) {
            try {
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
                sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData[0]);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                e.printStackTrace();
            }
            return null;
        }
    }


    private static class InsertGPSData extends AsyncTask<GpsSpeed, Void, Void> {
        @Override
        protected Void doInBackground(GpsSpeed... gpsSpeeds) {
            String temp;
            try{
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
                sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeeds[0]);
//                java.io.InputStream iStream = MainActivity.shared().getApplicationContext().getAssets().open("ride4.txt");
//                BufferedReader bufRead = new BufferedReader(new java.io.InputStreamReader(iStream));
//                double latitude, longitude;
//                long ts_ms = 1586270357440l;//1586268823340
//                long i = 0;
//                while ((temp = bufRead.readLine()) != null) {
//                    String[] coords = temp.split(",");
//                    latitude = Double.parseDouble(coords[0]);
//                    longitude = Double.parseDouble(coords[1]);
//                    GpsSpeed gpsSpeed = new GpsSpeed();
//                    gpsSpeed.latitude = (float)latitude;
//                    gpsSpeed.longitude = (float)longitude;
//                    gpsSpeed.timestamp = (ts_ms + i*1000);
//                    i++;
//                    //gpsSpeed.date = new Date((long)gpsSpeed.timestamp);
//                    sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeed);
//
//                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }


    private static class DeleteAllGps extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... gpsSpeeds) {
            String temp;
            try{
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
                sessionCdlDb.gpsSpeedDAO().deleteAll();
//                java.io.InputStream iStream = MainActivity.shared().getApplicationContext().getAssets().open("ride4.txt");
//                BufferedReader bufRead = new BufferedReader(new java.io.InputStreamReader(iStream));
//                double latitude, longitude;
//                long ts_ms = 1586270357440l;//1586268823340
//                long i = 0;
//                while ((temp = bufRead.readLine()) != null) {
//                    String[] coords = temp.split(",");
//                    latitude = Double.parseDouble(coords[0]);
//                    longitude = Double.parseDouble(coords[1]);
//                    GpsSpeed gpsSpeed = new GpsSpeed();
//                    gpsSpeed.latitude = (float)latitude;
//                    gpsSpeed.longitude = (float)longitude;
//                    gpsSpeed.timestamp = (ts_ms + i*1000);
//                    i++;
//                    //gpsSpeed.date = new Date((long)gpsSpeed.timestamp);
//                    sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeed);
//
//                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }
    }

}