package com.decalthon.helmet.stability.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.SessionCDL;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.FileUtilities;
import com.decalthon.helmet.stability.location.BackgroundLocationUpdateService;
import com.decalthon.helmet.stability.model.DeviceModels.SensorDataOld;

import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.decalthon.helmet.stability.Utilities.FileUtilities.REQUEST_PERMISSIONS_LOG_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GPSSpeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GPSSpeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * Graph without a UI used to run the fused-location GPS speed service
 */
public class GPSSpeedFragment extends Fragment implements LocationListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "GPS Speed Fragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Location oldLocation = null;
    private SensorDataOld sensorData = new SensorDataOld();
    EventBus mBus = EventBus.getDefault();
    private View gpsSpeedView;
    private CircleImageView startStopView;
    private OnFragmentInteractionListener mListener;
    private Handler gpsSpeedHandler = new Handler();
    private static GpsSpeed gpsSpeed;
    private static Cursor mergeCursor;
    public static SessionCdlDb sessionCdlDb;
    static ArrayList<SessionCDL> sessionData;

    public GPSSpeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GPSSpeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GPSSpeedFragment newInstance(String param1, String param2) {
        GPSSpeedFragment fragment = new GPSSpeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment creation
     * @param savedInstanceState Any restore-able data
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        /*
        NOTE: When the GPS fragment is created, it does not create a view, rather,
        it fires an intent to begin a background location update service.
        Using the onLocationChanged callback, speed changes have been measured
         */

        Intent intent  = new Intent(getActivity(), BackgroundLocationUpdateService.class);

        //This service is bound to the current activity and fragment
        getActivity().startService(intent);

        Constants.isStart = true;

        //While the service begins to sense location updates, existing storage permissions are checked
        checkAndRequestWriteLog();
    }

    /**
     *
     * @param inflater The callback argument which inflates a zero height {@link GPSSpeedFragment}
     * @param container The parent container which holds the graph fragment
     * @param savedInstanceState Any restore-able data
     * @return The view of the fragment, a rectangular area on the screen
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //There is no rectangular area, as this is a zero-dimension fragment
        gpsSpeedView = inflater.inflate(R.layout.fragment_gps_speed, container, false);

        //The GPS speed update thread starts as soon as the view is created
        gpsSpeedHandler.postDelayed(gpsSpeedUpdateThread, 0);
        return gpsSpeedView;
    }

    /**
     *
     * @param view The zero-height GPS speed fragment
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        * Customizes the action bar for the GPS speed view
        * */
        ActionBar mActionBar =
                ( (MainActivity) getActivity() ).getSupportActionBar();

        /*The button to start or stop location updates*/
        startStopView =
                mActionBar.getCustomView().findViewById(R.id.gps_session_start_btn);
        startStopView.setImageResource(R.drawable.pause);
        startStopView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopView.setImageResource(R.drawable.play);
                MainActivity.shared().onBackPressed();

            }
        });
    }

    /**
     * For API levels above level 23
     * =============================
     * If the Storage Write permission has been granted, the GPS speed header is written to the log.
     * If the Storage Write permissions are not granted, permissions are requested using
     * {@link Fragment#requestPermissions(String[], int)}
     *
     *
     * For supported API levels below level 23
     * ======================================
     * The permission check is not required. GPS speed header is written to the log.
     */
    private void checkAndRequestWriteLog(){
        if (Build.VERSION.SDK_INT >= 23){
            if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED ) {
                FileUtilities.writeGpsSpeedHeaderToLog(getContext());
            }else{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_LOG_STORAGE);
            }
        }else{
            FileUtilities.writeGpsSpeedHeaderToLog(getContext());
        }

    }

    /**
     * Associates the event bus with the fragment on Activity Creation
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerAndCheck(this);
        sessionCdlDb = Room.databaseBuilder(getActivity().getApplicationContext(), SessionCdlDb.class,"Gps_Speed_DB")
                .fallbackToDestructiveMigration()
                .addCallback(callback)
                .build();
        Log.d(TAG,"GPS speed DB created");
    }

    /**
     * Checks if the an event bus has been registered for the current Fragment instance
     * @param helper The Fragment instance which needs to listen to an EventBus event
     */
    void registerAndCheck(Object helper) {
        if (!mBus.isRegistered(helper)) {
            mBus.register(helper);
        }
    }

    // Periodically writes data to the file log
    private Runnable gpsSpeedUpdateThread = new Runnable() {
        public void run() {

            //The session can start only if if isStart is set to true

            if(!Constants.isStart) {
                return;
            }

            //Every second, GPS speed is updated and written to the log.
            //Each session log is prefixed with "GPS Speed"
            Log.d(TAG,"Timer="+new Date().toString());
            if(gpsSpeed != null) {
                FileUtilities.writeGpsSpeedDataToLog
                        (getContext(), gpsSpeed);

            }
            gpsSpeedHandler.postDelayed(this, 1000);
        }
    };




    public boolean checkPermissions(){
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(getContext(),Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return false;
        }
        return true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        mLocationManager.removeUpdates(GPSSpeedFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(checkPermissions()) {
//            mLocationManager.requestLocationUpdates
//                    (LocationManager.GPS_PROVIDER, 1000, 0, GPSSpeedFragment.this);
//        }
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
//        mLocationManager = (LocationManager) getActivity()
//                .getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mLocationManager.removeUpdates(GPSSpeedFragment.this);
        loadSessionCDLData();
        FileUtilities.closeFile();
        mListener = null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //No default implementation
        Log.i(TAG, "Provider " + provider + " has now status: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        //No default implementation
        Log.i(TAG, "Provider " + provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //No default implementation
        Log.i(TAG, "Provider " + provider + " is disabled");
    }


    /**
     * The event bus delivers location update events every second from the background service.
     * @param event A new/changed location , a LatLng object
     */
    public void onEvent(Location event){
//        updateTime = System.currentTimeMillis() - oldTime;
//        oldTime = updateTime;
//        System.out.println("updated time->"+updateTime);
        onLocationChanged(event);
    }

    /**
     * Calculates the speed and updates the file log
     * @param location A LatLng object
     */
    public void onLocationChanged(Location location) {
        float currentSpeed;
        gpsSpeed = new GpsSpeed();
        gpsSpeed.date = new Date().toString();
        gpsSpeed.timestamp = System.currentTimeMillis();
//        gpsSpeed.date = new Date().toString();
        gpsSpeed.latitude = (float)location.getLatitude();
        gpsSpeed.longitude = (float)location.getLongitude();


        MapFragment.addRideMarkerOnMap(location);


        if (location != null){

            //Add location to table here
            addGpsSpeedEntry(gpsSpeed);
            MapFragment.addRideMarkerOnMap(location);

            // gps support speed feature, then show the gps's speed
            if (location.hasSpeed()){
                    currentSpeed = location.getSpeed();
                    currentSpeed =  currentSpeed*(18.0f/5.0f);
                Log.d(TAG, "Localtion has speed="+currentSpeed);
                 gpsSpeed.speed = currentSpeed;

                 //Accuracy of location, higher the value, lower the accuracy
                if(location.hasAccuracy()){
                    gpsSpeed.accuracy_location = location.getAccuracy();
                }// this api is not good to show speed accurately , sometimes show random values
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(location.hasSpeedAccuracy()){
                        gpsSpeed.accuracy_speed = location.getSpeedAccuracyMetersPerSecond();
                    } else {
                        gpsSpeed.accuracy_speed = 0;
                    }
                }
                Log.d(TAG, "Localtion has speed="+currentSpeed);

            }else if(oldLocation != null){
                // if gps doesn't support speed, then calculate the speed using latlong and time interval
                double speed = Common.calculateSpeed(oldLocation, location);
                sensorData.speed =  (float)(speed*(18.0/5.0));
                Log.d(TAG, "Calculate speed="+sensorData.speed);
            }else{
                sensorData.speed = 0;
            }

//            speed_tv.setText(String.format("%3.1f", sensorData.speed));

            oldLocation = location;
        }else{
            sensorData.speed = 0;
        }
    }

    private void addGpsSpeedEntry(GpsSpeed gpsSpeed) {

        new InsertGpsSpeedEntryAsyncTask().execute(gpsSpeed);

    }

    private void loadSessionCDLData(){

        new SessionCDLDataLoadAsyncTask().execute();

    }

//    private void getMarkerSpeedTable(){
//
//        new JoinMarkerSpeedAsyncTask().execute();
//
//    }

    RoomDatabase.Callback callback= new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            //Toast.makeText(getApplicationContext()," On Create Called ",Toast.LENGTH_LONG).show();
            Log.i(TAG, " on create invoked ");

        }


        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //  Toast.makeText(getApplicationContext()," On Create Called ",Toast.LENGTH_LONG).show();
            Log.i(TAG, " on open invoked ");

        }

    };

