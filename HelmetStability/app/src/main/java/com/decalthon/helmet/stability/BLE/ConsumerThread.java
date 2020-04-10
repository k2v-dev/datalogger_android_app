package com.decalthon.helmet.stability.BLE;

import android.content.Context;
import android.util.Log;

import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author gts
 */
public class ConsumerThread extends Thread{
    private final String TAG = "ConsumerThread";
    public static ConcurrentLinkedQueue<DeviceData> DATA_QUEUE = new ConcurrentLinkedQueue<>();
    private static boolean isThreadActive = false;
    public static boolean stopProducing = false;

    private Context context;
    private  String DEV_1 ;
    private  String DEV_2 ;
    private  String DEV_3;

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
//
//                Log.d(TAG, "broadcastData: checking address and data\n " + deviceData.mac_address +", "+stringBuilder);
                try {
                    if (device_id.equalsIgnoreCase(DEV_1)) {
                        Device1_Parser device1_parser = new Device1_Parser(context, device_id, deviceData.mac_address);
                        device1_parser.parse(deviceData.bytes);
//                        Log.d(TAG, "run: Device 1 parsed");
                    } else if (device_id.equalsIgnoreCase(DEV_2)) {
                        ButtonBox_Parser buttonBox_parser = new ButtonBox_Parser(context, device_id, deviceData.mac_address);
                        buttonBox_parser.parse(deviceData.bytes);
                    } else if (device_id.equalsIgnoreCase(DEV_3)) {
                        //EventBus.getDefault().post(new HeartRateBelt(hr_value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        isThreadActive = false;
    }
}
