/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decalthon.helmet.stability.BLE;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.BLE.gatt_server.BluetoothLeGattServer;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.model.DeviceModels.BatteryLevel;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceData;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import de.greenrobot.event.EventBus;


public class BluetoothLeService extends Service {

    private final String TAG = BluetoothLeService.class.getSimpleName();

    public BluetoothManager mBluetoothManager;
    public BluetoothAdapter mBluetoothAdapter;
    //    public static BluetoothGatt mBluetoothGatt;
    private String mBluetoothDeviceAddress;

    //TODO delete this line
    private long mReceived = 0;

    public static final String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String BYTE_VALUES =
            "com.example.bluetooth.le.BYTE_VALUES";
    public static final String HR_VALUE =
            "com.example.bluetooth.le.HR_VALUE";
    public static final String MAC_ADDRESS =
            "com.example.bluetooth.le.MAC_ADDRESS";
    public static final String BT_VALUE =
            "com.example.bluetooth.le.BT_VALUE";
    public static final String TYPE = "TYPE_OF_PACKET";


    //    public final UUID UUID_HEART_RATE_SERVER =
//            UUID.fromString(SampleGattAttributes.NORDIC_UART_tx);
    public final UUID UUID_HEART_RATE_SERVER =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public final UUID UUID_SERVER_TX =
            UUID.fromString(SampleGattAttributes.NORDIC_UART_tx);
    public final UUID UUID_BATTERY_SERVER =
            UUID.fromString(SampleGattAttributes.BATTERY_LEVEL_MEASUREMENT);
//    public final UUID UUID_BATTERY_SERVER =
//            UUID.fromString(SampleGattAttributes.BATTERY_LEVEL_MEASUREMENT);
    // Implements callback methods for GATT events that the app cares about.  For example,
    // BleDevice change and services discovered.
    private final BluetoothGattCallback mGattCallback1;
    private final BluetoothGattCallback mGattCallback2;
    private final BluetoothGattCallback mGattCallback3;

    MainActivity activity = MainActivity.shared();
    final String DEV_1 = activity.getResources().getString(R.string.device1_tv);
    final String DEV_2 = activity.getResources().getString(R.string.device2_tv);
    final String DEV_3 = activity.getResources().getString(R.string.device3_tv);

    private Map<String, BluetoothGattCallback> devBleCallback = new HashMap<>();
    {
        class myBluetoothCallback extends  BluetoothGattCallback {
            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.d("RSSI", "rssi is : " + rssi);
            }

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = ACTION_GATT_CONNECTED;
//                    synchronized (this) {
//                    }
                    String address = gatt.getDevice().getAddress();
                    broadcastUpdate(address, intentAction);
                    gatt.discoverServices();
                    //broadcastUpdate2(intentAction);
                    Log.i(TAG, "Connected to GATT server.");
                    // Attempts to discover services after successful BleDevice.
//                    Log.i(TAG, "Attempting to start service discovery:" +
//                            mBluetoothGatt.discoverServices());
//                    String device_id = Constants.ADDR_ID_MAPS.get(address);
//                    if (Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt != null) {
//                        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.discoverServices();
//                    }

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = ACTION_GATT_DISCONNECTED;
//                    synchronized (mGattCallback) {
//                    }
                    Log.i(TAG, "Disconnected from GATT server.");
                    broadcastUpdate(gatt.getDevice().getAddress(),intentAction);
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.i(TAG, " onDescriptorWrite: "+Common.convertByteArrToStr(descriptor.getValue(), true) + " status="+status);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    broadcastUpdate(gatt.getDevice().getAddress(), ACTION_GATT_SERVICES_DISCOVERED);
                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                System.out.println("Characteristic Read");
//                broadcastBattery(gatt.getDevice().getAddress(),characteristic);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
                    characteristic, int status) {
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
                //System.out.println("Entering characteristic changed"+characteristic.getUuid());
                broadcastData(gatt.getDevice().getAddress(), characteristic);
            }
        };
        mGattCallback1 = new myBluetoothCallback();
        mGattCallback2 = new myBluetoothCallback();
        mGattCallback3 = new myBluetoothCallback();

        devBleCallback.put(MainActivity.shared().getResources().getString(R.string.device1_tv), mGattCallback1);
        devBleCallback.put(MainActivity.shared().getResources().getString(R.string.device2_tv), mGattCallback2);
        devBleCallback.put(MainActivity.shared().getResources().getString(R.string.device3_tv), mGattCallback3);

