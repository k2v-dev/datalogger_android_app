package com.decalthon.helmet.stability.BLE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.model.DeviceModels.BLEConnectionState;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.DeviceModels.HeartRateBelt;
import com.decalthon.helmet.stability.model.DeviceModels.SensoryWatch;


import java.nio.ByteBuffer;

import de.greenrobot.event.EventBus;


/**
 * Created by gts-3 on 9/1/2018.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    String TAG = "MyBroadcastReceiver";
    Context context;
//    public BluetoothLeService mBluetoothLeService = new BluetoothLeService();
    MainActivity activity;

    @Override
    public void onReceive(Context context, Intent intent) {
        context = context;
        final String action = intent.getAction();
        final String address = intent.getStringExtra(BluetoothLeService.MAC_ADDRESS);
        String device_id;
        System.out.println("Entry into onREceive - initial"+address);
        if(Constants.ADDR_ID_MAPS.containsKey(address)){
             device_id = Constants.ADDR_ID_MAPS.get(address);
        }else{
            return;
        }
        activity = MainActivity.shared();
        final String DEV_1 = activity.getResources().getString(R.string.device1_tv);
        final String DEV_2 = activity.getResources().getString(R.string.device2_tv);
        final String DEV_3 = activity.getResources().getString(R.string.device3_tv);

        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
            System.out.println("Entry into onREceive - connected");
//            activity.mConnected = true;
//            activity.func_connect();
            Constants.DEVICE_MAPS.get(device_id).connected = true;
            Constants.DEVICE_MAPS.get(device_id).num_delay = Constants.RECONNECTION_TEST;
            if (!Constants.isStart) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                //Vibrate for 300 milliseconds
                vibrator.vibrate(300);
            }else{
                Log.d(TAG, "Sensor ("+device_id+") connected");
                Toast.makeText(context, device_id+" Connected",
                        Toast.LENGTH_SHORT).show();
            }
            EventBus.getDefault().post(new BLEConnectionState(device_id, true));
//            invalidateOptionsMenu();
//            new ConsumerThread(context).start();

        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//            activity.mConnected = false;
//            activity.func_disconnect();
            //menu.findItem(R.id.action_bluetooth).setIcon(R.drawable.ic_disconnected);
            System.out.println("Entry into onREceive - disconnected");
            if(Constants.DEVICE_MAPS.get(device_id).connected){

//                android.app.AlertDialog.Builder builder =
//                        new android.app.AlertDialog.Builder(context).
//                                setTitle(""+device_id+" Disconnected!").
//                                setMessage("The sensor ("+device_id+") has been disconnected").
//                                setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                }).
//                                setView(null);
//                builder.create().show();
                Log.d(TAG, "Sensor disconnected : "+ device_id);
//                activity.getBleService().disconnect(device_id);
                Constants.DEVICE_MAPS.get(device_id).connected = false;
                if(Constants.isStart == false) {
//                    Constants.DEVICE_MAPS.get(device_id).clear();
                    Toast.makeText(context, device_id+" Disconnected",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(300);
                }
                if(Constants.DEVICE_MAPS.get(device_id).noAutoconnect){
                    Constants.DEVICE_MAPS.get(device_id).clear();
                }
                EventBus.getDefault().post(new BLEConnectionState(device_id, false));
            }


//            MapActivity.stopBroadcastDataNotify(MapActivity.mReadCharacteristic);
//            for (Map.Entry<String, DeviceDetails > entry : Constants.DEVICE_MAPS.entrySet()){
//                mBluetoothLeService.disconnect(entry.getKey());
//            }

//            mBluetoothLeService = null;
//            activity.mConnected = false;
//            invalidateOptionsMenu();

//          invalidateOptionsMenu();

        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//              String device_id = Constants.ADDR_ID_MAPS.get(address);
            System.out.println("Entry into onREceive - services discovered");
            Constants.DEVICE_MAPS.get(device_id).displayGattServices();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MainActivity.shared().getBleService().exchangeGattMtu(device_id, 512);
            }

            DeviceDetails deviceDetails =
                    Constants.DEVICE_MAPS.get(device_id);

            if(deviceDetails != null){
                deviceDetails.prepareBroadcastDataNotify();
            }

//            if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//                deviceDetails.prepareBroadcastDataNotify();
//                Log.d(TAG, "Prepare Broadcast Data Notification");
//                deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//                Common.wait(1000);
//                deviceDetails.sendData(Common.convertingTobyteArray(Constants.SEND_NOTIF_CMD));
//                deviceDetails.sendData(Common.convertingTobyteArray(Constants.SESSION_CMD));
//            }

//            List <BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
////            activity.displayGattServices(gattServices);
////            System.out.println(gattServices);
        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            String typeOfPacket = "";
            Bundle extras = intent.getExtras();
            if (extras != null) {
                typeOfPacket = extras.getString(BluetoothLeService.TYPE);
            }
            byte[] received_data = intent.getByteArrayExtra(BluetoothLeService.BYTE_VALUES);
            short hr_value = (short)intent.getIntExtra(BluetoothLeService.HR_VALUE, 0);
            if (received_data != null && received_data.length > 0) {
                final String stringBuilder = Common.convertByteArrToStr(received_data, true);
//                final StringBuilder stringBuilder = new StringBuilder(received_data.length);
//                for (byte byteChar : received_data) {
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                }
                if(Constants.isStart) {
                    Log.d(TAG, String.format("%20.3f", System.currentTimeMillis()/1000.0)+", "+device_id.toUpperCase()+"--"+stringBuilder+", heart rate="+hr_value);
                    try{
                        if (device_id.equalsIgnoreCase(DEV_1)){
                            Device1_Parser device1_parser = new Device1_Parser(context);
                            device1_parser.parse(received_data);
                        }else if (device_id.equalsIgnoreCase(DEV_2)){
                            parseDevice2(received_data);
                        }else if (device_id.equalsIgnoreCase(DEV_3)){
                            EventBus.getDefault().post(new HeartRateBelt(hr_value));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            byte batteryLevel = intent.getByteExtra(BluetoothLeService.BT_VALUE,(byte)0);
            System.out.println("Battery level broadcasted"+batteryLevel);
            //activity.invalidateOptionsMenu();
        }
    }

    //Parse sensory data, which received from device, send to FragmentRealtime to update UI and write log file
    private void parseDevice2(byte[] received_data){
        if(received_data.length < 3) {
            return;
        }

        int firstByte = (received_data[1] & 0xFF);
        int secondByte = (received_data[2] & 0xFF);
        int value = 0;
        if ((received_data[0] & 0x01) == 0){
            value  = (int) (firstByte*256);
        }else{
            value  = (int) ((firstByte*256) + secondByte);
        }

        if(Constants.sensoryWatchMap.containsKey(value)){
            SensoryWatch sensoryWatch = Constants.sensoryWatchMap.get(value);
            EventBus.getDefault().post(sensoryWatch);
            Log.d(TAG,"Sensor watch data found, note= "+ ((SensoryWatch) sensoryWatch).english_msg);
        }else{
            Log.d(TAG,"No Sensor watch data found, value="+value);
        }
    }

//    // Parsing the helmet data and
//    private void parseDevice1(Context context, String address, byte[] received_data){
//        // Session Summary
//        // Session summary's header
//        if (received_data[0] == 0xAC && received_data.length >= 2) // for header information
//        {
//            parseSummarySession(context, address, received_data);
//        }
//
//    }
//
//   // Parse Session summary data and
//    private void parseSummarySession(Context context, String address, byte[] received_data){
//        int num_sessions =  received_data[1] & 0xFF ;
//        int packet_size = 4 + 11*num_sessions; // 4==> 2 byte for header, 2 byte for checksum and 11byte per session info
//
//        if(received_data.length < packet_size) {
//            Log.d(TAG, "Invalid session summary data");
//            return ;
//        }
//
//        int checksum = Helper.getIntValue(received_data[packet_size-2], received_data[packet_size - 1]);
//
//        int total = 0;
//        for(int i = 0 ; i <= (packet_size-3) ; i++ ){
//            int val = received_data[i] & 0xFF;
//            total += val;
////            System.out.println("val="+val+", total="+total);
//        }
//        if(checksum != total){
//            System.out.println("Device1: Unmatching checksum = "+checksum+", total ="+total);
//            return;
//        }
//
//        DeviceHelper.SESSION_SUMMARIES.clear();
//        int index = 3;
//        for (int i=0 ; i< num_sessions ; i++){
//            SessionSummary sessionSummary = new SessionSummary();
//
//            sessionSummary.setSession_number((received_data[index] & 0xFF));
//            int number_pages = Helper.getIntValue(received_data[index], received_data[index+1]); // Each page contains 24 pkt
//            int num_packets_lst_page = received_data[index+2] & 0xFF; // number of pkts in last page
//            int total_pkts = number_pages*24 + num_packets_lst_page;
//            sessionSummary.setTotal_pkts(total_pkts);
//            sessionSummary.setTotal_data(total_pkts*80);// 80 bytes per packet
//
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, received_data[index+3] & 0xFF);
//            calendar.set(Calendar.MONTH, received_data[index+4] & 0xFF);
//            int year = received_data[index+5] & 0xFF;
//            int yr =  calendar.get(Calendar.YEAR);
//            yr = yr - yr%100;
//            year = yr + year;
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.HOUR_OF_DAY, received_data[index+6] & 0xFF);
//            calendar.set(Calendar.MINUTE, received_data[index+7] & 0xFF);
//            calendar.set(Calendar.SECOND, received_data[index+8] & 0xFF);
//            int milli_second = received_data[index+9] & 0xFF;
//            calendar.set(Calendar.MILLISECOND, milli_second*10);
//
//            DeviceHelper.SESSION_SUMMARIES.put(sessionSummary.getSession_number(), sessionSummary);
//            index += 11;
//        }
//
//        // send stop cmd to receive session summary
//        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
//
//        if(deviceDetails != null && deviceDetails.mac_address != null && deviceDetails.mac_address.equalsIgnoreCase(address)){
//            deviceDetails.prepareBroadcastDataNotify();
//            deviceDetails.sendData(Common.convertingTobyteArray(Constants.STOP_CMD));
//        }
//    }

    // Parsing the helmet data and
    private void parseDevice1_old(byte[] received_data){

        if( (received_data[0] == 0x14 && received_data.length < 16) || (received_data[0] == 0x15 && received_data.length < 17) ) {
            System.out.print("Incomplete packet");
            return;
        }

        int byte_idx = 1;
        int sensor_start = 0;
        int packet_num = 0;

        if (received_data[0] == 0x14){
            packet_num = received_data[1] & 0xFF ;
            byte_idx = 2;
            // data will contain 1st to 4th sensor's data
        }else if (received_data[0] == 0x15){
            packet_num = Helper.getIntValue(received_data[2], received_data[1]);//val1*256 + val2;
            byte_idx = 3;
            // data will contain 5th to 8th sensor's data
        }
        System.out.println("Packet num="+packet_num);
        if (packet_num%2 ==  0) {
            sensor_start = 0;
        }else{
            sensor_start = 4;
        }

        int checksum = Helper.getIntValue(received_data[byte_idx+13], received_data[byte_idx+12]);

        int total = 0;
        for(int i = 0 ; i <= (byte_idx+11) ; i++ ){
            int val = received_data[i] & 0xFF;
            total += val;
//            System.out.println("val="+val+", total="+total);
        }
        if(checksum != total){
            System.out.println("Unmatching checksum = "+checksum+", total ="+total);
            return;
        }

        Constants.HELMET_DATA.temp[sensor_start]   = Helper.getShortValue(received_data[byte_idx+1], received_data[byte_idx])/10.0f;   // 1st temp
        Constants.HELMET_DATA.temp[sensor_start+1] = Helper.getShortValue(received_data[byte_idx+3], received_data[byte_idx+2])/10.0f; // 2nd temp
        Constants.HELMET_DATA.temp[sensor_start+2] = Helper.getShortValue(received_data[byte_idx+5], received_data[byte_idx+4])/10.0f; // 3rd temp
        Constants.HELMET_DATA.temp[sensor_start+3] = Helper.getShortValue(received_data[byte_idx+7], received_data[byte_idx+6])/10.0f; // 4th temp

        Constants.HELMET_DATA.humdity[sensor_start]   = received_data[byte_idx+8] & 0xFF ;                            // 1st humidity
        Constants.HELMET_DATA.humdity[sensor_start+1] = received_data[byte_idx+9] & 0xFF ;                            // 2nd humidity
        Constants.HELMET_DATA.humdity[sensor_start+2] = received_data[byte_idx+10] & 0xFF ;                           // 3rd humidity
        Constants.HELMET_DATA.humdity[sensor_start+3] = received_data[byte_idx+11] & 0xFF ;                           // 4th humidity

        // Send data to FragmentRealtime to update the UI and write to log file
        EventBus.getDefault().post(Constants.HELMET_DATA);
//        System.out.println("Checksum = "+checksum+", total="+total);
    }

    //convert two byte to integer
    private int getIntValue(byte b1, byte b2){
        int val1 = b1 & 0xFF ;
        int val2 = b2 & 0xFF ;
        return val1*256 + val2;
    }

    //convert two byte to short integer
    private short getShortValue(byte b1, byte b2){
        byte[] temp = {b1, b2};
        return ByteBuffer.wrap(temp).getShort();
    }

}

