package com.decalthon.helmet.stability.Fragments;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.Activities.DeviceScanActivity;
import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.model.DeviceModels.BLEConnectionState;
import com.decalthon.helmet.stability.model.DeviceModels.BatteryLevel;
import com.decalthon.helmet.stability.model.DeviceModels.BleDevice;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.preferences.DevicePreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * The DeviceFragment class defines properties and methods for connection/disconnection,
 * battery level monitoring and auto-connection of preferred devices
 */
public class DeviceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DeviceFragment";

    //Handler declarations within this fragment
    private Handler progressBarHandler = new Handler();
    private Handler batteryEnableHandler = new Handler();
    private Handler connectionChkHandler = new Handler();
    private Handler scanningHandler = new Handler();

    //Flag declarations within this fragment
    private boolean mScanning = true;
    private boolean isStartCall = false;
    private boolean bConnectionChk = false;

    //Maps used within this fragment
    private Map<String, Boolean>  connect_req = new HashMap<>();
    private Map<String, ViewList> viewUIMaps;

    // TODO: Rename and change types of parameters

    private String mParam1;
    private String mParam2;

    //Fragment views used throughout the scope of this class
    View view;
    private CircleProgressView disconnect;
//    private ImageView  disconnect;

    //Event bus instance use for onEvent actions
    private EventBus mBus = EventBus.getDefault();

    private OnFragmentInteractionListener mListener;

    //Other fields and device-specific parameters,
    private short total_time = 0;
    private String DEV_1, DEV_2, DEV_3;
    private ProgressBar[] progressBar = new ProgressBar[Constants.NUM_DEVICE];
    private final int[] sessionDataVolume = new int[Constants.NUM_DEVICE];

    //A BLE device list adapter instance
    private BluetoothAdapter mBluetoothAdapter;

    //Just for now, inside the constructor, later can be part of
    //device details

    public DeviceFragment() {
        // Required empty public constructor
        sessionDataVolume[0] = 100;
        sessionDataVolume[1] = 100;
        sessionDataVolume[2] = 100;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceFragment.
     */






    // TODO: Rename and change types and number of parameters
    public static DeviceFragment newInstance(String param1, String param2) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Once the fragment is created, the connection request of each device is flagged as false.
     *
     * Device keys are defined based on the order rendered in the UI.
     * By flagging connection requests as false,device keys are associated with a default connection
     * status of false/disconnected.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: start");
        DEV_1 = getResources().getString(R.string.device1_tv);
        DEV_2 = getResources().getString(R.string.device2_tv);
        DEV_3 = getResources().getString(R.string.device3_tv);
        connect_req.put(DEV_1, false);
        connect_req.put(DEV_2, false);
        connect_req.put(DEV_3, false);
//        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
//            System.out.println("Device prefs-->"+ DevicePreferences.getInstance(getContext())
//                    .getName(entry.getKey())+ " --- " +
//                    DevicePreferences.getInstance(getContext()).getAddr(entry.getKey()));
//        }
        Log.d(TAG,"Create");
    }

    /**
     * Once the device keys are defined, with individual connection status as false, the fragment
     * UI is inflated, and view-specific initialization is executed.
     *
     * The custom action bar (from the Activity) is modified for displaying BLE connection
     * and specific icons to connect and disconnect devices.
     *
     * Without any initial delay, device connections are checked using the updateTimerThread
     * {@link DeviceFragment#updateTimerThread}
     *
     * @param inflater The layout inflater associated with the fragment
     * @param container The parent view that holds the fragment
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        view = inflater.inflate(R.layout.fragment_device, container, false);

        initialization(view);

        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();

        if (actionBar != null) {

            actionBar.getCustomView().findViewById(R.id.gps_session_start_btn)
            .setVisibility(View.GONE);

            actionBar.getCustomView().findViewById(R.id.profile_link)
                    .setVisibility(View.GONE);

            actionBar.getCustomView().findViewById(R.id.logout_link)
                    .setVisibility(View.GONE);

            TextView title = actionBar.getCustomView().findViewById(R.id.title_text);
            title.setText(R.string.device_view_title);

            title.setVisibility(View.VISIBLE);

            CircleProgressView progressView
            = actionBar.getCustomView().findViewById(R.id.ble_device_connectivity);

            progressView.setVisibility(View.GONE);

            CircleImageView disconnect = actionBar.getCustomView().findViewById
                    (R.id.disconnect_btn);
            disconnect.setVisibility(View.VISIBLE);

            CircleImageView backLink = actionBar.getCustomView().findViewById(R.id.back_link);

            backLink.setVisibility(View.VISIBLE);

            backLink.setOnClickListener(v -> MainActivity.shared().onBackPressed());

            disconnect.setOnClickListener(v -> disconnectAll());

        }

//        disconnect.setImageResource(R.drawable.ic_disconnect_24dp);
//         Button test_btn = view.findViewById(R.id.test_btn);
//
//        test_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Constants.DEVICE_MAPS.get(getString(R.string.device1_tv)).sendData(Common.convertingTobyteArray(Constants.STOP_CMD));;
//            }
//        });

        Constants.isStart = true;

        connectionChkHandler.postDelayed(updateTimerThread, 0);

        total_time = 0;

        return view;
    }

    /**
     * Once the activity has been created, the DeviceFragment instance takes the following
     * steps:'
     * 1. Register the EventBus that releases with class-specific OnEvent callbacks.
     *
     * 2. Register the Fragment's Bluetooth Adapter from the list of system services
     *    available for the MainActivity
     *
     * 3. If the bluetooth adapter is not enabled an enable bluetooth request is placed
     *    using an Intent.
     *
     * 4. Reconnections are initiated for known, preferred devices.
     * {@link DeviceFragment#startReconnection()}
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG,"OnActivityCreated");
        super.onActivityCreated(savedInstanceState);
        registerAndCheck(this);

        // Get BluetoothManager for getting bluetoothAdapter object
        final BluetoothManager bluetoothManager =
                (BluetoothManager) MainActivity.shared().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported o the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();

        }
        if (!((MainActivity)getActivity()).mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
            return ;

        }

        startReconnection();

    }

    /**
     * Once the view of the device fragment is created, the device detail objects
     * of each device are used to update the inner views
     *
     * Once the views are updated, the {@link DeviceFragment#batteryEnableHandler} is used
     * to read the values of the battery level characteristic of connected devices.
     * @param view
     * @param savedInstanceState
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"ViewCreated");
        super.onViewCreated(view, savedInstanceState);
        //First get all device ids
        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ) {
            DeviceDetails deviceDetail = Constants.DEVICE_MAPS.get(entry.getKey());
            if (deviceDetail != null && deviceDetail.connected) {
                viewUIMaps.get(entry.getKey()).scan_image.setImageResource(R.drawable.connected);
                viewUIMaps.get(entry.getKey()).address_tv.setText(deviceDetail.mac_address);
                viewUIMaps.get(entry.getKey()).name_tv.setText(deviceDetail.name);
                viewUIMaps.get(entry.getKey()).progressBar.setProgress(0);
            }

//            int[] progressStatus;
//
//            progressBarUpdateHandler.postnew Runnable() {
//                @Override
//                public void run() {
//                    while (progressStatus[0] < 100) {
//                        // Update the progress status
//                        progressStatus[0] += 1;
//
//                        // Try to sleep the thread for 20 milliseconds
//                        try {
//                            Thread.sleep(20);  //3 seconds
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                        // Update the progress bar
//                        progressBarUpdateHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressBar[0].setProgress(progressStatus[0]);
//                                progressBar[1].setProgress(progressStatus[0]);
//                                progressBar[2].setProgress(progressStatus[0]);
//                                // Show the progress on TextView
//                            }
//                        });
//                    }
//                }
//            }),2000;
//        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(progressStatus[0] > 100){
//                    progressStatus[0] = 0;
//                }
//                while (progressStatus[0] < 100) {
//                    // Update the progress status
//                    progressStatus[0] += 1;
//
//                    // Try to sleep the thread for 20 milliseconds
//                    try {
//                        Thread.sleep(20);  //3 seconds
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    // Update the progress bar
//                    progressBarUpdateHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            progressBar[0].setProgress(progressStatus[0]);
//                            progressBar[1].setProgress(progressStatus[0]);
//                            progressBar[2].setProgress(progressStatus[0]);
//                            // Show the progress on TextView
//                        }
//                    });
//                }
//            }
//        }).start();
    }

//        progressBarUpdateHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Thread called atleast once");
//                if(progressBar[0].getProgress() > 100){
//                    progressBar[0].setProgress(0);
//                }
//                progressBar[0].incrementProgressBy(1);
//            }
//        },200);
        batteryEnableHandler.postDelayed( enableBatteryReadThread,0);
        //Second get the target viewlist based on the device ID
    }

    /**
     * Each time the fragment starts, if any autoconnections are required,
     * the {@link DeviceFragment#updateTimerThread} is used for reconnecting
     */
    @Override
    public void onStart() {
        Log.d(TAG,"Start");
        super.onStart();

        System.out.println("DeviceFragment:: onStart");
        isStartCall = true;
        startCallback();

        boolean bNeed = false ;
        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
            if(!entry.getValue().noAutoconnect){
                bNeed = true;
            }
        }
        if (bNeed) {
            connectionChkHandler.postDelayed(updateTimerThread, 1000);
        }
        total_time = 1;

