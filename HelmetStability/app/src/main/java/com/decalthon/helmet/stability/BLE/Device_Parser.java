package com.decalthon.helmet.stability.BLE;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.Utilities.ByteUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Device_Parser {
    protected static SessionCdlDb sessionCdlDb;


    //    //01 pl1 pl2 pl3 pl4 pl5 pl6 cs1 cs2
//    public static final String SESSION_CMD = "0x01 0x32 0x00 0x00 0x00 0x01 0x01 0x00 0x35";
    protected static byte[] request_session_data(int session_num, long packet_num){
        byte[] cmd = new byte[9];

        cmd[0] = 0x01;
        cmd[1] = (byte)(session_num);
        byte[] nums = ByteUtils.longToBytes(packet_num); // Convert packet_num to 8 byte array
        cmd[2] = nums[3];
        cmd[3] = nums[2];
        cmd[4] = nums[1];
        cmd[5] = nums[0];

        cmd[6] = 0x02;

        int total = 0;
        for(int i = 0 ; i <= 7 ; i++ ){
            int val = cmd[i] & 0xFF;
            total += val;
        }

        byte[] checksum = ByteUtils.intToBytes(total); //Convert total to 4 byte array

        cmd[7] = checksum[1];
        cmd[8] = checksum[0];

        return cmd;
    }

    protected void insertSessionSummary(SessionSummary sessionSummary) {
        SessionSummary currentSessionSummary = null;
        try {
            currentSessionSummary = new GetSessionSummaryAsyncTask().execute(sessionSummary.getDate()).get();
            if(currentSessionSummary == null){
                new InsertSessionSummaryAsyncTask().execute(sessionSummary);
            }else{// Ajit: Update num_pages, total_pkts, total_data as per device
                if((this instanceof Device1_Parser) && currentSessionSummary.getTotal_pkts() == 0 ){
                    currentSessionSummary.setTotal_pkts(sessionSummary.getTotal_pkts());
                    currentSessionSummary.setTotal_data(sessionSummary.getTotal_data());
                    currentSessionSummary.setNum_pages(sessionSummary.getNum_pages());
                    new UpdateNumberofReadPacketsAsyncTask().execute(currentSessionSummary);
                }else if((this instanceof ButtonBox_Parser) && currentSessionSummary.getBb_total_pkts() == 0 ){
                    currentSessionSummary.setBb_total_pkts(sessionSummary.getBb_total_pkts());
                    currentSessionSummary.setBb_total_data(sessionSummary.getBb_total_data());
                    currentSessionSummary.setBb_num_pages(sessionSummary.getBb_num_pages());
                    new UpdateNumberofReadPacketsAsyncTask().execute(currentSessionSummary);
                }
            }
        }catch(android.database.sqlite.SQLiteConstraintException e){
            System.out.println("Duplicate session being entered" + e.getMessage());
        }catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static class InsertSessionSummaryAsyncTask extends AsyncTask<SessionSummary,Void,Void> {
        @Override
        protected Void doInBackground(SessionSummary... sessionSummaries) {
            sessionCdlDb.getSessionDataDAO().insertSessionSummary(sessionSummaries);
            return null;
        }
    }

    protected static class GetSessionSummaryListAsyncTask extends AsyncTask<Long , Void, List<SessionSummary>> {
        @Override
        protected List<SessionSummary> doInBackground(Long... longs) {
            return sessionCdlDb.getSessionDataDAO().getSummaryList(longs);
        }

        @Override
        protected void onPostExecute(List<SessionSummary> sessionSummaries) {
            super.onPostExecute(sessionSummaries);
        }
    }

    protected static class GetSessionSummaryAsyncTask extends  AsyncTask<Long, Void, SessionSummary>{

        @Override
        protected SessionSummary doInBackground(Long... longs) {
            if(longs != null && longs.length > 0) {
                return sessionCdlDb.getSessionDataDAO().getSessionSummary(longs[0]);
            }
            return null;
        }
    }

    protected static  class UpdateNumberofReadPacketsAsyncTask extends AsyncTask<SessionSummary,Void,Void>  {

        @Override
        protected Void doInBackground(SessionSummary... sessionSummaries) {
            long timestamp1 = System.currentTimeMillis();
            sessionCdlDb.getSessionDataDAO().updateSessionSummaries(sessionSummaries);
            long timestamp2 = System.currentTimeMillis();
            System.out.println("Timestamp diff: Updating numreadpacket "+ (timestamp2-timestamp1));
            return null;
        }
    }
}
