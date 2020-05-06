package com.decalthon.helmet.stability.model.DeviceModels;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.BLE.SampleGattAttributes;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/*
   DeviceDetails which contains device's details, bluetooth's properties and method for enable notification
 */

public class DeviceDetails {
    private static final String TAG = DeviceDetails.class.getSimpleName();

    public String name; // Device name
    public String mac_address; // Mac address
    public boolean connected; // connection status of bluetooth device
    public short num_delay = 0; // For reconnection timeout
    public BluetoothGatt mBluetoothGatt = null;
    public BluetoothGattCharacteristic mNotifyCharacteristic = null;
    public BluetoothGattCharacteristic mWriteCharacteristic = null;
    public BluetoothGattCharacteristic mReadBatteryCharacteristic = null ;
    public boolean noAutoconnect; // It will be use when no reconnection require e.g. disconnectted the device by user
    public long total_pkts;
    public long num_pkts_rcvd;
    public long read_pkts;


    /**
     * The default constructor
     */

    public DeviceDetails() {
        noAutoconnect = false;
        total_pkts = 0;
        num_pkts_rcvd = 0;
    }

    /**
     * List all the GATT services of the bluetooth service
     * Filter the required characteristic.
     * This method is called on service discovery.
     */

    public void displayGattServices(){

        /**Check if there is an existing bluetooth profile,
         * which has services advertised
         * If no services are advertised, no services to display
         * */

        if(mBluetoothGatt == null){
            return;
        }

        /**
         * Place the services exposed by the GATT server in a list
         * If there is no element in the list, connections are made
         * with one or more profiles, no services are listed in any profile.
         * */

        List<BluetoothGattService> gattServices = mBluetoothGatt.getServices();
        if (gattServices == null) return;

        /**
         * Among the services exposed by the GATT server(s),
         * choose the desired characteristic exposed by the service
         */
        String uuid;
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            if (uuid.equals(SampleGattAttributes.SERVER_HEART_RATE)) {
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
//                    mReadCharacteristic = gattCharacteristic;
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.HEART_RATE_MEASUREMENT)) {
                        Log.e("data ", "read characteristics " + gattCharacteristic.getUuid().toString());
                        mNotifyCharacteristic = gattCharacteristic;
                    }
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.NORDIC_UART_rx)){
                        Log.e("data ", "write characteristics " + gattCharacteristic.getUuid().toString());
                        mWriteCharacteristic = gattCharacteristic;
                    }
                    if(uuidchara.equalsIgnoreCase(SampleGattAttributes.BATTERY_LEVEL_MEASUREMENT)) {
                        mReadBatteryCharacteristic = gattCharacteristic;
                        System.out.println("Notify battery characteristic set");
                    }

                }
            }else  if (uuid.equals(SampleGattAttributes.NORDIC_SERVER_UART)) { //it's for oblu device only, ignore this block
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
//                    mWriteCharacteristic = gattCharacteristic;
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.NORDIC_UART_tx)) {
                        Log.e("data ", "read characteristics " + gattCharacteristic);
                        mNotifyCharacteristic = gattCharacteristic;
                        //preparebroadcastdatanotify(mac_address, mNotifyCharacteristic);
                    }
                    if (uuidchara.equalsIgnoreCase(SampleGattAttributes.NORDIC_UART_rx)){
                        Log.e("data ", "write characteristics " + gattCharacteristic);
                        mWriteCharacteristic = gattCharacteristic;
                    }

                }
            }


            /**
             * Check for the battery service and battery level characteristic.
             * Set the required GATT characteristic to BATTERY_LEVEL_MEASUREMENT,
             * among any other battery characteristics
             * */
