package com.decalthon.helmet.stability.activities;
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


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.devicemodels.BleDevice;
import com.decalthon.helmet.stability.model.devicemodels.DeviceDetails;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends AppCompatActivity {
    EditText textBox;
    ListView listView;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private String device_id;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);
//        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
//        getActionBar().setIcon(R.mipmap.back_button_round);
//        getActionBar().setTitle("");
//        getActionBar().getCustomView().setOCn
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
        }

        myToolbar.findViewById(R.id.back_navigation_device_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceScanActivity.this.finish();
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                device_id= null;
            } else {
//                device_id= extras.getString("STRING_I_NEED");
                device_id = extras.getString(Constants.DEVICE_ID);
            }
        } else {
            device_id = (String) savedInstanceState.getString(Constants.DEVICE_ID);
        }

        textBox = (EditText)findViewById(R.id.textBox);
        listView = (ListView)findViewById(R.id.listview);

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listView.setAdapter(mLeDeviceListAdapter);

        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                System.out.println("beforeTextChanged Len = "+s.length());
                mLeDeviceListAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    System.out.println("onTextChanged Len = 0");
                    mLeDeviceListAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                Log.d("DeviceScanActivity ", "device_id = "+device_id+" device name:"+ device.getName() + ", address :" + device.getAddress() );
                //        EventBus.getDefault().post(new BleDevice(device.getAddress()));
                DeviceDetails deviceDetails = new DeviceDetails();;
//                if( Constants.DEVICE_MAPS.containsKey(device_id)) {
//                    deviceDetails = Constants.DEVICE_MAPS.get(device_id);
//                }else {
//                    deviceDetails = new DeviceDetails();
//                }

                if(device.getName() == null){
                    deviceDetails.name = getApplicationContext().getResources().getString(R.string.unknown_device);
                }else {
                    deviceDetails.name = device.getName();
                }

                deviceDetails.mac_address = device.getAddress();
                Constants.DEVICE_MAPS.put(device_id, deviceDetails);
                EventBus.getDefault().post(new BleDevice(device_id, device.getAddress()));
                finish();

                if (mScanning) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }
            }
        });


        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_ble_menu, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

//        Initializes listView view adapter.
//        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
//        if (device == null) return;
////        EventBus.getDefault().post(new BleDevice(device.getAddress()));
//        DeviceDetails deviceDetails;
//        if( Constants.DEVICE_MAPS.containsKey(device_id)) {
//            deviceDetails = Constants.DEVICE_MAPS.get(device_id);
//        }else {
//            deviceDetails = new DeviceDetails();
//        }
//        deviceDetails.mac_address = device.getAddress();
//        Constants.DEVICE_MAPS.put(device_id, deviceDetails);
//
//        EventBus.getDefault().post(new BleDevice(device_id, device.getAddress()));
//        finish();
//
//        if (mScanning) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mScanning = false;
//        }
//        //startActivity(intent);
//    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    //  Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter implements Filterable {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;
        private ArrayList<BluetoothDevice> filteredData = null;
        private ItemFilter mFilter = new ItemFilter();

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            filteredData = new ArrayList<BluetoothDevice>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
            if(!filteredData.contains(device)) {
                filteredData.add(device);
            }
        }

        public boolean contains(BluetoothDevice device){
            if(mLeDevices != null && mLeDevices.contains(device)) {
                return true;
            }
            return false;
        }

        public BluetoothDevice getDevice(int position) {
            return filteredData.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            filteredData.clear();
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public Object getItem(int i) {
            return filteredData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = filteredData.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }


        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final ArrayList<BluetoothDevice> list = mLeDevices;

                int count = list.size();
                final ArrayList<BluetoothDevice> nlist = new ArrayList<BluetoothDevice>(count);


                if(filterString.length() == 0) {
                    for (int i = 0; i < count; i++) {
                        nlist.add(list.get(i));
                    }
                }else {
                    String filterableString ;

                    for (int i = 0; i < count; i++) {
                        filterableString = list.get(i).getName();
                        if (filterableString !=null && filterableString.toLowerCase().contains(filterString)) {
                            nlist.add(list.get(i));
                        }
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
                Log.d("DeviceScanActivity", "list size="+nlist.size());
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if(results.values == null) {
                    Log.d("DeviceScanActivity", "results.values is null");
                }else{
                    filteredData = (ArrayList<BluetoothDevice>) results.values;
                }
                notifyDataSetChanged();
            }
        }

    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mLeDeviceListAdapter.contains(device)){
                                return;
                            }
                            //Toast.makeText(getApplicationContext(), "Found Device " + device.getName() , Toast.LENGTH_SHORT).show();
                            Log.d("DeviceScanActivity", "Found Device " + device.getName());
                            mLeDeviceListAdapter.addDevice(device);
                            mLeDeviceListAdapter.notifyDataSetChanged();

                            Common.wait(50);
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}