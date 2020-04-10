package com.decalthon.helmet.stability.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.decalthon.helmet.stability.BLE.BluetoothLeService;
import com.decalthon.helmet.stability.BLE.MyBroadcastReceiver;
import com.decalthon.helmet.stability.BLE.gatt_server.BluetoothLeGattServer;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.Fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.Fragments.CustomViewFragment;
import com.decalthon.helmet.stability.Fragments.DeviceFragment;
import com.decalthon.helmet.stability.Fragments.GPSSpeedFragment;
import com.decalthon.helmet.stability.Fragments.HomeFragment;
import com.decalthon.helmet.stability.Fragments.LoginFragment;
import com.decalthon.helmet.stability.Fragments.MapFragment;
import com.decalthon.helmet.stability.Fragments.MarkerDialogFragment;
import com.decalthon.helmet.stability.Fragments.MonthlyCalendarFragment;
import com.decalthon.helmet.stability.Fragments.ProfileFragment;
import com.decalthon.helmet.stability.Fragments.RegistrationFormFragment;
import com.decalthon.helmet.stability.Fragments.SevenSessionsSummaryFragment;
import com.decalthon.helmet.stability.Fragments.YearPagerFragment;
import com.decalthon.helmet.stability.Fragments.YearlyCalendarFragment;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.firestore.FirebaseStorageManager;
import com.decalthon.helmet.stability.firestore.FirestoreUserModel;
import com.decalthon.helmet.stability.firestore.entities.impl.UserImpl;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.preferences.DevicePreferences;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.decalthon.helmet.stability.Utilities.Constants.DEVICE_MAPS;
import static com.decalthon.helmet.stability.Utilities.Constants.DevPREFERENCES;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, LoginFragment.OnFragmentInteractionListener, RegistrationFormFragment.OnFragmentInteractionListener, MonthlyCalendarFragment.OnFragmentInteractionListener,  ProfileFragment.OnFragmentInteractionListener , DeviceFragment.OnFragmentInteractionListener , MapFragment.OnFragmentInteractionListener, CustomViewFragment.OnFragmentInteractionListener, CustomGraphFragment.OnFragmentInteractionListener, GPSSpeedFragment.OnFragmentInteractionListener , SevenSessionsSummaryFragment.OnFragmentInteractionListener , MarkerDialogFragment.OnFragmentInteractionListener , YearlyCalendarFragment.OnFragmentInteractionListener, YearPagerFragment.OnFragmentInteractionListener{

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
        setContentView(R.layout.activity_main);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mainActivity = MainActivity.this;

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

        initialization();

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

        navigateToFragments();
        mGattUpdateReceiver = new MyBroadcastReceiver();
        checkAndRequestPermission();

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        View mCustomView = mInflater.inflate(R.layout.actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        LocalBroadcastManager.getInstance(this).registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        CircleImageView logoutMenuButton = mActionBar.getCustomView().findViewById(R.id.logout_link);
        logoutMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(getBaseContext())
                        .setTitle("Alert")
                        .setMessage("  Do you want to logout?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes",null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1B5AAC")); // Set text color to blue color
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D3D3D3"));  // Set text color to ligh gray color
            }
        });
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

            CircleImageView connect = actionbarView.findViewById(R.id.ble_device_connectivity);
            connect.setImageResource(R.drawable.ic_bluetooth_connected_24dp);
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

    public void initialization(){
        DEVICE_MAPS.put(getResources().getString(R.string.device1_tv), new DeviceDetails());
        DEVICE_MAPS.put(getResources().getString(R.string.device2_tv), new DeviceDetails());
        DEVICE_MAPS.put(getResources().getString(R.string.device3_tv), new DeviceDetails());

//        SENSORS_MAPS.put(getResources().getString(R.string.device1_tv), HEL_SENSORS);
//        SENSORS_MAPS.put(getResources().getString(R.string.device2_tv), WAT_SENSORS);
//        SENSORS_MAPS.put(getResources().getString(R.string.device3_tv), BELT_SENSORS);
    }

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
        fragmentTransaction.add(R.id.fragment, fragment);
        fragmentTransaction.addToBackStack("DeviceFragment");
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
        this.showProgressCircle(getApplicationContext(),0);
        System.out.println("onResume");
    }

    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
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



        Fragment frag = fragmentManager.findFragmentByTag(DeviceFragment.class.getSimpleName());
        if(frag instanceof DeviceFragment){
//            System.out.println("BACK PRESS::"+frag+"  Fragment");
            ((DeviceFragment) frag).startByActivity();
            fragmentManager.popBackStack();
            restoreHomeActionBar();
            Log.d("DeviceFragment","popped");
        }
        frag = fragmentManager.findFragmentByTag("Profile Fragment");
        if(frag instanceof  ProfileFragment){
            fragmentManager.popBackStack();
            restoreHomeActionBar();
        }

        frag = fragmentManager.findFragmentByTag("Map Fragment");
        if(frag instanceof  MapFragment){
            fragmentManager.popBackStack();
        }

        frag = fragmentManager.findFragmentByTag("Graph Fragment");
        if(frag instanceof CustomGraphFragment || frag instanceof CustomViewFragment){
            fragmentManager.popBackStack();
        }

        frag = fragmentManager.findFragmentByTag("GPS Fragment");
        if(frag instanceof GPSSpeedFragment){
            fragmentManager.popBackStack();
        }

        frag = fragmentManager.findFragmentByTag("Calendar Fragment");
        if(frag instanceof MonthlyCalendarFragment){
            fragmentManager.popBackStack();
        }

        frag = fragmentManager.findFragmentByTag(SevenSessionsSummaryFragment.class.getSimpleName());

        if(frag instanceof SevenSessionsSummaryFragment){

        }

        frag = fragmentManager.findFragmentByTag("Marker Dialog Fragment");
        if(frag instanceof MarkerDialogFragment){
            //Do not pop stack
            ((MarkerDialogFragment) frag).dismiss();
        }

        if(frag instanceof RegistrationFormFragment ){
            super.onBackPressed();
        }
    }

    public void restoreHomeActionBar(){
        System.out.println("Restore action bar called");
        ActionBar mActionbar = getSupportActionBar();
        showProgressCircle(getApplicationContext() ,0 );
        mActionbar.getCustomView().findViewById(R.id.logout_link)
                .setVisibility(View.VISIBLE);
        mActionbar.getCustomView().findViewById(R.id.profile_link)
                .setVisibility(View.VISIBLE);
        mActionbar.getCustomView().findViewById(R.id.gps_session_start_btn)
                .setVisibility(View.VISIBLE);
        mActionbar.getCustomView().findViewById(R.id.ble_device_connectivity)
                .setVisibility(View.VISIBLE);
        mActionbar.getCustomView().findViewById(R.id.title_text)
                .setVisibility(View.GONE);
        mActionbar.getCustomView().findViewById(R.id.back_link)
                .setVisibility(View.GONE);
    }

    public void showProgressCircle(Context context,float increasePercent){

        boolean anyDeviceConnected = false;
        int deviceConnectedCount = 0;
        CircleProgressView dataLoadProgressView;
        ActionBar actionbar = getSupportActionBar();
        dataLoadProgressView = actionbar.getCustomView().findViewById
                (R.id.ble_device_connectivity);
        if(context != null){
            for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
                if(entry.getValue().connected){
                    deviceConnectedCount += 1;
                    anyDeviceConnected = true;
                }
            }
            if(!anyDeviceConnected){
                dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
                dataLoadProgressView.setValueAnimated(24,1000);
                return;
            }else{
                switch(deviceConnectedCount){
                    case 1:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.yellow));
                        break;
                    case 2:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.orange));
                        break;
                    case 3:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.green));
                        break;
                    default:
                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
                }
            }
        }
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

}
