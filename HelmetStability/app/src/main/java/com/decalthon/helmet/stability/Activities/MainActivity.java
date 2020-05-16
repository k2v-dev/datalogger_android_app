package com.decalthon.helmet.stability.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.decalthon.helmet.stability.ble.BluetoothLeService;
import com.decalthon.helmet.stability.ble.Device_Parser;
import com.decalthon.helmet.stability.ble.MyBroadcastReceiver;
import com.decalthon.helmet.stability.ble.gatt_server.BluetoothLeGattServer;
import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.fragments.CalendarPagerFragment;
import com.decalthon.helmet.stability.fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.fragments.CustomViewFragment;
import com.decalthon.helmet.stability.fragments.DeviceFragment;
import com.decalthon.helmet.stability.fragments.GPSSpeedFragment;
import com.decalthon.helmet.stability.fragments.HomeFragment;
import com.decalthon.helmet.stability.fragments.LoginFragment;
import com.decalthon.helmet.stability.fragments.MapFragment;
import com.decalthon.helmet.stability.fragments.MarkerDialogFragment;
import com.decalthon.helmet.stability.fragments.MonthlyCalendarFragment;
import com.decalthon.helmet.stability.fragments.ProfileFragment;
import com.decalthon.helmet.stability.fragments.RegistrationFormFragment;
import com.decalthon.helmet.stability.fragments.SevenSessionsSummaryFragment;
import com.decalthon.helmet.stability.fragments.YearPagerFragment;
import com.decalthon.helmet.stability.fragments.YearlyCalendarFragment;
import com.decalthon.helmet.stability.MainApplication;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.devicemodels.DeviceDetails;
import com.decalthon.helmet.stability.preferences.DevicePreferences;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;

import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.decalthon.helmet.stability.utilities.Constants.DEVICE_MAPS;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, RegistrationFormFragment.OnFragmentInteractionListener, MonthlyCalendarFragment.OnFragmentInteractionListener,  ProfileFragment.OnFragmentInteractionListener , DeviceFragment.OnFragmentInteractionListener , MapFragment.OnFragmentInteractionListener, CustomViewFragment.OnFragmentInteractionListener, CustomGraphFragment.OnFragmentInteractionListener, GPSSpeedFragment.OnFragmentInteractionListener , SevenSessionsSummaryFragment.OnFragmentInteractionListener , MarkerDialogFragment.OnFragmentInteractionListener , YearlyCalendarFragment.OnFragmentInteractionListener, YearPagerFragment.OnFragmentInteractionListener , CalendarPagerFragment.OnFragmentInteractionListener{

    private static MainActivity mainActivity;
    public BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothLeService mBluetoothLeService = null;
    MyBroadcastReceiver mGattUpdateReceiver = null;
    private Menu menu;
    private static SessionCdlDb sessionRoomDb = null;
    private String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //getSupportActionBar().setCustomView(R.layout.actionbar);
        mainActivity = MainActivity.this;
        setContentView(R.layout.activity_main);

//        try{
//            long t1 = System.currentTimeMillis();
//            Long res = new DatabaseHelper.WaitingTask().execute().get();
//            long t2 = System.currentTimeMillis();
//
//            System.out.println("Result: "+ res+", time="+(t2-t1));
//        }catch (Exception ex){
//
//        }

//        DevicePreferences.getInstance(getApplicationContext()).clear();
       // FirebaseAuth.getInstance().signInAnonymously();
        //FirebaseAuth.getInstance().signOut();

//        FirebaseStorageManager.downloadImage(getApplicationContext(), UserPreferences.getInstance(getApplicationContext()).getUserID(), new FirebaseStorageManager.DownloadListener() {
//            @Override
//            public void onComplete(boolean isSuccess, String filepath) {
//                if(isSuccess){
//                    UserPreferences.getInstance(getApplicationContext()).saveProfilePhoto(filepath);
//                    System.out.println("Download iamge");
//                }
//            }
//        });
//        UserImpl userImpl = new UserImpl(getApplicationContext());
//        FirestoreUserModel firestoreUserModel = new FirestoreUserModel();
//        firestoreUserModel.setEmail("ajit.gupta123@gmail.com");
//        firestoreUserModel.setPhone_no("917044223313");
//        firestoreUserModel.setName("Ajit");
//        userImpl.doesUserExist(firestoreUserModel);
//        userImpl.getLoginUserByPhone(firestoreUserModel.getPhone_no());
        SessionCdlDb gpsSpeedDB;
//        MyBroadcastReceiver mGattUpdateReceiver = null;

//        DevicePreferences.getInstance(getApplicationContext()).clear();

//        initialization();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        final Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);


        mGattUpdateReceiver = new MyBroadcastReceiver();
        checkAndRequestPermission();

        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


//        CircleImageView logoutMenuButton =
//                findViewById(R.id.logout_link);
//        logoutMenuButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog dialog = new AlertDialog.Builder(mainActivity)
//                        .setTitle("Alert")
//                        .setMessage(getResources().getString(R.string.log_out_msg))
//                        .setNegativeButton("No", null)
//                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                UserPreferences.getInstance(getApplicationContext()).clear();
//                                ProfilePreferences.getInstance(getApplicationContext()).clear();
//                                DevicePreferences.getInstance(getApplicationContext()).clear();
//                                navigateToFragments();
//                            }
//                        })
//                        .create();
//                dialog.show();
//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1B5AAC")); // Set text color to blue color
//                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D3D3D3"));  // Set text color to ligh gray color
//            }
//        });
//        sessionCdlDb = Room.databaseBuilder(getApplicationContext(),SessionCdlDb.class,"Gps_Speed_DB")
//                .addCallback(callback).build();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mGattUpdateReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mGattUpdateReceiver);
        }

        if (mServiceConnection != null){
            unbindService(mServiceConnection);
        }

        try {
            if(mBluetoothLeService != null){
                for (Map.Entry<String,DeviceDetails> entry : DEVICE_MAPS.entrySet()){
                    mBluetoothLeService.disconnect(entry.getKey());
                }
            }
        }
        catch (Exception e){
//            Crashlytics.logException(e);
            e.printStackTrace();
        }
        mBluetoothLeService = null;

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSessionStarted(String TAG, String message) {
        System.out.println(TAG + "--_->" + message );
        if(TAG.equalsIgnoreCase(HomeFragment.TAG)){
            
        }
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("Connected now");

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
//                System.out.println("unable to initialise bluetooth");
                finish();
            }
            BluetoothLeGattServer.getInstance(getApplicationContext()).start();
            Log.d("MapActivity", "Automatically connects to the device upon successful start-up initialization.");
            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(mDeviceAddress);
