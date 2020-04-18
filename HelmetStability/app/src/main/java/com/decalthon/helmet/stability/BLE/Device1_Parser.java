package com.decalthon.helmet.stability.BLE;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.DB.DatabaseHelper;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.ByteUtils;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceHelper;
import com.decalthon.helmet.stability.model.DeviceModels.session.SessionHeader;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Device1_Parser extends Device_Parser{
    private static final String TAG = Device1_Parser.class.getSimpleName();
    Context context;
//    String device_id;
//    String address;
    private static byte[] last_data;
    private static short prevBtnType = -1;
    private static long prev_pkt_num = -1;
    private static long num_pkt_read = 0;

//    private static SessionCdlDb sessionCdlDb;
    private static SensorDataEntity currentSensorDataEntity;

    private List<SessionSummary> sessionSummaryList;
    private ArrayList<Long> timestamps;

    public Device1_Parser(Context context) {
        this.context = context;
//        this.device_id = device_id;
//        this.address = address;
        sessionCdlDb = SessionCdlDb.getInstance(context);
        timestamps = new ArrayList<>();
    }

    // Parsing the helmet data and
    public void parse( byte[] received_data){

        if( received_data.length < 2){
            return;
        }
        // Session Summary
        // Session summary's header
        if (received_data[0] == (byte)0xAC) // for session summary
        {
            //Log.d(TAG, "parse in device1: Received session summary with 0xAC");
            parseSessionSummary(received_data);
        }
        else if (received_data[0] == (byte)0xDD) // for session header
        {
           // Log.d(TAG, "parse: in device1: Received session header with 0xDD");
            parseSessionHeader(received_data);
        }
        else if (received_data[0] == (byte)0xAA) // for data packet header
        {
            //Log.d(TAG, "parse: in device1: Received session data with 0xAA");
            parseSensorData(received_data);
        }
    }

    private void parseSensorData(byte[] received_data) {
//        Integer currentSessionNumber = 52;
        Integer currentSessionNumber = DeviceHelper.REC_SESSION_HDR.getNumber();

        SessionSummary sessionSummary = DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber);
        if(sessionSummary == null){
            return;
        }

        int packet_size = 80;

        if(received_data.length < packet_size) {
            Log.d(TAG, "Invalid packet data");
            return ;
        }

//        SessionFile.getInstance(context).writeHexData(DeviceHelper.REC_SESSION_HDR.getNumber(), received_data);

        //Get checksum value from packet
        int checksum = Helper.getIntValue(received_data[packet_size-5], received_data[packet_size - 4]);

        // Calculate the checksum from packet
        int total = 0;
        for(int i = 0 ; i <= (packet_size-6) ; i++ ){
            int val = received_data[i] & 0xFF;
            total += val;
//            System.out.println("val="+val+", total="+total);
        }

        // if checksum and calculated checksume are not equal, then data is corrupted and so reject the packet
        if(checksum != total){
            System.out.println("Device1: Unmatching checksum = "+checksum+", total ="+total);
            return;
        }

        SensorDataEntity sensorDataEntity = new SensorDataEntity();

        // Convert packet number to long
        byte[] long_num = new byte[Long.BYTES];
        long_num[3] = received_data[1];
        long_num[2] = received_data[2];
        long_num[1] = received_data[3];
        long_num[0] = received_data[4];

        sensorDataEntity.packet_number = (int)ByteUtils.bytesToLongNew(long_num);
        //TODO In every 1000 packets, update session summary using %, using separate thread in method;
        if(sensorDataEntity.packet_number - prev_pkt_num > 1){
            new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber));
            Common.wait(50);
            //sendSessionCommand();
            sendStopCmd(context);
            sendNextSessionCmd(context);
            return;
        }else if(prev_pkt_num >= sensorDataEntity.packet_number){
            return;
        }

        //TODO set SensorDataEntity.sessionsummary = current session summary
        Calendar calendar = Calendar.getInstance();

        Date date = new Date();
        // If session header for this packet is available, then set date
        if(DeviceHelper.REC_SESSION_HDR != null){
            date = DeviceHelper.REC_SESSION_HDR.getDate();
        }
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, received_data[5] & 0xFF);
        calendar.set(Calendar.MINUTE, received_data[6] & 0xFF);
        calendar.set(Calendar.SECOND, received_data[7] & 0xFF);
        int milli_second = received_data[8] & 0xFF;
        calendar.set(Calendar.MILLISECOND, milli_second*10);

        sensorDataEntity.date = calendar.getTime();
