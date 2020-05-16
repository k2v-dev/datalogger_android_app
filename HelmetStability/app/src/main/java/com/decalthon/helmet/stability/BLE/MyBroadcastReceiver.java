package com.decalthon.helmet.stability.ble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.devicemodels.BLEConnectionState;
import com.decalthon.helmet.stability.model.devicemodels.DeviceDetails;


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
//                if(Constants.isStart) {
//                    Log.d(TAG, String.format("%20.3f", System.currentTimeMillis()/1000.0)+", "+device_id.toUpperCase()+"--"+stringBuilder+", heart rate="+hr_value);
//                    try{
//                        if (device_id.equalsIgnoreCase(DEV_1)){
//                            Device1_Parser device1_parser = new Device1_Parser(context);
//                            device1_parser.parse(received_data);
//                        }else if (device_id.equalsIgnoreCase(DEV_2)){
//                            //parseDevice2(received_data);
//                        }else if (device_id.equalsIgnoreCase(DEV_3)){
//                            EventBus.getDefault().post(new HeartRateBelt(hr_value));
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
            }
            byte batteryLevel = intent.getByteExtra(BluetoothLeService.BT_VALUE,(byte)0);
            System.out.println("Battery level broadcasted"+batteryLevel);
            //activity.invalidateOptionsMenu();
        }
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