//            Common.connectAll();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            BluetoothLeGattServer.getInstance(getApplicationContext()).stopAll();
            View actionbarView = getActionBar().getCustomView();

//            CircleImageView connect = actionbarView.findViewById(R.id.ble_device_connectivity);
//            connect.setImageResource(R.drawable.ic_bluetooth_connected_24dp);
        }
    };

    public static MainActivity shared(){
        return mainActivity;
    }

    public BluetoothLeService getBleService(){
        return mBluetoothLeService;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("Inside on create options menu");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main_menu, menu);
//        changed_menu();
        return super.onCreateOptionsMenu(menu);
    }

    public void changed_menu(){
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment);
        try{
            if (fragment instanceof DeviceFragment){
                this.menu.getItem(0).setVisible(true);
            }else{
                this.menu.getItem(0).setVisible(false);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mBus != null){
//            mBus.unregister(this);
//        }
    }

//    public void initialization(){
//        DEVICE_MAPS.put(getResources().getString(R.string.device1_tv), new DeviceDetails());
//        DEVICE_MAPS.put(getResources().getString(R.string.device2_tv), new DeviceDetails());
//        DEVICE_MAPS.put(getResources().getString(R.string.device3_tv), new DeviceDetails());
//
//        String[] indoor = getResources().getStringArray(R.array.indoor_sports_array);
//        int[] indoor_code = getResources().getIntArray(R.array.indoor_sports_array_code);
//        String[] outdoor = getResources().getStringArray(R.array.outdoor_sports_array);
//        int[] outdoor_code = getResources().getIntArray(R.array.outdoor_sports_array_code);
//
//        for (int i = 0; i < indoor.length; i++) {
//            String key = indoor[i]+"_"+Constants.INDOOR;
//            Constants.ActivityCodeMap.put(key, indoor_code[i]);
//        }
//
//        for (int i = 0; i < outdoor.length; i++) {
//            String key = outdoor[i]+"_"+Constants.OUTDOOR;
//            Constants.ActivityCodeMap.put(key, outdoor_code[i]);
//        }
//
////        SENSORS_MAPS.put(getResources().getString(R.string.device1_tv), HEL_SENSORS);
////        SENSORS_MAPS.put(getResources().getString(R.string.device2_tv), WAT_SENSORS);
////        SENSORS_MAPS.put(getResources().getString(R.string.device3_tv), BELT_SENSORS);
//    }

    static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void navigateToFragments(){
        Fragment fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment,
                HomeFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(MainActivity.class.getSimpleName());
        fragmentTransaction.commit();

//        Fragment fragment = new MonthlyCalendarFragment();
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.fragment, fragment);
//        fragmentTransaction.addToBackStack("MonthlyCalendarFragment");
//        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.showProgressCircle(Device_Parser.get_txf_status());
        System.out.println("onResume");
    }

    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = manager.findFragmentById(R.id.fragment);
                if (fragment instanceof  HomeFragment){
                    ((HomeFragment)fragment).refresh_cards();
                }
                invalidateOptionsMenu();
            }
        };

        return result;
    }