//        sensorDataEntity.dateMillis = calendar.getTimeInMillis();
        sensorDataEntity.dateMillis = sensorDataEntity.date.getTime();
        sensorDataEntity.ax_9axis_dev1 = Helper.getShortValue(received_data[9], received_data[10])*Constants.ACC_9axis_SF;
        sensorDataEntity.ay_9axis_dev1 = Helper.getShortValue(received_data[11], received_data[12])*Constants.ACC_9axis_SF;
        sensorDataEntity.az_9axis_dev1 = Helper.getShortValue(received_data[13], received_data[14])*Constants.ACC_9axis_SF;

        sensorDataEntity.gx_9axis_dev1 = Helper.getShortValue(received_data[15], received_data[16])*Constants.GYR_9axis_SF;
        sensorDataEntity.gy_9axis_dev1 = Helper.getShortValue(received_data[17], received_data[18])*Constants.GYR_9axis_SF;
        sensorDataEntity.gz_9axis_dev1 = Helper.getShortValue(received_data[19], received_data[20])*Constants.GYR_9axis_SF;

        sensorDataEntity.mx_9axis_dev1 = Helper.getShortValue(received_data[21], received_data[22])*Constants.MAG_9axis_SF;
        sensorDataEntity.my_9axis_dev1 = Helper.getShortValue(received_data[23], received_data[24])*Constants.MAG_9axis_SF;
        sensorDataEntity.mz_9axis_dev1 = Helper.getShortValue(received_data[25], received_data[26])*Constants.MAG_9axis_SF;

        sensorDataEntity.ax_3axis_dev1 = Helper.getShortValue(received_data[27], received_data[28])*Constants.ACC_3axis_SF;
        sensorDataEntity.ay_3axis_dev1 = Helper.getShortValue(received_data[29], received_data[30])*Constants.ACC_3axis_SF;
        sensorDataEntity.az_3axis_dev1 = Helper.getShortValue(received_data[31], received_data[32])*Constants.ACC_3axis_SF;

        //received_data[33] - Latitude  in unsigned byte format.
        //received_data[34]  - Longitude  in unsigned byte format
        // received_data[45]  -GPS Tag Type.
        // received_data[46]  - GPS Tagger.
        //short gps_tag_type = (short)(received_data[45] & 0xFF);
        sensorDataEntity.gps_tagger = (short)(received_data[46] & 0xFF);

        if(prevBtnType == -1){
            MarkerData markerData = new MarkerData(sensorDataEntity.dateMillis,  "0", "");
            markerData.session_id = sensorDataEntity.session_id;
            new InsertMarkerDataAsyncTask().execute(markerData);
            prevBtnType = (short) sensorDataEntity.packet_number;
            //prevBtnType = gps_tag_type;
        }

        if(sensorDataEntity.gps_tagger > 0){
            MarkerData markerData = new MarkerData(sensorDataEntity.dateMillis,  prevBtnType+"", "");
            markerData.session_id = sensorDataEntity.session_id;
            new InsertMarkerDataAsyncTask().execute(markerData);
            prevBtnType++;
            //prevBtnType = gps_tag_type;
        }


        sensorDataEntity.ax_9axis_dev2 = Helper.getShortValue(received_data[47], received_data[48])*Constants.ACC_9axis_SF;
        sensorDataEntity.ay_9axis_dev2 = Helper.getShortValue(received_data[49], received_data[50])*Constants.ACC_9axis_SF;
        sensorDataEntity.az_9axis_dev2 = Helper.getShortValue(received_data[51], received_data[52])*Constants.ACC_9axis_SF;

        sensorDataEntity.gx_9axis_dev2 = Helper.getShortValue(received_data[53], received_data[54])*Constants.GYR_9axis_SF;
        sensorDataEntity.gy_9axis_dev2 = Helper.getShortValue(received_data[55], received_data[56])*Constants.GYR_9axis_SF;
        sensorDataEntity.gz_9axis_dev2 = Helper.getShortValue(received_data[57], received_data[58])*Constants.GYR_9axis_SF;

        sensorDataEntity.mx_9axis_dev2 = Helper.getShortValue(received_data[59], received_data[60])*Constants.MAG_9axis_SF;
        sensorDataEntity.my_9axis_dev2 = Helper.getShortValue(received_data[61], received_data[62])*Constants.MAG_9axis_SF;
        sensorDataEntity.mz_9axis_dev2 = Helper.getShortValue(received_data[63], received_data[64])*Constants.MAG_9axis_SF;

        sensorDataEntity.ax_3axis_dev2 = Helper.getShortValue(received_data[65], received_data[66])*Constants.ACC_3axis_SF;
        sensorDataEntity.ay_3axis_dev2 = Helper.getShortValue(received_data[67], received_data[68])*Constants.ACC_3axis_SF;
        sensorDataEntity.az_3axis_dev2 = Helper.getShortValue(received_data[69], received_data[70])*Constants.ACC_3axis_SF;

        sensorDataEntity.frontal_slippage = Helper.getShortValue(received_data[71], received_data[72])*0.1f;
        sensorDataEntity.sagital_slippage = Helper.getShortValue(received_data[73], received_data[74])*0.1f;

