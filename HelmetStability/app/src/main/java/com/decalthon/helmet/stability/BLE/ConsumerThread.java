package com.decalthon.helmet.stability.ble;

import android.content.Context;
import android.util.Log;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.devicemodels.DeviceData;
import com.decalthon.helmet.stability.model.devicemodels.DeviceHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author gts
 */
public class ConsumerThread extends Thread{
    private final String TAG = "ConsumerThread";
    public static ConcurrentLinkedQueue<DeviceData> DATA_QUEUE = new ConcurrentLinkedQueue<>();
    public static boolean isThreadActive = false;
    public static boolean stopProducing = false;

    private Context context;
    private  String DEV_1 ;
    private  String DEV_2 ;
    private  String DEV_3;
    private boolean bSentStopCmd = false;

    //Single instance
//    private static ConsumerThread singleInstance ;

    public ConsumerThread(Context context) {
        this.setName("ConsumerThread");
        this.context = context;
        if (context != null) {
            DEV_1 = context.getResources().getString(R.string.device1_tv);
            DEV_2 = context.getResources().getString(R.string.device2_tv);
            DEV_3 = context.getResources().getString(R.string.device3_tv);
        }
    }

//    public static ConsumerThread getInstance(){
//        if(singleInstance == null){
//            singleInstance = new ConsumerThread();
//        }
//        return singleInstance;
//    }


    @Override
    public void run() {
        if (context == null){
            Log.d(TAG, "Cannot create run thread");
            return;
        }

        // Whether consumer
        if(isThreadActive){
            Log.d(TAG, getName()+": Thread is already started");
            return;
        }

        isThreadActive = true;
        int wait_sec = 1;
        while(isThreadActive){
            DeviceData deviceData = DATA_QUEUE.poll();

            // if no data is available, then wait for data upto 10 second
            if(deviceData == null) {
                if (wait_sec <= Constants.WAIT_FOR_DATA){
                    try {
                        Thread.sleep(1000);
                        wait_sec++;
                        Log.d(TAG, "Waiting..."+wait_sec);
                        continue;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    isThreadActive = false;
                    break;
                }
            }
            wait_sec = 1;
            // Process the datase
            // Check the device type
            String device_id = Constants.ADDR_ID_MAPS.get(deviceData.mac_address);
            if (device_id == null || device_id.length() == 0) {
                return;
            }

            if (deviceData.bytes != null && deviceData.bytes.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(deviceData.bytes.length);
//                for(byte byteChar : deviceData.bytes)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                Log.d(TAG, "broadcastData: checking address and data\n " + deviceData.mac_address +", "+stringBuilder);
                try{
                    checkPacketType(device_id, deviceData.bytes, deviceData.mac_address);
                }catch (Exception ex){
                    ex.printStackTrace();
                }

//                try {
//                    if (device_id.equalsIgnoreCase(DEV_1)) {
//                        Device1_Parser device1_parser = new Device1_Parser(context, device_id, deviceData.mac_address);
//                        device1_parser.parse(deviceData.bytes);
////                        Log.d(TAG, "run: Device 1 parsed");
//                    } else if (device_id.equalsIgnoreCase(DEV_2)) {
//                        ButtonBox_Parser buttonBox_parser = new ButtonBox_Parser(context, device_id, deviceData.mac_address);
//                        buttonBox_parser.parse(deviceData.bytes);
//                    } else if (device_id.equalsIgnoreCase(DEV_3)) {
//                        //EventBus.getDefault().post(new HeartRateBelt(hr_value));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
        isThreadActive = false;
    }

    private void checkPacketType(String device_id, byte[] bytes, String address){

        if((bytes[0] == 0x65) && (bytes[1] == 0x00)  && (bytes[2] == 0x65)){
            sendNxtSessioncmd(device_id);
        }else if((bytes[0] == 0x40) && (bytes[1] == 0x00)  && (bytes[2] == 0x40)){
           //removeSession(device_id);
            sendStopCmd(device_id);
        }else if((bytes[0] == 0x50) && (bytes[1] == 0x00)  && (bytes[2] == 0x50)){
            sendStopCmd(device_id);
        }else if((bytes[0] == 0x09) && (bytes[1] == 0x00)  && (bytes[2] == 0x09)){
//            sendStopCmd(device_id);
        }else{
            sendParsingData(device_id, bytes, address);
        }
    }

    private void sendStopCmd(String device_id){
        try {
            if (device_id.equalsIgnoreCase(DEV_1)) {
                Device1_Parser.sendStopCmd(context);
            } else if (device_id.equalsIgnoreCase(DEV_2)) {
                ButtonBox_Parser.sendStopCmd(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeSession(String device_id){
        try {
            if (device_id.equalsIgnoreCase(DEV_1)) {
                if(DeviceHelper.SESSION_SUMMARIES.size() > 0) {
                    Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES.entrySet().iterator().next();
                    int key = entry.getKey();
                    DeviceHelper.SESSION_SUMMARIES.remove(key);
                }
            } else if (device_id.equalsIgnoreCase(DEV_2)) {
                if(DeviceHelper.SESSION_SUMMARIES_BB.size() > 0) {
                    Map.Entry<Integer, SessionSummary> entry = DeviceHelper.SESSION_SUMMARIES_BB.entrySet().iterator().next();
                    int key = entry.getKey();
                    DeviceHelper.SESSION_SUMMARIES_BB.remove(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNxtSessioncmd(String device_id){
        try {
            if (device_id.equalsIgnoreCase(DEV_1)) {
                Device1_Parser.sendNextSessionCmd(context);
            } else if (device_id.equalsIgnoreCase(DEV_2)) {
                ButtonBox_Parser.sendNextSessionCmd(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendParsingData(String device_id, byte[] bytes, String address){
        try {
            if (device_id.equalsIgnoreCase(DEV_1)) {
                Device1_Parser device1_parser = new Device1_Parser(context);
                device1_parser.parse(bytes);
//                        Log.d(TAG, "run: Device 1 parsed");
            } else if (device_id.equalsIgnoreCase(DEV_2)) {
                ButtonBox_Parser buttonBox_parser = new ButtonBox_Parser(context);
                buttonBox_parser.parse(bytes);
            } else if (device_id.equalsIgnoreCase(DEV_3)) {
                //EventBus.getDefault().post(new HeartRateBelt(hr_value));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