//    private FragmentManager.OnBackStackChangedListener getListener()
//    {
//        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
//        {
//            public void onBackStackChanged()
//            {
//                FragmentManager manager = getSupportFragmentManager();
//                Fragment fragment = manager.findFragmentById(R.id.fragment);
//                if (fragment instanceof  DeviceFragment){
//                    ((DeviceFragment)fragment).startByActivity();
//                }
//                invalidateOptionsMenu();
//            }
//        };
//
//        return result;
//    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //create a dialog to ask yes no question whether or not the user wants to exit
        System.out.println("Back button");
//        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();

//        Fragment frag =
//                fragmentManager.findFragmentByTag(DeviceFragment.class.getSimpleName());
//        if(frag instanceof DeviceFragment){
////            System.out.println("BACK PRESS::"+frag+"  Fragment");
//            ((DeviceFragment) frag).startByActivity();
//            fragmentManager.popBackStack();
////            restoreHomeActionBar();
//            Log.d("DeviceFragment","popped");
//            return;
//        }
//        frag = fragmentManager.findFragmentByTag(ProfileFragment.class.getSimpleName());
//        if(frag instanceof  ProfileFragment){
//            if (ProfilePreferences.getInstance(getApplicationContext()).isEmpty()) {
//                Common.okAlertMessage(frag.getContext(), getString(R.string.enter_all_details));
//            }else {
//                System.out.println("Back button2");
//                fragmentManager.popBackStack();
////                restoreHomeActionBar();
//            }
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(MapFragment.class.getSimpleName());
//        if(frag instanceof  MapFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(CustomViewFragment.class.getSimpleName());
//        if(frag instanceof CustomViewFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(GPSSpeedFragment.class.getSimpleName());
//        if(frag instanceof GPSSpeedFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(CalendarPagerFragment.class.getSimpleName());
//        if(frag instanceof CalendarPagerFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(SevenSessionsSummaryFragment.class.getSimpleName());
//
//        if(frag instanceof SevenSessionsSummaryFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag = fragmentManager.findFragmentByTag(MarkerDialogFragment.class.getSimpleName());
//        if(frag instanceof MarkerDialogFragment){
//            fragmentManager.popBackStack();
//            return;
//        }
//
//        frag =
//                fragmentManager.findFragmentByTag(DailySessionsFragment.class.getSimpleName());
//        if(frag instanceof DailySessionsFragment){
//            fragmentManager.popBackStack(MonthlyCalendarFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            return;
//        }

        List<Fragment> frags = fragmentManager.getFragments();
        Fragment fragment = frags.get(frags.size() -1);
        if(frags.size() > 0){
            fragment = frags.get(frags.size() -1);
        }