//        sensorDataEntity.setSessionSummary();
//        System.out.println(currentSessionNumber+"currentSessionNumber");
//        sensorDataEntity.sessionSummary = DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber);
//                currentSensorDataEntity = sensorDataEntity;

        sensorDataEntity.session_id = sessionSummary.getSession_id();
        //DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber).setNum_read_pkt((int)sensorDataEntity.packet_number);
//        addSensorDataEntity(sensorDataEntity);
//        addSensorDataEntity();
        //TODO write completion code, if complete then save session summary, then send session command

        if(sensorDataEntity.packet_number
            > sessionSummary.getTotal_pkts()){
            System.out.println("Number of packets-->"+sensorDataEntity.packet_number);
            // Update the session summary table like number of read packet and completee read
            DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber).setComplete(true);
            new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES.get(currentSessionNumber));
            Common.wait(50);
            DeviceHelper.SESSION_SUMMARIES.remove(currentSessionNumber);

            // Add stop marker in MarkerData table
            MarkerData markerData = new MarkerData(sensorDataEntity.dateMillis,  "0", "");
            markerData.session_id = sensorDataEntity.session_id;
            new InsertMarkerDataAsyncTask().execute(markerData);
            prevBtnType = -1;
            new DatabaseHelper.UpdateMarkerData().execute(sensorDataEntity.session_id);
            // Send command to device for getting next session's data
            //sendSessionCommand();
            sendStopCmd(context);
            // There is bug with below command, After sending the command , device stop sending any data and After that there is no change in session summary
            // When this bug get fixed, then uncomment sendReadSessionCmd  method and comment sendNextSessionCmd method
            //sendReadSessionCmd(currentSessionNumber);
            sendNextSessionCmd(context);
        }else{
            update_num_pkt_rcvd();
            addSensorDataEntity(sensorDataEntity);
            if(sensorDataEntity.packet_number % 100 == 0){
                Log.d(TAG, "Packet number="+sensorDataEntity.packet_number);
            }
//            try{
//                // Update the session on every 100th packet
//                checkForHundredPackets(currentSessionNumber);
////            sensorDataEntity.setSessionSummary(currentSessionSummary);
//            }catch (NullPointerException e){
//                System.out.println("Unable to set packet number");
//            }
        }
    }