//        mGattCallback = new BluetoothGattCallback() {
//            @Override
//            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//                super.onReadRemoteRssi(gatt, rssi, status);
//                Log.d("RSSI", "rssi is : " + rssi);
//            }
//
//            @Override
//            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//                String intentAction;
//                if (newState == BluetoothProfile.STATE_CONNECTED) {
//                    intentAction = ACTION_GATT_CONNECTED;
//                    synchronized (mGattCallback) {
//                    }
//                    String address = gatt.getDevice().getAddress();
//                    broadcastUpdate(address, intentAction);
//                    gatt.discoverServices();
//                    //broadcastUpdate2(intentAction);
//                    Log.i(TAG, "Connected to GATT server.");
//                    // Attempts to discover services after successful BleDevice.
////                    Log.i(TAG, "Attempting to start service discovery:" +
////                            mBluetoothGatt.discoverServices());
////                    String device_id = Constants.ADDR_ID_MAPS.get(address);
////                    if (Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt != null) {
////                        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.discoverServices();
////                    }
//
//                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                    intentAction = ACTION_GATT_DISCONNECTED;
//                    synchronized (mGattCallback) {
//                    }
//                    Log.i(TAG, "Disconnected from GATT server.");
//                    broadcastUpdate(gatt.getDevice().getAddress(),intentAction);
//                }
//            }
//
//            @Override
//            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//                if (status == BluetoothGatt.GATT_SUCCESS) {
//                    broadcastUpdate(gatt.getDevice().getAddress(), ACTION_GATT_SERVICES_DISCOVERED);
//                } else {
//                    Log.w(TAG, "onServicesDiscovered received: " + status);
//                }
//            }
//
//            @Override
//            public void onCharacteristicRead(BluetoothGatt gatt,
//                                             BluetoothGattCharacteristic characteristic,
//                                             int status) {
//            }
//
//            @Override
//            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic
//                    characteristic, int status) {
//            }
//
//            @Override
//            public void onCharacteristicChanged(BluetoothGatt gatt,
//                                                BluetoothGattCharacteristic characteristic) {
//
//                //System.out.println("oncharacteristicChanged called");
//                broadcastData(gatt.getDevice().getAddress(), characteristic);
//            }
//        };
    }