//        if(mBus != null){
//            mBus.register(this);
//        }
//        MapActivity.shared().changed_menu();
//        MapActivity.shared().invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        Log.d(TAG,"OnStop");
        super.onStop();
        connectionChkHandler.removeCallbacks(updateTimerThread);

//        if(mBus != null){
//            mBus.unregister(this);
//        }
    }

    @Override
    public void onResume() {
        Log.d(TAG,"OnResume");
        super.onResume();

    }

    @Override
    public void onDetach() {
        Log.d(TAG,"OnDetach");
        super.onDetach();
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.getCustomView().findViewById(R.id.disconnect_btn).setVisibility(View.GONE);
        }
        mListener = null;
    }

    /**
     * Start reconnection service to reconnect all disconnected device.
     */
    private void startCallback(){
        Log.d(TAG,"Starting Reconnection Callback");
        // Request user to enable bluetooth if bluetooth is disable
        if (!((MainActivity)getActivity()).mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        }
        // if user forcefully disconnected all device, then no need to call reconnection service.
//        if (bConnectionChk) {
        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
            Constants.DEVICE_MAPS.get(entry.getKey()).noAutoconnect = false;
        }
        connectionChkHandler.postDelayed(updateTimerThread, 1000);

//        }
        total_time = 1;
        //
        boolean bNeed = true ;
        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
            if(!entry.getValue().connected){
                bNeed = false;
                entry.getValue().stopBroadcastBatteryNotify();
            }else{
                connect_req.put(entry.getKey(), true);
            }
        }

        startReconnection();
    }

    /**
     * 1. Reconnections are attempted with previously saved devices.
     * 2. The connection state {@link DeviceFragment#connect_req} is used as a lookup table
     * 3. For each device connected, the the user's device preferences are checked for existing
     *    saved device details.
     * 4. If there is no device "address" preference for the device key in
     *    {@link DeviceFragment#connect_req}, the need for reconnection remains false, and a
     *    connection request is flagged as true.
     * 5. If there is a saved device mac address, but the device is not connected, it indicates
     *    that a reconnection is allowed. The {@link DeviceFragment#scanAndReconnectThread} is
     *    scheduled after 2 seconds to achieve the reconnection.
     */
    private void startReconnection(){

        Log.d(TAG,"Starting Reconnections");
        if (mBluetoothAdapter == null) {
            return;
        }
        DevicePreferences devpref = DevicePreferences.getInstance(getContext());

        boolean bNeedReconnection = false ;

        for(Map.Entry<String, Boolean> entry : connect_req.entrySet()){
            if(devpref.getAddr(entry.getKey()).length() == 0){
                connect_req.put(entry.getKey(), true);
                continue;
            }else if(!entry.getValue()){
                bNeedReconnection = true;
            }
        }

        if(bNeedReconnection){
            //Reconnect the previously connected device

            scanningHandler.postDelayed(scanAndReconnectThread, 2000);
        }

    }


    /**
     * This thread is called when a reconnection request is placed for saved device.
     *
     * If scanning is under progress, the the adapter can retrieve bonded BLE devices.
     * {@link DeviceFragment#onActivityCreated(Bundle)}
     *
     * This thread is used for scanning nearby devices. If found match device, which were used in
     * last experiment. It will request for reconnection this matched device.
     *
     * First time, it will scan near by device for about 5 second and then stop scanning for 20
     * second. This thread will be executed until all matched devices not found.
     */
    private Runnable scanAndReconnectThread = new Runnable() {
        @Override
        public void run() {
            if(mScanning){
                Log.d(TAG,"Scanning nearby");
                MainActivity.shared().mBluetoothAdapter.getBondedDevices();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                scanningHandler.postDelayed(scanAndReconnectThread, 5000);
            }else{
                Log.d(TAG,"Stopping scan");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                // Check whether need re-scanning is required or not
                boolean bNeedReconnection = false;
                for(Map.Entry<String, Boolean> entry : connect_req.entrySet()){
                    System.out.println(entry.getKey());
                    if(!entry.getValue()){
                        bNeedReconnection = true;
                    }
                }
                // if re-scanning is required, then rescanning will be happened after 20 second.
                // otherwise stop scanning for forever
                if(bNeedReconnection){
                    Log.d(TAG,"Scan postdelayed , 20s");
                    scanningHandler.postDelayed(scanAndReconnectThread, 20000);
                }else{
                    Log.d(TAG,"Scan not required, All done");
                    scanningHandler.removeCallbacks(scanAndReconnectThread);
                    mScanning = true;
                }
            }
            mScanning = !mScanning;
        }
    };

    /**
     * Scan near by devices. If matching devices, which used in recent experiment, store in memory
     * for reconnection service.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    Log.d(TAG,"OnLEScan");
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            String name = device.getName();
                            if(name == null){
                                return;
                            }
//                            else{
//                                boolean bFound = false;
//                                // Iterated all devices, filter out given devices from nearby devices
//                                outerloop:
//                                for( Map.Entry<String, List<String>> entry : Constants.SENSORS_MAPS.entrySet()) {
//                                    for (String sensor : entry.getValue()) {
//                                        if(name.toUpperCase().contains(sensor.toUpperCase())){
//                                            bFound = true;
//                                            break outerloop;
//                                        }
//                                    }
//                                }
//                                if (!bFound){
//                                    return;
//                                }
//                            }
                            //Get the last used device
                            System.out.println("Checking addresses"+device.getAddress());
                            filteredDevices(device.getName(), device.getAddress());

                            //Toast.makeText(getApplicationContext(), "Found Device " + device.getName(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Background scanning: Found Device " + device.getName());
                            Common.wait(50);
                        }
                    };

                    new Thread(runnable).start();

                }
            };

    /**
     * Disconnect all devices
     */
    private void disconnect_devices(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                connectionChkHandler.removeCallbacks(updateTimerThread);
                batteryEnableHandler.removeCallbacks(enableBatteryReadThread);
                for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet()){
                    Constants.DEVICE_MAPS.get(entry.getKey()).noAutoconnect = true;
                    if (entry.getValue().mBluetoothGatt != null){
                        MainActivity.shared().getBleService().disconnect(entry.getKey());
                        Common.wait(500);
                    }
                    entry.getValue().stopBroadcastBatteryNotify();
//                    if (!entry.getValue().connected) {
//                        Constants.DEVICE_MAPS.get(entry.getKey()).clear();
//                    }
                }
                bConnectionChk = false;
            }
        };
        new Thread(runnable).start();
    }




    /**
     * Based on connection status retrieved from the eventBus, UI changes are made for the
     * specific device_id
     * @param device_id
     * @param isConnected
     */
    private void updateUI(final String device_id , final boolean isConnected){
        try {
            MainActivity.shared().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("updateUI");
                    Common.wait(500);
                    DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(device_id);
                    if(viewUIMaps != null)
                        if (isConnected && deviceDetails.mBluetoothGatt != null) {
                            viewUIMaps.get(device_id).name_tv.setText(deviceDetails.name);
                            viewUIMaps.get(device_id).address_tv.setText(deviceDetails.mac_address);
                            viewUIMaps.get(device_id).battery_icon.setVisibility(View.VISIBLE);
                            DevicePreferences.getInstance(getContext()).saveName(device_id, deviceDetails.name);
                            DevicePreferences.getInstance(getContext()).saveAddr(device_id, deviceDetails.mac_address);


                            //TODO uncomment this line when data is available
                            //                    viewUIMaps.get(device_id).progressBar.incrementProgressBy((data_received/total_data)/100);
                            connect_req.put(device_id, true);
                        } else {
                            viewUIMaps.get(device_id).name_tv.setText("");
                            viewUIMaps.get(device_id).address_tv.setText("");
                            viewUIMaps.get(device_id).battery_icon.setVisibility(View.GONE);
                            viewUIMaps.get(device_id).battery_percent_tv.setText("");
                        }
                }
            });
        }catch (NullPointerException ne){
            System.out.println("Unable to update UI, reporting this fix");
        }
    }

    // Disconnect all connected device. It will be called whene press "Disconnect all" button on menu bar.
    public void disconnectAll() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Alert")
                .setMessage("  Do you want disconnect all device?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void  onClick(DialogInterface arg0, int arg1) {
//                                            ((MapActivity)getActivity()).mConnected = false;
//                        ProgressIndicator pb = new ProgressIndicator(getActivity());
//                        pb.show("Disconnection", "Wait for disconnection the devices.");
//                        connectionChkHandler.removeCallbacks(updateTimerThread);
//                        batteryEnableHandler.removeCallbacks(enableBatteryReadThread);
//                        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet()){
//                            Constants.DEVICE_MAPS.get(entry.getKey()).noAutoconnect = true;
//                            if (entry.getValue().mBluetoothGatt != null){
//
//                                MainActivity.shared().getBleService().disconnect(entry.getKey());
//                                Common.wait(500);
//                            }
//                        }

                        disconnect_devices();



//                        disconnect.setImageResource(R.mipmap.ic_bluetooth_devices);

//                        Common.wait(3000);
//                        if (mBluetoothAdapter != null){
//                            mBluetoothAdapter.disable();
//                            Common.wait(100);
//                            mBluetoothAdapter.enable();
//                        }
                    }
                }).create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1B5AAC")); // Set text color to blue color
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D3D3D3"));  // Set text color to ligh gray color

        // Get the alert dialog buttons reference