//    private static int counter = 0;
//    private void checkForHundredPackets(int session_number){
//                int cur_pkt  = DeviceHelper.SESSION_SUMMARIES.get(session_number).getNum_read_pkt();
//
//                if( (cur_pkt-counter) > 100){
////                    System.out.println(counter + " --> check for 100 packets");
//                    System.out.println("pkt_num="+ cur_pkt+" --> check for 100 packets");
//                    new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES.get(session_number));
//                    counter = cur_pkt;
//                }
//    }

    private void addSensorDataEntity(SensorDataEntity sensorDataEntity) {
        try {
            new InsertSensorDataEntityAsyncTask().execute(sensorDataEntity).get();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    private void addSensorDataEntity() {
//        System.out.println("Entering async task wit db instance " + sessionCdlDb.toString());
//        new InsertSensorDataEntityAsyncTask().execute();
//    }
    // Parsing session header which recevied just before packets
    private void parseSessionHeader(byte[] received_data) {
        int packet_size = 18; // change to 16 when new firmwarer availabe
        int session_num =  received_data[1] & 0xFF ;

        if(received_data.length < packet_size) {
            Log.d(TAG, "Invalid session header data");
            return ;
        }
//        DeviceHelper.SESSION_SUMMARIES.remove(session_num);
//        SessionFile.getInstance(context).writeHexData(session_num, received_data);
        //Get checksum value from packet
        int checksum = Helper.getIntValue(received_data[packet_size-2], received_data[packet_size - 1]);

        // Calculate the checksum from packet
        int total = 0;
        for( int i = 0 ; i <= (packet_size-3) ; i++ ){
            int val = received_data[i] & 0xFF;
            total += val;
//            System.out.println("val="+val+", total="+total);
        }

        // if checksum and calculated checksume are not equal, then data is corrupted and so reject the packet
        if(checksum != total){
            System.out.println("Device1: Session Header: Unmatching checksum = "+checksum+", total ="+total+", packet="+Common.convertByteArrToStr(received_data, true));
            sendStopCmd(context);
            sendNextSessionCmd(context);
            return;
        }

        SessionHeader sessionHeader = new SessionHeader();
        sessionHeader.setNumber(session_num);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, received_data[2] & 0xFF);
        int month = received_data[3] & 0xFF;
        calendar.set(Calendar.MONTH, month - 1);
        int year = received_data[4] & 0xFF;
        int yr =  calendar.get(Calendar.YEAR);
        yr = yr - yr%100;
        year = yr + year;
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, received_data[5] & 0xFF);
        calendar.set(Calendar.MINUTE, received_data[6] & 0xFF);
        calendar.set(Calendar.SECOND, received_data[7] & 0xFF);
        int milli_second = received_data[8] & 0xFF;
        calendar.set(Calendar.MILLISECOND, milli_second*10);
        sessionHeader.setDate(calendar.getTime());

        byte[] float_num = new byte[Float.BYTES];

        float_num[3] = received_data[9];
        float_num[2] = received_data[10];
        float_num[1] = received_data[11];
        float_num[0] = received_data[12];

        float total_size = ByteUtils.bytesToFloat(float_num);
        sessionHeader.setData_size(total_size);

        sessionHeader.setFirmwareType((short) (received_data[13] & 0xFF));

        sessionHeader.setActivity_type(received_data[14] & 0xFF);

        sessionHeader.setSamp_freq((short) (received_data[15] & 0xFF));

        update_num_pkt_rcvd();

        DeviceHelper.REC_SESSION_HDR = sessionHeader;
        DeviceHelper.SESSION_SUMMARIES.get(session_num).setActivity_type(sessionHeader.getActivity_type());
        DeviceHelper.SESSION_SUMMARIES.get(session_num).setFirmware_type(sessionHeader.getFirmwareType());
        DeviceHelper.SESSION_SUMMARIES.get(session_num).setSampling_freq(sessionHeader.getSamp_freq());
        //DeviceHelper.SESSION_HDRS.put(session_num, sessionHeader);
    }

    // Parse Session summary data
    private void parseSessionSummary(byte[] received_data){
        int num_sessions =  received_data[1] & 0xFF ;
        int packet_size = 4 + 11*num_sessions; // 4==> 2 byte for header, 2 byte for checksum and 11byte per session info

        if(received_data.length < packet_size) {
            Log.d(TAG, "Invalid session summary data");
            return ;
        }
        //Get checksum value from packet
        int checksum = Helper.getIntValue(received_data[packet_size-2], received_data[packet_size - 1]);

        // Calculate the checksum from packet
        int total = 0;
        for(int i = 0 ; i <= (packet_size-3) ; i++ ){
            int val = received_data[i] & 0xFF;
            total += val;
//            System.out.println("val="+val+", total="+total);
        }

        // if checksum and calculated checksume are not equal, then data is corrupted and so reject the packet
        if(checksum != total){
            System.out.println("Device1: Session Summary: Unmatching checksum = "+checksum+", total ="+total);
            return;
        }

        // Check whether packet is repeated or not
        if (last_data != null && last_data.length == packet_size) {
            boolean bFound = false;
            for (int i = 0; i < packet_size; i++) {
                if(last_data[i] != received_data[i]){// if anyone byte is unmatching, then current pkt is new
                    bFound = true;
                    break;
                }
            }
            if(!bFound){
                Log.w(TAG, "Repeating data found");
                return;
            }
        }


        DeviceHelper.SESSION_SUMMARIES.clear();
        int index = 2;
        int total_pkts_available = 0;
        for (int i=0 ; i< num_sessions ; i++){

            SessionSummary sessionSummary = new SessionSummary();

            sessionSummary.setSession_number((received_data[index] & 0xFF));
            int number_pages = Helper.getIntValue(received_data[index+1], received_data[index+2]); // Each page contains 24 pkt
            int num_packets_lst_page = received_data[index+3] & 0xFF; // number of pkts in last page
            int total_pkts = number_pages*25 + num_packets_lst_page;
            //int total_pkts = 25000;
            total_pkts_available += total_pkts;
            sessionSummary.setNum_pages(number_pages);
            sessionSummary.setTotal_pkts(total_pkts);
            sessionSummary.setTotal_data(total_pkts*80);// 80 bytes per packet
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, received_data[index+4] & 0xFF);
            int month = received_data[index+5] & 0xFF;
            calendar.set(Calendar.MONTH, month - 1);
            int year = received_data[index+6] & 0xFF;
            int yr =  calendar.get(Calendar.YEAR);
            yr = yr - yr%100;
            year = yr + year;
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.HOUR_OF_DAY, received_data[index+7] & 0xFF);
            calendar.set(Calendar.MINUTE, received_data[index+8] & 0xFF);
            calendar.set(Calendar.SECOND, received_data[index+9] & 0xFF);
            int milli_second = received_data[index+10] & 0xFF;
            calendar.set(Calendar.MILLISECOND, milli_second*10);
            timestamps.add(calendar.getTimeInMillis());
//            sessionSu0mmary.setDate(calendar.getTime());
            sessionSummary.setDate(calendar.getTimeInMillis());
//            DeviceHelper.SESSION_SUMMARIES.put(sessionSummary.getSession_number(), sessionSummary);
            index += 11;
            insertSessionSummary(sessionSummary);
        }