//        for (Fragment fragment: frags) {
            if(fragment instanceof HomeFragment){
                fragmentManager.popBackStack();
                //finish();
                return;
            }else if(fragment instanceof ProfileFragment){
                if (ProfilePreferences.getInstance(getApplicationContext()).isEmpty()) {
                    Common.okAlertMessage(fragment.getContext(), getString(R.string.enter_all_details));
                }else {
                    System.out.println("Back button2");
                    fragmentManager.popBackStack();
                }
                return;
            }else if(fragment instanceof  YearlyCalendarFragment){
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();
                return;
            }else if(fragment instanceof CalendarPagerFragment){
                Log.d(TAG, "onBackPressed: checked");
                CalendarPagerFragment calendarPagerFragment = (CalendarPagerFragment) fragment;
                if(calendarPagerFragment.getCalendarType().equalsIgnoreCase(MonthlyCalendarFragment.class.getSimpleName())){
                    fragmentManager.popBackStack(HomeFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return;
                }
            }

//        }
//        super.onBackPressed();
        fragmentManager.popBackStack();
//            super.onBackPressed();
//        frag =
//                fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
//        if(frag instanceof HomeFragment){
//            fragmentManager.popBackStack();
//            finish();
//            return;
//        }


    }

//    public void restoreHomeActionBar(){
//        System.out.println("Restore action bar called");
//        ActionBar mActionbar = getSupportActionBar();
//        showProgressCircle(getApplicationContext() , Device_Parser.get_txf_status() );
//        mActionbar.getCustomView().findViewById(R.id.logout_link)
//                .setVisibility(View.VISIBLE);
//        mActionbar.getCustomView().findViewById(R.id.profile_link)
//                .setVisibility(View.VISIBLE);
//        mActionbar.getCustomView().findViewById(R.id.gps_session_start_btn)
//                .setVisibility(View.VISIBLE);
//        mActionbar.getCustomView().findViewById(R.id.ble_device_connectivity)
//                .setVisibility(View.VISIBLE);
//        mActionbar.getCustomView().findViewById(R.id.title_text)
//                .setVisibility(View.GONE);
//        mActionbar.getCustomView().findViewById(R.id.back_link)
//                .setVisibility(View.GONE);
//    }

    public void showProgressCircle(float increasePercent){

        boolean anyDeviceConnected = false;
        int deviceConnectedCount = 0;
        CircleProgressView dataLoadProgressView;
        dataLoadProgressView = findViewById(R.id.fragment).findViewById
                (R.id.ble_device_btn);
        Context context = MainApplication.getAppContext();
        if(context != null){
//            for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
//                if(entry.getValue().connected){
//                    deviceConnectedCount += 1;
//                    anyDeviceConnected = true;
//                }
//            }

            String[] devices = {context.getResources().getString(R.string.device1_tv), context.getResources().getString(R.string.device2_tv)};
            for (int i = 0; i < 2; i++) {
                DeviceDetails deviceDetails1 = Constants.DEVICE_MAPS.get(devices[i]);
                if(deviceDetails1 != null && deviceDetails1.connected){
                    deviceConnectedCount += 1;
                    anyDeviceConnected = true;
                }
            }
            Log.d(TAG, "DeviceConnection="+deviceConnectedCount+", percent="+increasePercent);
            if(!anyDeviceConnected){
                dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
                dataLoadProgressView.setValueAnimated(increasePercent,500);

                return;
            }else{
                switch(deviceConnectedCount){
//                    case 1:
//                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.yellow));
//                        break;
                    case 1:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.orange));
                        dataLoadProgressView.setBarColor(getResources().getColor(R.color.orange));
                        break;
                    case 2:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.green));
                        dataLoadProgressView.setBarColor(getResources().getColor(R.color.green));

                        break;
                    default:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
                        dataLoadProgressView.setBarColor(getResources().getColor(R.color.gray));
                }
                dataLoadProgressView.setValueAnimated(increasePercent,500);

                //dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.gray));
            }
        }
        invalidateOptionsMenu();
//        if (Build.VERSION.SDK_INT >= 11)
//        {
//            VersionHelper.refreshActionBarMenu(this);
//        }
    }

    private void checkAndRequestPermission(){
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23){
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQUEST_PERMISSIONS_LOG_STORAGE);

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                    }
                } else{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS_LOG_STORAGE:
                if (grantResults.length > 2) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Required GPS/Location permission  are disable.", Toast.LENGTH_SHORT).show();
                    }
                    if (grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Required GPS/Location permission  are disable.", Toast.LENGTH_SHORT).show();
                    }
                    if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Required storage permission  are disable.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * Referesh the actibivity with delay
     * @param delay in millisecond
     */
    public void refresh(int delay){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Common.wait(delay);
                finish();
                startActivity(getIntent());
            }
        }).start();
    }

}