//        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        // Change the alert dialog buttons text and background color
//        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
//        positiveButton.setTextColor(Color.parseColor("#FF1B5AAC"));
//        positiveButton.setBackgroundColor(Color.parseColor("#FFE1FCEA"));

//        negativeButton.setTextColor(Color.parseColor("#FFFF0400"));
//        negativeButton.setBackgroundColor(Color.parseColor("#FFFCB9B7"));

//        negativeButton.setTextColor(Color.parseColor("#D3D3D3"));
//        negativeButton.setBackgroundColor(Color.parseColor("#F6F6F6"));
    }


    /**
     * This thread enables notification for battery level reads, once in every five minutes.
     */

    private Runnable enableBatteryReadThread = new Runnable() {

        public void run()
        {
            for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ) {
                if(Constants.DEVICE_MAPS.get(entry.getKey()) != null){
                    Constants.DEVICE_MAPS.get(entry.getKey()).prepareBroadcastBatteryNotify();
                    System.out.println(entry.getKey() + "called prepare broadcast battery notify");
                }
            }
            batteryEnableHandler.postDelayed( this, Constants.BATTERY_LEVEL_INTERVAL);
        }
    };

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            Log.d(TAG, "run: Update timer thread");
            String loadStr = new Date().toString();
            bConnectionChk = false;
            for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
                if ( entry.getValue() != null ){
                    loadStr += " Device: "+ entry.getKey() + ", Connection state: "+ (entry.getValue().connected?"True":"False");
                    if (entry.getValue().connected == false && !entry.getValue().noAutoconnect) {
                        if(entry.getValue().num_delay >= Constants.RECONNECTION_TEST){
                            Log.d(TAG, "Reconnect request-"+entry.getKey()+", device addr="+entry.getValue().mac_address);
                            MainActivity.shared().getBleService().connect(entry.getKey());
                            Constants.DEVICE_MAPS.get(entry.getKey()).num_delay = 0 ;
                        }else{
                            Constants.DEVICE_MAPS.get(entry.getKey()).num_delay += 1;
                        }
                    }
                    float percent = entry.getValue().readData();
                    viewUIMaps.get(entry.getKey()).progressBar.setProgress((int)percent);
                    viewUIMaps.get(entry.getKey()).transfer_percent_tv.setText(String.format(Locale.getDefault(), "%5.2f%%", percent));
                    if(!entry.getValue().noAutoconnect){
                        bConnectionChk = true;
                    }
                }
            }

            Log.d(TAG, loadStr);
            if(total_time < Constants.TOTAL_SCAN_TIME && bConnectionChk){
                connectionChkHandler.postDelayed(this, 2100);
                total_time += 2;
            }else{
                Log.d(TAG, "Total time for connection is over. total_time="+total_time);
                connectionChkHandler.removeCallbacks(this);
                for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet()){
                    entry.getValue().stopBroadcastBatteryNotify();
                }
            }
        }
    };

    public void startByActivity(){
        if(!isStartCall){
            startCallback();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * When a BLE device is picked from the device scan, the connection process starts
     * @param event
     */
    public void onEvent(final BleDevice event) {
        String mDeviceAddress = event.getAddress();
        System.out.println("Address: "+mDeviceAddress);
        Constants.ADDR_ID_MAPS.put(event.getAddress(), event.getDevice_id());
        System.out.println("Address here"+Constants.ADDR_ID_MAPS.get(event.getAddress()));
        MainActivity.shared().getBleService().connect(event.getDevice_id());

        if(!bConnectionChk){
            bConnectionChk = true;
            connectionChkHandler.postDelayed(updateTimerThread, 1000);
        }
    }

    /**
     * When a BLE connection is established, the UI is updated with the connection state.
     * @param event
     */
    public void onEvent(final BLEConnectionState event) {
        try {
            MainActivity.shared().runOnUiThread(() -> {
                if (event.connectionstate) {
                    viewUIMaps.get(event.device_id).scan_image.setImageResource(R.drawable.connected);
                    //                    viewUIMaps.get(event.device_id).scan_image.setText(R.string.connected);
                    updateUI(event.device_id, true);
                } else {
                    viewUIMaps.get(event.device_id).scan_image.setImageResource(R.drawable.disconnected);
                    //                    viewUIMaps.get(event.device_id).scan_image.setText(R.string.disconnected);
                    updateUI(event.device_id, false);
                }
            });
        }catch(NullPointerException ne){
            System.out.println("Unable to update UI, reporting this fix");
        }
    }

    /**
     *
     * @param bt
     */
    public void onEvent(final BatteryLevel bt){
        Log.d(TAG,"Battery level received for device" + bt.device_id + " " + bt.level+ new Date());
        try {
            ((MainActivity)getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewUIMaps.get(bt.device_id).battery_percent_tv.setText(bt.level+"");

                    viewUIMaps.get(bt.device_id).battery_percent_tv.setText(bt.level + "");
                    if (bt.level > 90 && bt.level <= 100) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_full_90to100_32dp);
                    } else if (bt.level > 80 && bt.level <= 90) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_full_80to90_32dp);
                    } else if (bt.level > 70 && bt.level <= 80) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_70to80_32dp);
                    } else if (bt.level > 60 && bt.level <= 70) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_60to70_32dp);
                    } else if (bt.level > 50 && bt.level <= 60) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_50to60_32dp);
                    } else if (bt.level > 40 && bt.level <= 50) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_40to50_32dp);
                    } else if (bt.level > 30 && bt.level <= 40) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_30to40_32dp);
                    } else if (bt.level > 20 && bt.level <= 30) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_20to30_32dp);
                    } else if (bt.level > 10 && bt.level <= 20) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_5to10_32dp);
                    } else if (bt.level <= 10) {
                        viewUIMaps.get(bt.device_id).battery_icon.
                                setImageResource(R.drawable.ic_battery_alert_black_32dp);
                    }
                }
            });
        }catch( NullPointerException n) {
            System.out.println("UI cannot be updated as yet, trying again");
        }
    }