//TODO Get the query ip: list of timestamp op: list  of summaries [DONE]
        Long [] storedTimestamps = (Long[])timestamps.toArray(new Long[0]);
        try {
            List<SessionSummary> sessionSummariesStored  =
                    (new GetSessionSummaryListAsyncTask().execute(storedTimestamps)).get();
            for(SessionSummary sessionSummary:sessionSummariesStored){
//                if(DeviceHelper.SESSION_SUMMARIES.get(sessionSummary.getSession_number()) != null ){
                    DeviceHelper.SESSION_SUMMARIES.put(sessionSummary.getSession_number(),sessionSummary);
//                }
            }
//            for(long timestamp : timestamps){
//                SessionSummary currentSessionSummary = new GetSessionSummaryAsyncTask().execute(timestamp).get();
//                if(currentSessionSummary != null){
//
//                }
//            }
        } catch (ExecutionException e) {
            System.out.println("Check the code,there is an execution exception");
        } catch (InterruptedException e) {
            System.out.println("Check for an interrupted exception, executed exception failed");
        }

        last_data = new byte[packet_size];
        for (int i = 0; i < packet_size; i++) {
            last_data[i] = received_data[i];
        }
        update_total_pkt(total_pkts_available);
//        sendSessionCommand();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
//
//                if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//                    deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//                     Common.wait(500);
//                    Map.Entry<Integer,SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
//                    int key= entry.getKey();
//                    byte[] session_cmd = request_session_data(key, 0);
//                    Log.d(TAG, "SessionCommand: "+Common.convertByteArrToStr(session_cmd, true));
//                    deviceDetails.sendData(session_cmd);
//                    //deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
//                }
//            }
//        }).start();
        // send stop cmd to receive session summary
