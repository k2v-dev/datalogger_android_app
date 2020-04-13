package com.decalthon.helmet.stability.BLE;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.DB.DatabaseHelper;
import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.ByteUtils;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceHelper;
import com.decalthon.helmet.stability.model.DeviceModels.session.SessionHeader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ButtonBox_Parser extends Device_Parser{
    private static final String TAG = ButtonBox_Parser.class.getSimpleName();
    private Context context;
    private String device_id;
    private String address;
    private static byte[] last_data;

    //    private static SessionCdlDb sessionCdlDb;
    private static SensorDataEntity currentSensorDataEntity;
    private static short prevBtnType = -1;

    private List<SessionSummary> sessionSummaryList;
    private ArrayList<Long> timestamps;

    public ButtonBox_Parser(Context context, String device_id, String address) {
        this.context = context;
        this.device_id = device_id;
        this.address = address;
        sessionCdlDb = SessionCdlDb.getInstance(context);
        timestamps = new ArrayList<>();
    }

    // Parsing the ButtonBox data and
    public void parse( byte[] received_data){

        if( received_data.length < 2){
            return;
        }
        // Session Summary
        // Session summary's header
        if (received_data[0] == (byte)0xAC) // for session summary
        {
            Log.d(TAG, "parse bb : Received session summary with 0xAC");
            parseSessionSummary(received_data);
        }
        else if (received_data[0] == (byte)0xDD) // for session header
        {
            Log.d(TAG, "parse bb : Received session header with 0xDD");
            parseSessionHeader(received_data);
        }
        else if (received_data[0] == (byte)0xAA) // for data packet header
        {
            //Log.d(TAG, "parse bb : Received session packet with 0xAA");
            parseButtonBox(received_data);
        }
    }

    /**
     * Parse button box data
     * @param received_data reeceived the data in byte array
     */
    protected void parseButtonBox(byte[] received_data){
        //Log.d(TAG, "parseButtonBox: receiving AA packets");
//        Integer currentSessionNumber = 50;
        Integer currentSessionNumber = DeviceHelper.REC_SESSION_HDR_BB.getNumber();
        SessionSummary sessionSummary = DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber);
        if(sessionSummary == null){
            return;
        }

        int packet_size = 18;


        if(received_data.length < packet_size) {
            Log.d(TAG, "Invalid packet data");
            return ;
        }

    //        SessionFile.getInstance(context).writeHexData(DeviceHelper.REC_SESSION_HDR_BB.getNumber(), received_data);

        //Get checksum value from packet
        int checksum = Helper.getIntValue(received_data[packet_size-2], received_data[packet_size - 1]);

        // Calculate the checksum from packet
        int total = 0;
        for(int i = 0 ; i <= (packet_size-3) ; i++ ){
            int val = received_data[i] & 0xFF;
            total += val;
        }

        // if checksum and calculated checksume are not equal, then data is corrupted and so reject the packet
        if(checksum != total){
            Log.d(TAG, "ButtonBox: Unmatching checksum = "+checksum+", total ="+total);
            return;
        }

        ButtonBoxEntity buttonBoxEntity = new ButtonBoxEntity();

        // Convert packet number to long
        byte[] long_num = new byte[Long.BYTES];
        long_num[3] = received_data[1];
        long_num[2] = received_data[2];
        long_num[1] = received_data[3];
        long_num[0] = received_data[4];

        buttonBoxEntity.packet_number = (int) ByteUtils.bytesToLongNew(long_num);

        Calendar calendar = Calendar.getInstance();

        Date date = new     Date();
        // If session header for this packet is available, then set date
        if(DeviceHelper.REC_SESSION_HDR_BB != null){
            date = DeviceHelper.REC_SESSION_HDR_BB.getDate();
        }
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, received_data[5] & 0xFF);
        calendar.set(Calendar.MINUTE, received_data[6] & 0xFF);
        calendar.set(Calendar.SECOND, received_data[7] & 0xFF);
        int milli_second = received_data[8] & 0xFF;
        calendar.set(Calendar.MILLISECOND, milli_second*10);

        buttonBoxEntity.date = calendar.getTime();

    //        sensorDataEntity.dateMillis = calendar.getTimeInMillis();
        buttonBoxEntity.dateMillis = buttonBoxEntity.date.getTime();
        buttonBoxEntity.ax_3axis = Helper.getShortValue(received_data[9], received_data[10])* Constants.ACC_3axis_SF;
        buttonBoxEntity.ay_3axis = Helper.getShortValue(received_data[11], received_data[12])*Constants.ACC_3axis_SF;
        buttonBoxEntity.az_3axis = Helper.getShortValue(received_data[13], received_data[14])*Constants.ACC_3axis_SF;

        buttonBoxEntity.button_type = (short) (received_data[15] & 0xFF);

        if(prevBtnType != buttonBoxEntity.button_type){// by default value is 0, it will be start marker
            prevBtnType = buttonBoxEntity.button_type;
            MarkerData markerData = new MarkerData(buttonBoxEntity.dateMillis,  Constants.MARKER_MAPS.get(prevBtnType), "");
            markerData.session_id = buttonBoxEntity.session_id;
            new InsertMarkerDataAsyncTask().execute(markerData);
        }