//    public void onEvent(HelmetData helmetData){
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                helmetDataVolume++;
//                if(helmetDataVolume == sessionDataVolume[0]){
//                    helmetDataVolume = 0; //Reset for next session
//                    return;
//                }
//                progressBar[0].setProgress( ( helmetDataVolume/sessionDataVolume[0] ) * 100 );
//            }
//        });
//    }

//    public void onEvent(SensoryWatch sensoryWatch){
//        if(isAdded())
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                sensoryWatchDataVolume++;
//
//                if(sensoryWatchDataVolume == sessionDataVolume[1]){
//                    sensoryWatchDataVolume = 0;
//                    return;
//                }
//
//                progressBar[1].setProgress( (sensoryWatchDataVolume/sessionDataVolume[1] ) * 100 );
//            }
//        });
//    }

//    public void onEvent(HeartRateBelt heartRateBelt) {
//        progressBarHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                heartRateBeltDataVolume = (heartRateBeltDataVolume + 1)/sessionDataVolume[2];
//                progressBar[2].setProgress( ( heartRateBeltDataVolume / sessionDataVolume[2] ) * 100);
//            }
//        });
//    }

    /*
     * Calculate the speed and update UI
     * @param location
     */

//    public void onLocationChanged(Location location) {
//
//        if (location != null){
//            // gps support speed feature, then show the gps's speed
//            if (location.hasSpeed()){
//                float speed = location.getSpeed(); // this api is not good to show speed accurately , sometimes show random values
//                sensorData.speed =  speed*(18.0f/5.0f);
//                Log.d(TAG, "Localtion has speed="+speed);
//            }else if(oldLocation != null){ // if gps doesn't support speed, then calculate the speed using latlong and time interval
//                double speed = Common.calculateSpeed(oldLocation, location);
//                sensorData.speed =  (float)(speed*(18.0/5.0));
//                Log.d(TAG, "Calculate speed="+sensorData.speed);
//            }else{
//                sensorData.speed = 0;
//            }
//
////            speed_tv.setText(String.format("%3.1f", sensorData.speed));
//
//            oldLocation = location;
//        }else{
//            sensorData.speed = 0;
//        }
//    }