//        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
//
//        if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//            deviceDetails.stopBroadcastDataNotify();
//            //Common.wait(500);
//           // deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//        }
        sendStopCmd(context);
        sendNextSessionCmd(context);
    }

//    private void sendSessionCommand(){
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                Log.d(TAG, "run: sendSessionCommand");
////                DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
////                if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
////                    deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
////                    Common.wait(100);
////                    //TODO If Device Helper session summary has size > 0, 374 - 379 [DONE]
////                    if(DeviceHelper.SESSION_SUMMARIES.size() > 0) {
////                        Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
////                        int key = entry.getKey();
////                        byte[] session_cmd = request_session_data(key, entry.getValue().getNum_read_pkt());
////                        Log.d(TAG, "SessionCommand: " + Common.convertByteArrToStr(session_cmd, true));
////                        deviceDetails.sendData(session_cmd);
////                    }
////                    //deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
////                }
////            }
////        }).start();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
//                if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//                    ConsumerThread.stopProducing = true;
//                    Common.wait(1000);
//                    deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//                    Common.wait(2000);
//                    ConsumerThread.DATA_QUEUE.clear();
//                    ConsumerThread.stopProducing = false;
//                    Log.d(TAG, "START NEW SESSION DATA");
//                    counter = 0;
//                    //TODO If Device Helper session summary has size > 0, 374 - 379 [DONE]
//                    if(DeviceHelper.SESSION_SUMMARIES.size() > 0) {
//                        Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
//                        int key = entry.getKey();
////                        int key = 51;
//                        byte[] session_cmd = request_session_data(key, entry.getValue().getNum_read_pkt());
//                        prev_pkt_num = entry.getValue().getNum_read_pkt();
//                        Log.d(TAG, "SessionCommand: " + Common.convertByteArrToStr(session_cmd, true));
//                        deviceDetails.sendData(session_cmd);
//                    }
//                    //deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
//                }
//            }
//        };
//
//        MainActivity.shared().runOnUiThread(runnable);
//        //new Thread(runnable).start();
//    }

    public static void sendStopCmd(Context context){
        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
        if(deviceDetails != null && deviceDetails.mac_address != null) {
            ConsumerThread.stopProducing = true;
            Common.wait(1000);
            deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
            Common.wait(2000);
            ConsumerThread.DATA_QUEUE.clear();
            ConsumerThread.stopProducing = false;
        }
    }

    public static void sendNextSessionCmd(Context context){
        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
        if(deviceDetails != null && deviceDetails.mac_address != null) {
            Log.d(TAG, "START NEW SESSION DATA");
//            counter = 0;
            //TODO If Device Helper session summary has size > 0, 374 - 379 [DONE]
            if(DeviceHelper.SESSION_SUMMARIES.size() > 0) {

                new GetLastPktNum(pkt_num -> {
                    if (pkt_num >= 0L) {
                        Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
                        int key = entry.getKey();
                        byte[] session_cmd = request_session_data(key, pkt_num);
//                prev_pkt_num = entry.getValue().getNum_read_pkt();
                        deviceDetails.sendData(session_cmd);
                    }
                });
//                Common.wait(50);
//                Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
//                int key = entry.getKey();
//                Common.wait(50);
//                byte[] session_cmd = request_session_data(key, prev_pkt_num);
////                prev_pkt_num = entry.getValue().getNum_read_pkt();
//                Log.d(TAG, "SessionCommand: " + Common.convertByteArrToStr(session_cmd, true));
//                deviceDetails.sendData(session_cmd);
            }
        }
    }

    public static void sendStartActivityCmd(Context context, int activity_type){
        byte[] cmd = new byte[4];

        cmd[0] = 0x06;
        cmd[1] = (byte)(activity_type);

        int total = 0;
        for(int i = 0 ; i <= 1 ; i++ ){
            int val = cmd[i] & 0xFF;
            total += val;
        }

        byte[] checksum = ByteUtils.intToBytes(total); //Convert total to 4 byte array

        cmd[2] = checksum[1];
        cmd[3] = checksum[0];

        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));

        if(deviceDetails != null && deviceDetails.mac_address != null){
            deviceDetails.sendData(cmd);
        }
    }


    public static void sendStopActivityCmd(Context context){
        byte[] cmd = new byte[4];

        cmd[0] = 0x07;
        cmd[1] = 0x00;
        cmd[2] = 0x00;
        cmd[3] = 0x07;

        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));

        if(deviceDetails != null && deviceDetails.mac_address != null){
            deviceDetails.sendData(cmd);
        }
    }

    public void sendReadSessionCmd(int session_num){
        byte[] cmd = new byte[4];

        cmd[0] = 0x65;
        cmd[1] = (byte)(session_num);

        int total = 0;
        for(int i = 0 ; i <= 1 ; i++ ){
            int val = cmd[i] & 0xFF;
            total += val;
        }

        byte[] checksum = ByteUtils.intToBytes(total); //Convert total to 4 byte array

        cmd[2] = checksum[1];
        cmd[3] = checksum[0];

        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));

        if(deviceDetails != null && deviceDetails.mac_address != null){
            deviceDetails.sendData(cmd);
        }
    }

    /**
     * Update the number packets for device 2
     */
    public void update_num_pkt_rcvd(){
        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
        if(deviceDetails != null && deviceDetails.mac_address != null) {
            deviceDetails.num_pkts_rcvd = num_pkt_read+prev_pkt_num;
        }
    }

    /**
     * Update the total number of packets for device2
     * @param total_pkt_read
     */
    public void update_total_pkt(long total_pkt_read){
        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
        if(deviceDetails != null && deviceDetails.mac_address != null) {
            deviceDetails.total_pkts = total_pkt_read;
        }
    }