//    private static class JoinMarkerSpeedAsyncTask extends AsyncTask<Void,Void,Void>{
//
//        /**
//         * Override this method to perform a computation on a background thread. The
//         * specified parameters are the parameters passed to {@link #execute}
//         * by the caller of this task.
//         * <p>
//         * This will normally run on a background thread. But to better
//         * support testing frameworks, it is recommended that this also tolerates
//         * direct execution on the foreground thread, as part of the {@link #execute} call.
//         * <p>
//         * This method can call {@link #publishProgress} to publish updates
//         * on the UI thread.
//         *
//         * @param voids The parameters of the task.
//         * @return A result, defined by the subclass of this task.
//         * @see #onPreExecute()
//         * @see #onPostExecute
//         * @see #publishProgress
//         */
//        @Override
//        protected Void doInBackground(Void... voids) {
//            mergeCursor = sessionCdlDb.getMarker_speedDAO().getMergedTimeStampData();
//            return null;
//        }
//    }

    private static class InsertGpsSpeedEntryAsyncTask extends AsyncTask<GpsSpeed,Void,Void> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param gpsSpeeds The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(GpsSpeed... gpsSpeeds) {
            sessionCdlDb.gpsSpeedDAO().insertSpeed(gpsSpeed);
            Log.d(TAG,"GPS speed has been inserted");
            return null;
        }
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("-----------onDestroy-----------");
//        mLocationManager.removeUpdates(this);
        gpsSpeedHandler.removeCallbacks(gpsSpeedUpdateThread);
        Intent intent  = new Intent(getActivity(), BackgroundLocationUpdateService.class);
        getActivity().stopService(intent);

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private static class SessionCDLDataLoadAsyncTask extends  AsyncTask<Void,Void,Void>{
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... voids) {
            sessionData = (ArrayList<SessionCDL>) sessionCdlDb.getMergedDao().getGpsMarkerMerge();
            System.out.println("SessionCDL data" + sessionData);
            return null;
        }
    }
}