//        Integer currentSessionNumber = DeviceHelper.REC_SESSION_HDR_BB.getNumber();


        buttonBoxEntity.session_id = sessionSummary.getSession_id();
        DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber).setBb_num_read_pkt((int)buttonBoxEntity.packet_number);
        addButtonBoxEntity(buttonBoxEntity);

        if(DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber).getBb_num_read_pkt()
                >= sessionSummary.getBb_total_pkts()){
            System.out.println("Number of packets-->"+DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber).getBb_num_read_pkt());
            DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber).setBb_isComplete(true);
            new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES_BB.get(currentSessionNumber));
            sendSessionCommand();
            DeviceHelper.SESSION_SUMMARIES_BB.remove(currentSessionNumber);
            // Stop marker
            MarkerData markerData = new MarkerData(buttonBoxEntity.dateMillis,  Constants.MARKER_MAPS.get(255), "");
            markerData.session_id = buttonBoxEntity.session_id;
            new InsertMarkerDataAsyncTask().execute(markerData);
            prevBtnType = -1;
            new DatabaseHelper.UpdateMarkerData().execute(buttonBoxEntity.session_id);
        }else{
            try{
                checkForHundredPackets(currentSessionNumber);
    //            sensorDataEntity.setSessionSummary(currentSessionSummary);
            }catch (NullPointerException e){
                System.out.println("Unable to set packet number");
            }
        }

    }