//    private void insertSessionSummary(SessionSummary sessionSummary) {
//        SessionSummary currentSessionSummary = null;
//        try {
//            currentSessionSummary = new GetSessionSummaryAsyncTask().execute(sessionSummary.getDate()).get();
//            if(currentSessionSummary == null){
//                new InsertSessionSummaryAsyncTask().execute(sessionSummary);
//            }
//        }catch(android.database.sqlite.SQLiteConstraintException e){
//            System.out.println("Duplicate session being entered" + e.getMessage());
//        }catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }


//    //    //01 pl1 pl2 pl3 pl4 pl5 pl6 cs1 cs2
////    public static final String SESSION_CMD = "0x01 0x32 0x00 0x00 0x00 0x01 0x01 0x00 0x35";
//    private byte[] request_session_data(int session_num, long packet_num){
//        byte[] cmd = new byte[9];
//
//        cmd[0] = 0x01;
//        cmd[1] = (byte)(session_num);
//        byte[] nums = ByteUtils.longToBytes(packet_num); // Convert packet_num to 8 byte array
//        cmd[2] = nums[3];
//        cmd[3] = nums[2];
//        cmd[4] = nums[1];
//        cmd[5] = nums[0];
//
//        cmd[6] = 0x01;
//
//        int total = 0;
//        for(int i = 0 ; i <= 7 ; i++ ){
//            int val = cmd[i] & 0xFF;
//            total += val;
//        }
//
//        byte[] checksum = ByteUtils.intToBytes(total); //Convert total to 4 byte array
//
//        cmd[7] = checksum[1];
//        cmd[8] = checksum[0];
//
//        return cmd;
//    }

    private static class InsertSensorDataEntityAsyncTask extends AsyncTask<SensorDataEntity,Void,Void> {
        @Override
        protected Void doInBackground(SensorDataEntity... sensorDataEntities) {
            try {
                sessionCdlDb.getSessionDataDAO().insertSessionPacket(sensorDataEntities[0]);
                prev_pkt_num = sensorDataEntities[0].packet_number;
            }catch (android.database.sqlite.SQLiteConstraintException e){
                if(sensorDataEntities != null && sensorDataEntities.length > 0){
                    Log.d(TAG, "packet #:"+sensorDataEntities[0].packet_number+", time="+sensorDataEntities[0].dateMillis);
                }
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class InsertMarkerDataAsyncTask extends AsyncTask<MarkerData,Void,Void> {

        @Override
        protected Void doInBackground(MarkerData... markerData) {
            try {
                sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData[0]);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class GetLastPktNum extends AsyncTask<Void, Void, Long> {
        private IGetLastPkt iGetLastPkt;
        public  interface IGetLastPkt { void accept(Long pkt_num); }

        public  GetLastPktNum(IGetLastPkt iGetLastPkt) { this.iGetLastPkt = iGetLastPkt; execute(); }

        @Override
        protected Long doInBackground(Void... voids) {
            if(DeviceHelper.SESSION_SUMMARIES.size() == 0){
                return -1l;
            }
            Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
            prev_pkt_num = sessionCdlDb.getSessionDataDAO().getLastPktNumSD(entry.getValue().getSession_id());
            Log.d(TAG, "Prev Packet num = "+prev_pkt_num);

            return prev_pkt_num;
        }

        @Override
        protected void onPostExecute(Long pkt_num) {
            iGetLastPkt.accept(pkt_num);
        }
    }

//    private static class InsertSensorDataEntityAsyncTask extends AsyncTask<Void,Void,Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            sessionCdlDb.getSessionDataDAO().insertSessionPacket(currentSensorDataEntity);
//            return null;
//        }
//    }
//
//    private static class InsertSessionSummaryAsyncTask extends AsyncTask<SessionSummary,Void,Void>{
//        @Override
//        protected Void doInBackground(SessionSummary... sessionSummaries) {
//            sessionCdlDb.getSessionDataDAO().insertSessionSummary(sessionSummaries);
//            return null;
//        }
//    }
//
//    private static class GetSessionSummaryListAsyncTask extends AsyncTask<Long , Void, List<SessionSummary>> {
//        @Override
//        protected List<SessionSummary> doInBackground(Long... longs) {
//            return sessionCdlDb.getSessionDataDAO().getSummaryList(longs);
//        }
//
//        @Override
//        protected void onPostExecute(List<SessionSummary> sessionSummaries) {
//            super.onPostExecute(sessionSummaries);
//        }
//    }
//
//    private static class GetSessionSummaryAsyncTask extends  AsyncTask<Long, Void, SessionSummary>{
//
//        @Override
//        protected SessionSummary doInBackground(Long... longs) {
//            if(longs != null && longs.length > 0) {
//                return sessionCdlDb.getSessionDataDAO().getSessionSummary(longs[0]);
//            }
//            return null;
//        }
//    }
//
//    private static  class UpdateNumberofReadPacketsAsyncTask extends AsyncTask<SessionSummary,Void,Void>  {
//
//        @Override
//        protected Void doInBackground(SessionSummary... sessionSummaries) {
//            long timestamp1 = System.currentTimeMillis();
//            sessionCdlDb.getSessionDataDAO().updateSessionSummary(sessionSummaries);
//            long timestamp2 = System.currentTimeMillis();
//            System.out.println("Timestamp diff: Updating numreadpacket "+ (timestamp2-timestamp1));
//            return null;
//        }
//    }
}
