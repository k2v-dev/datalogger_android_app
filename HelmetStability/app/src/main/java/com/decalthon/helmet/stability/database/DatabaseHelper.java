package com.decalthon.helmet.stability.database;

import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.database.entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.database.entities.GpsSpeed;
import com.decalthon.helmet.stability.database.entities.MarkerData;
import com.decalthon.helmet.stability.database.entities.SensorDataEntity;
import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.MainApplication;
import com.decalthon.helmet.stability.preferences.CollectiveSummaryPreference;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.preferences.CsvPreference;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.workmanager.WorkMgrHelper;

import java.io.BufferedReader;
import java.util.List;
import java.util.Set;

public class DatabaseHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();

//    public static void generateMarkerData()
    private static SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();

    public static class UpdateMarkerData extends  AsyncTask<Long, Void, Void> {
        @Override
        protected Void doInBackground(Long... longs) {
            try{
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
            }catch (Exception ex){
                ex.printStackTrace();
            }


            return null;
        }
    }

    public static class CheckForCsvGeneration extends AsyncTask<Long, Void, Void>{

        @Override
        protected Void doInBackground(Long... longs) {
            SessionSummary sessionSummary = sessionCdlDb.getSessionDataDAO().getSessionSummaryById(longs[0]);
            if(sessionSummary == null) return null;

            if(sessionSummary.isBb_isComplete() && sessionSummary.isComplete()){
                Constants.isNewSessionAdded = true;
                Constants.isUpdateCollectiveSummary = true;
                //Todo: Update
               new CollectiveSummaryPreference(MainApplication.getAppContext()).updateAndUpload(sessionSummary);
                CsvPreference.getInstance(MainApplication.getAppContext()).addSessionId(longs[0]);
                new WorkMgrHelper(MainApplication.getAppContext()).oneTimeCSVGenerationRequest();
            }
            return null;
        }
    }

    public static class SetSessionSummary extends  AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            sessionCdlDb.getSessionDataDAO().setSessionSummary();
            sessionCdlDb.getSessionDataDAO().setSessionSummary1();
            return null;
        }
    }

    public static class InsertButtonBoxEntityAsyncTask extends AsyncTask<Boolean,Void,Long> {

        @Override
        protected Long doInBackground(Boolean... booleans) {
            Common.wait(4000);
            if(booleans[0]){
                return 100L;
            }
            return -1L;
        }
    }