//TODO check when battery service is appicable

            if(uuid.equals(SampleGattAttributes.SERVER_BATTERY)) {
                Log.d("server battery","server battery checked for battery level measurement");
                for(BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    String uuidchara = gattCharacteristic.getUuid().toString();
                    if(uuidchara.equalsIgnoreCase(SampleGattAttributes.BATTERY_LEVEL_MEASUREMENT)) {
                        mReadBatteryCharacteristic = gattCharacteristic;
                        System.out.println("Notify battery characteristic set"+gattCharacteristic.getUuid().toString());
                    }
                }
            }
        }
    }

    /**
     * Enable notifications for receiving data from heart rate server
     * */

    public void prepareBroadcastDataNotify() {

        if(mNotifyCharacteristic == null || mac_address == null || mac_address.isEmpty()){
            Log.d("DeviceDetails", "Not mNotifyCharacteristic found or mac address is empty ");
            return;
        }
        Common.wait(500);
        if ((mNotifyCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            MainActivity.shared().getBleService().setCharacteristicNotification(Constants.ADDR_ID_MAPS.get(mac_address), mNotifyCharacteristic, true);
//            System.out.println("************notificatoin  " + gattCharacteristic.getUuid());
            // BluetoothLeService.check();
            Common.wait(200);
        }
    }


    /**
     * Enable notifications for the battery service (to read battery level)
     */

    public void prepareBroadcastBatteryNotify() {
        if(mReadBatteryCharacteristic != null) {
            System.out.println(mReadBatteryCharacteristic.getUuid() + "mreadnow" + "macaddress" + mac_address);
        }
        if(mReadBatteryCharacteristic == null || mac_address == null || mac_address.isEmpty()){
            return;
        }
        Common.wait(500);

        if ((mReadBatteryCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//            System.out.println((new Date()).toString() + "Preparing battery broadcast");
            MainActivity.shared().getBleService().setCharacteristicNotification(Constants.ADDR_ID_MAPS.get(mac_address), mReadBatteryCharacteristic, true);
        }
//            mBluetoothGatt.readCharacteristic(mReadBatteryCharacteristic);
    }

    /**
     * The stopBroadcastBatteryNotify method
     * (a).Checks if the battery read characteristic is enabled
     * (b).If notifications are enabled for the battery level property, then notifications are
     * disabled
     * */


    public void stopBroadcastBatteryNotify() {
        Log.d("Battery level","stopping notifications");
        if (mReadBatteryCharacteristic == null || mac_address == null || mac_address.isEmpty()) {
                  Log.d("DeviceDetails", "Not mNotifyCharacteristic found or mac address is empty ");  this.mReadBatteryCharacteristic = null;
            return;
        }
        Common.wait(500);
        if ((mReadBatteryCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            MainActivity.shared().getBleService().setCharacteristicNotification(Constants.ADDR_ID_MAPS.get(mac_address), mReadBatteryCharacteristic, false);
            Common.wait(200);
        }
    }

    /**
     * The stopBroadcastDataNotify method
     * (a).Checks if the generic notify characteristic is enabled(for heart rate service)
     * (b).If notifications are enabled, then the notify characteristic is disabled.
     * */

    public void stopBroadcastDataNotify() {
//        System.out.println("am i in prepareBroadcastDataNotify");
        if(mNotifyCharacteristic == null || mac_address == null || mac_address.isEmpty()){
            Log.d("DeviceDetails", "Not mNotifyCharacteristic found or mac address is empty ");
            return;
        }
        Common.wait(500);
        if ((mNotifyCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            MainActivity.shared().getBleService().setCharacteristicNotification(Constants.ADDR_ID_MAPS.get(mac_address), mNotifyCharacteristic, false);
//            System.out.println("************notificatoin  " + gattCharacteristic.getUuid());
            // BluetoothLeService.check();
            Common.wait(200);
        }
    }

    // Send data / command to bluetooth device, ignore this method . It's not part of requirement.
    public boolean sendData(byte[] data){
        if(mWriteCharacteristic == null || mac_address == null || mac_address.isEmpty()){
            Log.d("DeviceDetails", "No mWriteCharacteristic found or mac address is empty ");
            return false;
        }

        Common.wait(300);
        boolean result = MainActivity.shared().getBleService().writeCharacteristicNoresponse(Constants.ADDR_ID_MAPS.get(mac_address), mWriteCharacteristic, data);
//       while(!result){
//            Log.d(TAG, "Send data to BLE : "+result);
//            refresh();
//            Common.wait(500);
//            result = MainActivity.shared().getBleService().writeCharacteristicNoresponse(Constants.ADDR_ID_MAPS.get(mac_address), mWriteCharacteristic, data);
//       }
        Log.d(TAG, "Send data("+Common.convertByteArrToStr(data, true)+") to BLE : "+result);
        return result;
    }

    public float readData(){

        long num_pkts = num_pkts_rcvd - read_pkts;
        long total = total_pkts - read_pkts-1;
        if(total < 1 ){
            return 0;
        }
        Log.d(TAG, "num_pkts_rcvd="+num_pkts_rcvd+", total_pkts="+total_pkts);
        return (((float)num_pkts)*100.0f/((float)total));
    }

    public void refresh(){
        if(mBluetoothGatt == null){
            return;
        }
        try {
            // BluetoothGatt gatt
            final Method refresh = mBluetoothGatt.getClass().getMethod("refresh");
            if (refresh != null) {
                refresh.invoke(mBluetoothGatt);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
    /**
    * reset all connections and characteristics
    * */

    public void clear() {
       Runnable runnable = new Runnable() {
           public void run() {
//               Common.wait(100);
               connected = false;
               if(mBluetoothGatt != null) mBluetoothGatt.close();
               mBluetoothGatt = null;
               mNotifyCharacteristic = null;
               mWriteCharacteristic = null;
               mReadBatteryCharacteristic = null;
           }
       };

       new Thread(runnable).start();

    }
}