//    public void onEvent(Location event){
//        onLocationChanged(event);
//    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Every UI element is initialized based on device parameters and placed in a position-based
     * ViewList
     * @param view
     */
    public void initialization(View view){
        viewUIMaps = new HashMap<>();

        //One view list for each device
        ViewList viewList1 = new ViewList();
        ViewList viewList2 = new ViewList();
        ViewList viewList3 = new ViewList();

        //Each device's name, address, image and data progress bars are extracted.
        viewList1.name_tv = (TextView) view.findViewById(R.id.device_name1);
        viewList2.name_tv = (TextView) view.findViewById(R.id.device_name2);
        viewList3.name_tv = (TextView) view.findViewById(R.id.device_name3);

        viewList1.address_tv = (TextView) view.findViewById(R.id.device_addr1);
        viewList2.address_tv = (TextView) view.findViewById(R.id.device_addr2);
        viewList3.address_tv = (TextView) view.findViewById(R.id.device_addr3);

        viewList1.scan_image = (ImageView) view.findViewById(R.id.scan_ble1);
        viewList2.scan_image = (ImageView) view.findViewById(R.id.scan_ble2);
        viewList3.scan_image = (ImageView) view.findViewById(R.id.scan_ble3);

        viewList1.transfer_percent_tv = (TextView) view.findViewById(R.id.device_data_level1);
        viewList2.transfer_percent_tv = (TextView) view.findViewById(R.id.device_data_level2);
        viewList3.transfer_percent_tv = (TextView) view.findViewById(R.id.device_data_level3);

        progressBar[0] = (ProgressBar) view.findViewById(R.id.progress_bar_helmet);
        progressBar[1] = (ProgressBar) view.findViewById(R.id.progress_bar_sensory_watch);
        progressBar[2] = (ProgressBar) view.findViewById(R.id.progress_bar_heart_rate_belt);


        viewList1.progressBar = progressBar[0];
        viewList2.progressBar = progressBar[1];
        viewList3.progressBar = progressBar[2];

        //Each device's battery status and icon are extracted.
        viewList1.battery_icon = (ImageView) view.findViewById(R.id.battery_level1_icon);
        viewList2.battery_icon = (ImageView) view.findViewById(R.id.battery_level2_icon);
        viewList3.battery_icon = (ImageView) view.findViewById(R.id.battery_level3_icon);

        viewList1.battery_percent_tv = (TextView) view.findViewById(R.id.battery_level1_percent_text);
        viewList2.battery_percent_tv = (TextView) view.findViewById(R.id.battery_level2_percent_text);
        viewList3.battery_percent_tv = (TextView) view.findViewById(R.id.battery_level3_percent_text);

        viewUIMaps.put(DEV_1, viewList1);
        viewUIMaps.put(DEV_2, viewList2);
        viewUIMaps.put(DEV_3, viewList3);

        /*In the UI map entrySet, the device IDs are keys. Each key is used to
        * manipulate the connection status image, based on the current connection
        * state.
        * */
        for (Map.Entry<String,ViewList> entry : viewUIMaps.entrySet()){
            final String device_id = entry.getKey();

            entry.getValue().scan_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!((MainActivity)getActivity()).mBluetoothAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
                        return ;
                        // Otherwise, setup the chat session
                    }

                    //Disconnect, if connected icon is clicked.
                    if(Constants.DEVICE_MAPS.get(device_id).connected){
                        System.out.println("starting disconnection");
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Disconnect Device ("+device_id+") ?")
                                .setMessage(" ")
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
//                                            ((MapActivity)getActivity()).mConnected = false;
                                        try{
                                            Constants.DEVICE_MAPS.get(device_id).noAutoconnect = true;
                                            ((MainActivity)getActivity()).getBleService().disconnect(device_id);
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1B5AAC"));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D3D3D3"));



                    }else{
                        //Initiates a device scan activity if the particular device id shows disconnected
                        if (((MainActivity)getActivity()).mBluetoothAdapter.isEnabled()) {
                            Toast.makeText(getActivity(), "Searching for sensor ", Toast.LENGTH_SHORT).show();
                            ((MainActivity)getActivity()).mBluetoothAdapter.getBondedDevices();
                            Intent i = new Intent(getContext(), DeviceScanActivity.class);
                            i.putExtra(Constants.DEVICE_ID, device_id);
                            startActivity(i);
                        }
                    }
                }
            });
        }
    }




    void registerAndCheck(Object helper) {
        if (!mBus.isRegistered(helper)) {
            mBus.register(helper);
        }
    }

    /**
     * Filter the last used device
     * @param name device name
     * @param addr device mac address
     */
    private void filteredDevices(String name, String addr){

        Log.d(TAG,"filtering devices");

        String new_name = name+Common.getLast4Char(addr);

        boolean needReconnect = false;

        for(Map.Entry<String, Boolean> entry : connect_req.entrySet()){
            //If this device not filter, then match it's address from list of last used device.
            if(!entry.getValue()){
                String foundaddr = DevicePreferences.getInstance(getContext()).getAddr(entry.getKey());
                if(foundaddr.length() > 0 && foundaddr.equalsIgnoreCase(addr)){
                    DeviceDetails deviceDetails = new DeviceDetails();
                    deviceDetails.mac_address = addr;
                    deviceDetails.name = new_name;
                    deviceDetails.num_delay =  Constants.RECONNECTION_TEST;
                    Constants.DEVICE_MAPS.put(entry.getKey(), deviceDetails);
                    Constants.ADDR_ID_MAPS.put(addr, entry.getKey());
                    needReconnect = true;
                    connect_req.put(entry.getKey(), true);
                    Log.d(TAG, "Background connection rq: Found Device " + new_name +":"+addr);
                }
            }
        }

        if(!bConnectionChk && needReconnect){
            bConnectionChk = true;
            connectionChkHandler.postDelayed(updateTimerThread, 1000);
        }
    }




}


class ViewList {

    TextView name_tv;
    TextView address_tv;
    ImageView scan_image;
    ImageView battery_icon;
    TextView battery_percent_tv;
    TextView transfer_percent_tv;
    ProgressBar progressBar;

}