//    public static class GetLastPktNum extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            long prev_pkt_num = sessionCdlDb.getSessionDataDAO().getLastPktNumSD(8);
//            Log.d(TAG, "Prev Packet num = "+prev_pkt_num);
//            return null;
//        }
//    }

    public static class DeleteAll extends  AsyncTask<Long, Void, Void>{

        @Override
        protected Void doInBackground(Long... longs) {
            //sessionCdlDb.getSessionDataDAO().deleteSessionSummary(longs[0]);
            sessionCdlDb.getSessionDataDAO().deleteSensorData(longs[0]);
            return null;
        }
    }

    public static class DeleteOldSessions extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            List<SessionSummary> sessionSummaries = sessionCdlDb.getSessionDataDAO().getOlderSessionSummary();
            Set<Long> session_ids = CsvPreference.getInstance(MainApplication.getAppContext()).getCsvGeneratedSessionIds();
            for (SessionSummary sessionSummary:
                    sessionSummaries) {
                long session_id = (long)sessionSummary.getSession_id();
                if(session_ids.contains(session_id)){
                    new DeleteSession().execute(session_id);
                }
            }
            return null;
        }
    }

    public static class DeleteSession extends  AsyncTask<Long, Void, Void>{
        @Override
        protected Void doInBackground(Long... longs) {
            //sessionCdlDb.getSessionDataDAO().deleteSessionSummary(longs[0]);
            sessionCdlDb.getSessionDataDAO().deleteSensorData(longs[0]);
            sessionCdlDb.getSessionDataDAO().deleteAllBBoxEntity(longs[0]);
            List<MarkerData> markerData = sessionCdlDb.getMarkerDataDAO().getMarkerData(longs[0]);
            sessionCdlDb.gpsSpeedDAO().deleteGpsSpeed(markerData.get(0).marker_timestamp, markerData.get(markerData.size()-1).marker_timestamp);
            sessionCdlDb.getMarkerDataDAO().deleteAll(longs[0]);
            sessionCdlDb.getSessionDataDAO().deleteSessionSummary(longs[0]);
            CsvPreference.getInstance(MainApplication.getAppContext()).removeCsvGeneratedSessionId(longs[0]);
            return null;
        }
    }

    public static class UpdateGPS extends  AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<GpsSpeed> gpsSpeeds = sessionCdlDb.gpsSpeedDAO().getGpsSpeed(0, 1587046482100l);
            sessionCdlDb.gpsSpeedDAO().updateSpeed();
            System.out.println("---UpdateGPS---");
            return null;
        }
    }

    public static class UpdateSensorData extends AsyncTask<Long,Void,Void> {
        long session_id = 0;
        @Override
        protected Void doInBackground(Long... longs) {
            session_id = longs[0];
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
            List<SensorDataEntity> sensorDataEntityList = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket(longs[0]);
            SensorDataEntity[] sensorDataEntities = new SensorDataEntity[sensorDataEntityList.size()];

            List<ButtonBoxEntity> buttonBoxEntities = sessionCdlDb.getSessionDataDAO().getButtonBoxEntityPacket(longs[0]);
            ButtonBoxEntity[] buttonBoxEntitiesArr = new ButtonBoxEntity[buttonBoxEntities.size()];
            long ts_ms = 1583740522233l;//1587043905100l;
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

                if(i < buttonBoxEntities.size()){
//                    sensorDataEntities[i].dateMillis = buttonBoxEntities.get(i).dateMillis - 2;
                    buttonBoxEntitiesArr[i] = buttonBoxEntities.get(i);
                    buttonBoxEntitiesArr[i].dateMillis = ts_ms_i - 3;
                    //long ts_ms_i = buttonBoxEntitiesArr[i].dateMillis+3;
//                    buttonBoxEntitiesArr[i].dateMillis = ts_ms_i;
                    buttonBoxEntitiesArr[i].ax_3axis = buttonBoxEntitiesArr[i].az_3axis;
                }else{
                    break;
                }
//                sessionCdlDb.getSessionDataDAO().insertSessionPacket(sensorDataEntities[i]);
            }

            try {
                sessionCdlDb.getSessionDataDAO().insertSessionPacket(sensorDataEntities);
//                sessionCdlDb.getSessionDataDAO().updateSessionPacket(sensorDataEntities);
                sessionCdlDb.getSessionDataDAO().insertButtonBoxPacket(buttonBoxEntitiesArr);
                Common.wait(100);
//                sessionCdlDb.getSessionDataDAO().deleteSensorData_test(longs[0]);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                if(sensorDataEntities != null && sensorDataEntities.length > 0){
                    Log.d(TAG, "packet #:"+sensorDataEntities[0].packet_number+", time="+sensorDataEntities[0].dateMillis);
                }
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "Update is done..now move to insertGPS()");
            insertGPS(session_id);
        }
    }




    private static class UpdateSessionSummary  extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
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

    public static class DeleteButtonBox extends AsyncTask<Long, Void, Void> {
        long session_id;
        @Override
        protected Void doInBackground(Long... longs) {
            session_id = longs[0];
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
            sessionCdlDb.getSessionDataDAO().deleteButtonBoxEntity(session_id);
            sessionCdlDb.getSessionDataDAO().deleteSensorData_test(session_id);
            sessionCdlDb.getMarkerDataDAO().update0();sessionCdlDb.getMarkerDataDAO().update1();sessionCdlDb.getMarkerDataDAO().update2();
            sessionCdlDb.getMarkerDataDAO().update3();sessionCdlDb.getMarkerDataDAO().update4();sessionCdlDb.getMarkerDataDAO().update5();
            List<MarkerData> listMarkers = sessionCdlDb.getMarkerDataDAO().getMarkerData(session_id);
//            sessionCdlDb.getMarkerDataDAO().deleteAll(longs[0]);
//            sessionCdlDb.getMarkerDataDAO().deleteAll(0);
//            MarkerData markerData = new MarkerData(1587811320000l, "64", "", 3l);
//            sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            insertGPS(session_id);
        }
    }


    public static void insertGPS(long session_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    new DeleteAllGps().execute();
                    String temp;
                    SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
                    List<ButtonBoxEntity> buttonBoxEntities = sessionCdlDb.getSessionDataDAO().getButtonBoxEntityPacket(session_id);
                    java.io.InputStream iStream = MainActivity.shared().getApplicationContext().getAssets().open("ride2.txt");
                    BufferedReader bufRead = new BufferedReader(new java.io.InputStreamReader(iStream));
                    double latitude, longitude;
                    //long ts_ms = 1583740522233l;//1586268823340
                    long i = 0;
                    while ((temp = bufRead.readLine()) != null) {
                        String[] coords = temp.split(",");
                        latitude = Double.parseDouble(coords[0]);
                        longitude = Double.parseDouble(coords[1]);
                        GpsSpeed gpsSpeed = new GpsSpeed();
                        gpsSpeed.latitude = (float)latitude;
                        gpsSpeed.longitude = (float)longitude;
                        //gpsSpeed.timestamp = (ts_ms + i*1000);
                        if(i*100 < buttonBoxEntities.size()){
                            gpsSpeed.timestamp = buttonBoxEntities.get((int)i*100).dateMillis;
                        }else{
                            break;
                        }
                        i++;
                        new InsertGPSData().execute(gpsSpeed);
                        //gpsSpeed.date = new Date((long)gpsSpeed.timestamp);
//            sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeed);

                    }
                    sessionCdlDb.gpsSpeedDAO().updateSpeed();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();

    }

    private static class GetButtonEntities extends  AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //List<TestAcc> testAccs = sessionCdlDb.getSessionDataDAO().getSensorDataPackets(1);
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
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
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
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
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
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
                SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
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

    public static class UpdateAndAddMarker extends  AsyncTask<Void , Void, Long> {

        @Override
        protected Long doInBackground(Void... voids) {
            //Common.wait(3000);
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance();
            sessionCdlDb.getMarkerDataDAO().update1();
            sessionCdlDb.getMarkerDataDAO().update2();
            sessionCdlDb.getMarkerDataDAO().update3();
            sessionCdlDb.getMarkerDataDAO().update4();
            sessionCdlDb.getMarkerDataDAO().deleteByMarkerNum(4l);

            MarkerData markerData1 = new MarkerData(1587043905133l,
            64+"",
           "", 4l);
            markerData1.lat = 12.9837341308594f;
            markerData1.lng = 77.4833297729492f;
            MarkerData markerData2 = new MarkerData(1587046305893l,
                    128+"",
                    "", 4l);
            markerData2.lat = 12.986572265625f;
            markerData2.lng = 77.4520874023438f;
            sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData1);
            sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData2);
            return 1234l;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            System.out.println("On Post Exceuted: "+aLong);
        }
    }

}