//    private void checkForHundredPackets(int session_number){
//        long packet_check_index  = DeviceHelper.SESSION_SUMMARIES_BB.get(session_number).getBb_num_read_pkt();
//        if( ( packet_check_index % 100 ) == 0){
//            System.out.println(packet_check_index + " --> check for 100 packets");
//            new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES_BB.get(session_number));
//        }
//    }

    private static int counter = 0;
    private void checkForHundredPackets(int session_number){
        int cur_pkt  = DeviceHelper.SESSION_SUMMARIES_BB.get(session_number).getBb_num_read_pkt();

        if( (cur_pkt-counter) > 100){
            System.out.println("pkt_num="+ cur_pkt+" --> check for 100 packets");
            new UpdateNumberofReadPacketsAsyncTask().execute(DeviceHelper.SESSION_SUMMARIES_BB.get(session_number));
            counter = cur_pkt;
        }
    }

    private void addButtonBoxEntity(ButtonBoxEntity buttonBoxEntity) {
        try {
            new InsertButtonBoxEntityAsyncTask().execute(buttonBoxEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Parsing session header which recevied just before packets
    private void parseSessionHeader(byte[] received_data) {
        int packet_size = 16; // change to 16 when new firmwarer availabe
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
            System.out.println("ButtonBox: Session Header: Unmatching checksum = "+checksum+", total ="+total);
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

        sessionHeader.setActivity_type(received_data[13] & 0xFF);

        DeviceHelper.REC_SESSION_HDR_BB = sessionHeader;
        DeviceHelper.SESSION_SUMMARIES_BB.get(session_num).setActivity_type(sessionHeader.getActivity_type());
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
            System.out.println("ButtonBox: Session Summary: Unmatching checksum = "+checksum+", total ="+total);
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


        DeviceHelper.SESSION_SUMMARIES_BB.clear();
        int index = 2;

        for (int i=0 ; i< num_sessions ; i++){

            SessionSummary sessionSummary = new SessionSummary();

            sessionSummary.setSession_number((received_data[index] & 0xFF));
            int number_pages = Helper.getIntValue(received_data[index+1], received_data[index+2]); // Each page contains 24 pkt
            int num_packets_lst_page = received_data[index+3] & 0xFF; // number of pkts in last page
            int total_pkts = number_pages*120 + num_packets_lst_page;
            sessionSummary.setBb_num_pages(number_pages);
            sessionSummary.setBb_total_pkts(total_pkts);
            sessionSummary.setBb_total_pkts(31000);
            sessionSummary.setBb_total_data(total_pkts*18);// 80 bytes per packet
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
//            DeviceHelper.SESSION_SUMMARIES_BB.put(sessionSummary.getSession_number(), sessionSummary);
            index += 11;
            insertSessionSummary(sessionSummary);
        }
//TODO Get the query ip: list of timestamp op: list  of summaries [DONE]
        Long [] storedTimestamps = (Long[])timestamps.toArray(new Long[0]);
        try {
            List<SessionSummary> sessionSummariesStored  =
                    (new GetSessionSummaryListAsyncTask().execute(storedTimestamps)).get();
            for(SessionSummary sessionSummary:sessionSummariesStored){
//                if(DeviceHelper.SESSION_SUMMARIES_BB.get(sessionSummary.getSession_number()) != null ){
                DeviceHelper.SESSION_SUMMARIES_BB.put(sessionSummary.getSession_number(),sessionSummary);
//                }
            }
        } catch (ExecutionException e) {
            System.out.println("Check the code,there is an execution exception");
        } catch (InterruptedException e) {
            System.out.println("Check for an interrupted exception, executed exception failed");
        }

        last_data = new byte[packet_size];
        for (int i = 0; i < packet_size; i++) {
            last_data[i] = received_data[i];
        }

        sendSessionCommand();
    }

    private void sendSessionCommand(){
        Log.d(TAG, "sendSessionCommand: Entering send session comand");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device2_tv));
//                if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//                    deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//                    Common.wait(100);
//                    //TODO If Device Helper session summary has size > 0, 374 - 379 [DONE]
//                    Log.d(TAG, "run: Running session command thread ");
//                    if(DeviceHelper.SESSION_SUMMARIES_BB.size() > 0) {
//                        Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES_BB.entrySet().iterator().next();
//                        int key = entry.getKey();
//                        byte[] session_cmd = request_session_data(key, entry.getValue().getBb_num_read_pkt());
//                        Log.d(TAG, "SessionCommand: " + Common.convertByteArrToStr(session_cmd, true));
//                        deviceDetails.sendData(session_cmd);
//                    }
//                    //deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
//                }
//            }
//        }).start();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device2_tv));
                if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
                    ConsumerThread.stopProducing = true;
                    Common.wait(1000);
                    deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
                    Common.wait(2000);
                    ConsumerThread.DATA_QUEUE.clear();
                    ConsumerThread.stopProducing = false;
                    Log.d(TAG, "START NEW SESSION DATA");
                    counter = 0;
                    //TODO If Device Helper session summary has size > 0, 374 - 379 [DONE]
                    if(DeviceHelper.SESSION_SUMMARIES_BB.size() > 0) {
                        Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES_BB.entrySet().iterator().next();
                        int key = entry.getKey();
//                        int key = 51;
                        byte[] session_cmd = request_session_data(key, entry.getValue().getBb_num_read_pkt());
                        Log.d(TAG, "SessionCommand: " + Common.convertByteArrToStr(session_cmd, true));
                        deviceDetails.sendData(session_cmd);
                    }
                    //deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
                }
            }
        };

        MainActivity.shared().runOnUiThread(runnable);
//        //new Thread(runnable).start();
    }

    private static class InsertButtonBoxEntityAsyncTask extends AsyncTask<ButtonBoxEntity,Void,Void> {

        @Override
        protected Void doInBackground(ButtonBoxEntity... buttonBoxEntitiesboxEntities) {
            try {

                sessionCdlDb.getSessionDataDAO().insertButtonBoxPacket(buttonBoxEntitiesboxEntities);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                if(buttonBoxEntitiesboxEntities != null && buttonBoxEntitiesboxEntities.length > 0){
                    Log.d(TAG, "packet #:"+buttonBoxEntitiesboxEntities[0].packet_number+", time="+buttonBoxEntitiesboxEntities[0].dateMillis);
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

}