//    private void broadcastBattery( final String address , final BluetoothGattCharacteristic characteristic ) {
//        System.out.println(" inside broadcast battery level " + characteristic.getUuid());
//        String device_name;
//        if(BATTERY_LEVEL_MEASUREMENT.equals(characteristic.getUuid()))
//        {
//            byte[] byte_values = UARTParser.getsensorsdata(characteristic);
////            System.out.println("Battery level " + byte_values[0]);
//            device_name = Constants.ADDR_ID_MAPS.get(address);
//            EventBus.getDefault().post(new BatteryLevel(byte_values[0] , device_name));
//        }
//    }

    private void broadcastUpdate(final String address, final String action) {
        final Intent intent = new Intent(action);
        intent.putExtra(MAC_ADDRESS, address);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void broadcastData(final String address, final BluetoothGattCharacteristic characteristic) {

        //System.out.println(" inside broadcast data   " + characteristic.getUuid());
//        System.out.println(" inside broadcast ble " + UUID_HEART_RATE_SERVER.toString() );

//        final StringBuilder stringBuilder = new StringBuilder(byte_values.length);
//        for(byte byteChar : byte_values)
//            stringBuilder.append(String.format("%02X ", byteChar));
//        Log.d(TAG, "Received Data: "+stringBuilder);

        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//        intent.putExtra(BYTE_VALUES, byte_values);
//        intent.putExtra(MAC_ADDRESS, address);
        if (UUID_HEART_RATE_SERVER.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            byte[] byte_values = UARTParser.getsensorsdata(characteristic);

            int format = -1;
            byte[] tx_value = UARTParser.getsensorsdata(characteristic);
            final StringBuilder stringBuilder = new StringBuilder(tx_value.length);
            for(byte byteChar : tx_value)
                stringBuilder.append(String.format("%02X ", byteChar));
//            Log.d(TAG, "Received Data: "+stringBuilder);
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
//            System.out.println(String.format(address+",  %20.3f", System.currentTimeMillis()/1000.0)+", "+stringBuilder+", heart rate="+heartRate);

//            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(HR_VALUE, heartRate);
            intent.putExtra(BYTE_VALUES, byte_values);
            intent.putExtra(MAC_ADDRESS, address);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }else if (UUID_SERVER_TX.equals(characteristic.getUuid())) {
            byte[] tx_value = UARTParser.getsensorsdata(characteristic);
//            final StringBuilder stringBuilder = new StringBuilder(tx_value.length);
//            for(byte byteChar : tx_value)
//                stringBuilder.append(String.format("%02X ", byteChar));
//            String string = stringBuilder.toString();
//            System.out.println(address+", "+stringBuilder);
            //Log.d(TAG, "broadcastData: checking address and data\n " + address+", "+stringBuilder);

            if(!ConsumerThread.stopProducing) {
                ConsumerThread.DATA_QUEUE.offer(new DeviceData(address, tx_value));
            }
//            else if(string.startsWith("01 00 01") || string.startsWith("09 00 09")){
//                ConsumerThread.stopProducing = false;
//            }
//            intent.putExtra(BYTE_VALUES, tx_value);
//            intent.putExtra(MAC_ADDRESS, address);
//            sendBroadcast(intent);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    // Call method here
//                    parseReceivedData(address,tx_value);
//                }
//            }).start();
        }

        /**
         * If the UUID of the characteristic refers to the battery server,
         * The battery level characteristic is probed for the value.
         * Usually, the battery characteristic, is the first byte of the reading*/

        /**
         * The battery level value is then exported to an event listener,
         * using the {@link EventBus} object*/

        if(UUID_BATTERY_SERVER.equals(characteristic.getUuid()))
        {
            byte [] batteryLevel = UARTParser.getsensorsdata(characteristic);
            int battery_level_value = batteryLevel[0] & 0xFF;
            float battery_percentage_value = ( battery_level_value / 255.0f ) * 100;
            battery_level_value = (int)battery_percentage_value;
            String device_id = Constants.ADDR_ID_MAPS.get(address);
            if(device_id.equals(getString(R.string.device3_tv))){
                EventBus.getDefault().post(new BatteryLevel(batteryLevel[0],
                        device_id));
            }else{
                EventBus.getDefault().post(new BatteryLevel(battery_level_value,
                        device_id));
            }
            Constants.DEVICE_MAPS.get(device_id).stopBroadcastBatteryNotify();

//            intent.putExtra(BT_VALUE, battery_level_value);
//            sendBroadcast(intent);
        }
//        else {
//            // For all other profiles, writes the data formatted in HEX.
//            System.out.println("Entering the last case");
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                Log.d(TAG, "Other Data: "+stringBuilder);
//                intent.putExtra(BYTE_VALUES, new String(data) + "\n" + stringBuilder.toString());
//            }
//        }

    }


    public void  parseReceivedData (String mac_address, byte[] received_data) {
        String device_id = Constants.ADDR_ID_MAPS.get(mac_address);
        if (device_id == null || device_id.length() == 0) {
            return;
        }

        if (received_data != null && received_data.length > 0) {

//            final String stringBuilder = Common.convertByteArrToStr(received_data, true);
            //                final StringBuilder stringBuilder = new StringBuilder(received_data.length);
            //                for (byte byteChar : received_data) {
            //                    stringBuilder.appstopend(String.format("%02X ", byteChar));
            //                }
//            if (Constants.isStart) {
//                Log.d(TAG, String.format("%20.3f", System.currentTimeMillis() / 1000.0) + ", " + device_id.toUpperCase() + "--" + stringBuilder); // + ", heart rate=" + hr_value);
                try {
                    if (device_id.equalsIgnoreCase(DEV_1)) {
                        Device1_Parser device1_parser = new Device1_Parser(getApplicationContext(), device_id, mac_address);
                        device1_parser.parse(received_data);
                    }else if (device_id.equalsIgnoreCase(DEV_2)) {
                        //parseDevice2(received_data);
                    } else if (device_id.equalsIgnoreCase(DEV_3)) {
                        //EventBus.getDefault().post(new HeartRateBelt(hr_value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        }
//    byte batteryLevel = intent.getByteExtra(BluetoothLeService.BT_VALUE,(byte)0);
//            System.out.println("Battery level broadcasted"+batteryLevel);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        for (Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet()){
            close(entry.getKey());
        }

        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param device_id The device details of the destination device. e.g. bluetoothgatt, address etc.
     *
     * @return Return true if the BleDevice is initiated successfully. The BleDevice result
    `   *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String device_id) {
        if (mBluetoothAdapter == null || Constants.DEVICE_MAPS.get(device_id).mac_address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        boolean state = false;

        //  Previously connected device.  Try to reconnect.
        if (Constants.DEVICE_MAPS.get(device_id).mac_address != null && Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for BleDevice.");
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//            state = Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.connect();
            if(!Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.connect()){
                Log.d(TAG, "Trying to connect but connection stat is false");
//                Constants.DEVICE_MAPS.get(device_id).num_delay = Constants.RECONNECTION_TEST - 2;
                return false;
            }
//            Constants.DEVICE_MAPS.get(device_id).num_delay = 0;
            return true;
        }

        if( Constants.DEVICE_MAPS.get(device_id).connected == false) {
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(Constants.DEVICE_MAPS.get(device_id).mac_address);

            if (device == null) {
                Log.w(TAG, "Device("+device_id+" : "+Constants.DEVICE_MAPS.get(device_id).mac_address+") not found.  Unable to connect.");
                return false;
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt = device.connectGatt(this, false, devBleCallback.get(device_id)); //disabling auto connect

            Log.d(TAG, "Trying to create a new BleDevice.");
        }

//        mBluetoothDeviceAddress = address;
        return true;
    }

    public boolean createBond(String mac_addr){
        boolean isBonded = false;
        if (mBluetoothAdapter != null){
            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mac_addr);
            isBonded = device.createBond();
        }
        return  isBonded;
    }

    /**
     * Disconnects an existing BleDevice or cancel a pending BleDevice. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(String device_id) {
        if (mBluetoothAdapter == null || Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.disconnect();

//        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt = null;
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close(String device_id) {
        if (Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt == null) {
            return;
        }
        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.close();
        Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt = null;
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public  void setCharacteristicNotification(String device_id, BluetoothGattCharacteristic characteristic,
                                               boolean enabled) {
        System.out.println("In char notification UUID::"+characteristic.getUuid());
        if (mBluetoothAdapter == null || Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized"+characteristic.getUuid());
            return;
        }

        //This is specific to DARE DATA.
        if (UUID_HEART_RATE_SERVER.equals(characteristic.getUuid()) || UUID_SERVER_TX.equals(characteristic.getUuid())
        || UUID_BATTERY_SERVER.equals(characteristic.getUuid()))
        {
            Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a listView of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
//    public List<BluetoothGattService> getSupportedGattServices() {
//        if (mBluetoothGatt == null) return null;
//
//        return mBluetoothGatt.getServices();
//    }
    /*
     * Request a write with no response on a given
     * {@code BluetoothGattCharacteristic}.
     *
     * @param characteristic
     * @param byteArray      to write
     * */
    public  boolean writeCharacteristicNoresponse(
            String device_id, BluetoothGattCharacteristic characteristic, byte[] byteArray) {
        if (mBluetoothAdapter == null || Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt == null || characteristic == null) {
            return false;
        } else {
            byte[] valueByte = byteArray;
            if (valueByte != null) {
                characteristic.setValue(valueByte);
                return Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.writeCharacteristic(characteristic);
            }
        }
        return false;
    }

    public  void exchangeGattMtu(String device_id, int mtu) {
        if(Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt == null){
            return;
        }
        int retry = 5;
        boolean status = false;
        while (!status && retry > 0) {
            Common.wait(160);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                status = Constants.DEVICE_MAPS.get(device_id).mBluetoothGatt.requestMtu(mtu);
            retry--;
        }
    }

}
