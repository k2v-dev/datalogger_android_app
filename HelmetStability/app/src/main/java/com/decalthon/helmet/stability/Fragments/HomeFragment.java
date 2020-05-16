package com.decalthon.helmet.stability.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.decalthon.helmet.stability.MainApplication;
import com.decalthon.helmet.stability.activities.LoginActivity;
import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.activities.ProfileActivity;
import com.decalthon.helmet.stability.asynctasks.imuasynctasks.GetCollectiveSummaryDetailsAsyncTask;
import com.decalthon.helmet.stability.asynctasks.sessioninfoasynctasks.GetLatestSessionSummaryAsyncTask;
import com.decalthon.helmet.stability.ble.ButtonBox_Parser;
import com.decalthon.helmet.stability.ble.Device1_Parser;
import com.decalthon.helmet.stability.ble.Device_Parser;
import com.decalthon.helmet.stability.database.DatabaseHelper;
import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.firestore.entities.impl.CollectiveSummaryImpl;
import com.decalthon.helmet.stability.model.devicemodels.DeviceDetails;
import com.decalthon.helmet.stability.preferences.CollectiveSummaryPreference;
import com.decalthon.helmet.stability.preferences.CsvPreference;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.devicemodels.MemoryUsage;
import com.decalthon.helmet.stability.model.generic.TimeFmt;
import com.decalthon.helmet.stability.preferences.DevicePreferences;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.utilities.CsvGenerator;
import com.decalthon.helmet.stability.webservice.requests.CollectiveSummaryReq;
import com.decalthon.helmet.stability.workmanager.WorkMgrHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import at.grabner.circleprogress.CircleProgressView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getColor;
import static com.decalthon.helmet.stability.utilities.ViewDimUtils.applyDim;
import static com.decalthon.helmet.stability.utilities.ViewDimUtils.clearDim;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */

//Provides the opening view on the screen after a login
public class HomeFragment extends Fragment  {
    public static String TAG  = HomeFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private ActionBar mActionBar;
    private Context mContext;

    private LocationManager mLocationManager;
    private static int percentage = 0;
    private static boolean isDone = false;
    private View actionbarView;
    private String mActivityType = "";
    private String prev_indoor_selected = "", prev_outdoor_selected = "";
//    private  SessionSummary mLatestSessionSummary;
    //A BLE device list adapter instance
    private BluetoothAdapter mBluetoothAdapter;
    private CircleImageView mProfileImageView =null;
    //Event bus instance use for onEvent actions
    private EventBus mBus = EventBus.getDefault();
    private boolean mIsStopActivity = true;
    private View mHomeFragmentView;

    SessionSummary mlatestSessionSummary = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        Common.load_initialization(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");

        //TODO Get back the map when needed
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeFragmentView = view;
        actionbarView = view.findViewById(R.id.home_action_bar);

        mProfileImageView = view.findViewById(R.id.profile_btn);

        //Restoration of profile photo from shared preferences
        UserPreferences userPreferences = UserPreferences.getInstance(getContext());

//        profileImageView.setImageBitmap(BitmapFactory.decodeFile
//                (userPreferences.getProfilePhoto()));
        if(userPreferences.getProfilePhoto().equals(Constants.DEFAULT_PATH)){
            mProfileImageView.setImageResource(R.mipmap.anonymous_round);
        }
        else{
            mProfileImageView.setImageBitmap(BitmapFactory.decodeFile
                    (userPreferences.getProfilePhoto()));
        }
        //Navigation to profile edit page
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Fragment profileFragment = new ProfileFragment();
//                FragmentTransaction fragmentTransaction =
//                        getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(HomeFragment.this.getId(),
//                        profileFragment, ProfileFragment.class.getSimpleName());
//                fragmentTransaction.addToBackStack(HomeFragment.class.getSimpleName());
//                fragmentTransaction.commit();
                Intent in = new Intent(getActivity(), ProfileActivity.class);

                startActivity(in);
            }
        });

//        CircleImageView sessionStartImageView =
//                actionbarView.findViewById(R.id.gps_session_start_btn);
//
        CircleImageView gpsSpeedShortcut =
                view.findViewById(R.id.gps_session_start_btn);
//        gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showHelmetAlertDialog();
//                gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        MainActivity.shared().onBackPressed();
//                    }
//                });
//            }
//        });
        gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mIsStopActivity){

                    DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(getContext().getResources().getString(R.string.device1_tv));
                    if(deviceDetails == null || deviceDetails.mac_address == null || !deviceDetails.connected) {
                        Common.okAlertMessage(getContext(), getString(R.string.connect_helmet_dev));
                        return;
                    }else{
                        Common.show_wait_bar(getContext(),"Wait for acknowledgement\nfrom Helmet.");
                        Device1_Parser.sendStopCmd(getContext());
                        Device1_Parser.sendMemoryCmd(getContext());
                    }

//                    inflatePopup(getContext());

                }else{
                    try{
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Alert")
                                .setMessage(getResources().getString(R.string.stop_activity))
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try{
                                            mIsStopActivity = !mIsStopActivity;
                                            gpsSpeedShortcut.setImageResource(R.mipmap.start_session_round);
                                            Device1_Parser.sendStopActivityCmd(getContext());
                                            MainActivity.shared().onBackPressed();
                                        }catch (Exception ex){

                                        }

                                    }
                                })
                                .create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color to blue color
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black));  // Set text color to ligh gray color

                    }catch (Exception e){

                    }
                }

            }
        });



        //Device connectivity and data progress indicator
        CircleProgressView bleStatusProgressView =
                view.findViewById(R.id.ble_device_btn);
        bleStatusProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment deviceFragment = new DeviceFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.replace(HomeFragment.this.getId(),
                        deviceFragment, DeviceFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

//        String userid = UserPreferences.getInstance(getContext()).getUserID();
//
//        new CollectiveSummaryImpl(getContext()).getUserDataByUserID(userid);
//        Common.wait(100);
        //Latest session summary, topmost card
        update_session_card();
        //Update collective summary card
        update_collective_summary();

        view.findViewById(R.id.calendar_icon)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: check on click");
                        if(mlatestSessionSummary.getName().isEmpty()){
                            Common.okAlertMessage(getContext(),"No session yet");
                        }else {
                            Fragment calendarFragment =
//                                    CalendarPagerFragment.newInstance
//                                            (-1, -1);
                            CalendarPagerFragment.newInstance
                                    (MonthlyCalendarFragment.class.getSimpleName(), -1, 2020);
                            FragmentTransaction fragmentTransaction;
                            if (getFragmentManager() != null) {
                                fragmentTransaction = getFragmentManager().beginTransaction();
                                fragmentTransaction.add
                                        (HomeFragment.this.getId(), calendarFragment,
                                                CalendarPagerFragment.class.getSimpleName());
                                fragmentTransaction.addToBackStack(HomeFragment.class.getSimpleName());
                                fragmentTransaction.commit();
                            }
                        }
                    }
                });
    }


    /**
     * Adds action bar events and associated listeners
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerAndCheck(this);
        Log.d(TAG, "onActivityCreated");
        try{
            View actionBarView = this.getView();
            CircleImageView profileShortcut =
                    actionBarView.findViewById(R.id.profile_link);

            CircleProgressView deviceStatusShortcut = (CircleProgressView)
                    actionBarView.findViewById(R.id.ble_device_btn);

//            CircleImageView gpsSpeedShortcut =
//                    actionBarView.findViewById(R.id.gps_session_start_btn);

            CircleImageView logoutMenuShorcut =
                    actionBarView.findViewById(R.id.logout_link);


            ((MainActivity)getActivity()).showProgressCircle( Device_Parser.get_txf_status());

//            UserPreferences userPreferences = UserPreferences.getInstance(getContext());
//
//            if(userPreferences.getProfilePhoto().equals(Constants.DEFAULT_PATH)){
//                ;
//            }
//            else if(profileShortcut != null){
//                profileShortcut.setImageBitmap(BitmapFactory.decodeFile
//                        (userPreferences.getProfilePhoto()));
//            }
//            //Profile can be edited on clicking the profileShortcut
//            if(profileShortcut != null){
//                profileShortcut.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Fragment profileFragment = new ProfileFragment();
//                        FragmentTransaction fragmentTransaction = getFragmentManager()
//                                .beginTransaction();
//                        fragmentTransaction.add(R.id.fragment,
//                                profileFragment,ProfileFragment.class.getSimpleName());
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
//                    }
//                });
//            }


            //BLE connections can be seen and modified on clicking the device view
            deviceStatusShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment deviceFragment = new DeviceFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, deviceFragment,
                            DeviceFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            //Without any view, GPS speed can be logged for reference and recorded in the database

//            gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(getContext().getResources().getString(R.string.device1_tv));
//                    if(deviceDetails == null || deviceDetails.mac_address == null || !deviceDetails.connected) {
//                        Common.okAlertMessage(getContext(), getString(R.string.connect_helmet_dev));
//                        return;
//                    }else{
//                        Device1_Parser.sendMemoryCmd(getContext());
//                    }
////                    AlertDialog helmetDialog = new AlertDialog.Builder(getContext())
////                            .setTitle("Alert")
////                            .setMessage("Have you worn helmet properly?")
////                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    inflatePopup(getContext());
////                                }
////                            })
////                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    dialog.dismiss();
////                                }
////                            })
////                            .create();
////                    helmetDialog.show();
////                    helmetDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor
////                            (Color.parseColor("#FF1B5AAC"));
////                    helmetDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor
////                            (Color.parseColor("#D3D3D3"));
//                }
//            });

            //The last icon on the extreme right, provides a logout option

            logoutMenuShorcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Alert")
                            .setMessage(getResources().getString(R.string.log_out_msg))
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Constants.isPhotoChanged = true;
                                    Common.show_wait_bar(getContext(), "Signing out...");
                                    UserPreferences.getInstance(getContext()).clear();
                                    ProfilePreferences.getInstance(getContext()).clear();
                                    DevicePreferences.getInstance(getContext()).clear();
                                    new CollectiveSummaryPreference(getContext()).clear();
                                    navigateToFragments();
                                }
                            })
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color to blue color
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black));  // Set text color to ligh gray color
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        new WorkMgrHelper(MainApplication.getAppContext()).oneTimeDeleteSessionRequest();
//        new DatabaseHelper.DeleteOldSessions().execute();
//        String userid = UserPreferences.getInstance(getContext()).getUserID();
//        new CollectiveSummaryImpl(getContext()).getUserDataByUserID(userid);

//        new InternetCheck(isInternet -> {
//            if (!isInternet) {
//                Common.isInternetAvailable(getContext());
//            }
//        });
//        Common.wait(50);


//        WorkManager.getInstance(getContext()).cancelAllWorkByTag("Csv_Generation_Workertest");
//        WorkManager.getInstance(getContext()).cancelAllWorkByTag("Csv_Generation_Worker");
//        Common.show_wait_bar(getContext(), "Waiting for cursor");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Common.wait(5000);
//                Common.dismiss_wait_bar();
//            }
//        }).start();

//        Common.lowStorageAlert(getContext());
//        new DatabaseHelper.DeleteButtonBox().execute(3l);
//        if(!isDone){
//            new DatabaseHelper.UpdateGPS().execute();
////            new DatabaseHelper.UpdateMarkerData().execute(4l);
//            isDone = true;
//        }
//        if(!isDone){
//            try{
////                new DatabaseHelper.UpdateSensorData().execute(4l);
////                DatabaseHelper.insertGPS(4l);
//                new DatabaseHelper.DeleteButtonBox().execute(4l);
//                isDone = true;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }



//        new DatabaseHelper.SetSessionSummary().execute();
//        new DatabaseHelper.GetLastPktNum().execute();
//        new DatabaseHelper.DeleteAll().execute((long) 4);
//        if(!isDone){
//            CsvGenerator csvGenerator = new CsvGenerator(getContext());
//            csvGenerator.generateCSV(4);
//            isDone = true;
//        }

//        if(!isDone){
//            new DatabaseHelper.UpdateGPS().execute();
//            isDone = true;
//        }
//        if(!isDone){
//            new DatabaseHelper.UpdateMarkerData().execute((long)1);
//            isDone = true;
//        }
//        if(!isDone){
//            new GetButtonEntities().execute();
//            isDone = true;
//        }

//        new UpdateSessionSummary().execute();
//        new DeleteSensorData().execute();
//        if(!isDone){
//            new UpdateSensorData().execute((long)1000);
//            isDone = true;
//        }
//        new UpdateGpsSpeedAsyncTask().execute();

//       insertGPS();
//        new DatabaseHelper.UpdateAndAddMarker().execute();
//        new DatabaseHelper.UpdateMarkerData().execute(4l);
    }

    @Override
    public void onStart() {
        super.onStart();

//        WorkMgrHelper workMgrHelper = new WorkMgrHelper(getContext());
//        workMgrHelper.oneTimeCSVUploadingRequest();
        //workMgrHelper.oneTimeCSVGenerationRequest();
        DevicePreferences.getInstance(getContext()).lowStorageAlert(getContext());
        Log.d(TAG, "Start");
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        navigateToFragments();
        if( Constants.isPhotoChanged && mProfileImageView != null ){
            Constants.isPhotoChanged = false;
            UserPreferences userPreferences = UserPreferences.getInstance(getContext());

            if(userPreferences.getProfilePhoto().equals(Constants.DEFAULT_PATH)){
                mProfileImageView.setImageResource(R.mipmap.anonymous_round);
            }
            else{
                mProfileImageView.setImageBitmap(BitmapFactory.decodeFile
                        (userPreferences.getProfilePhoto()));
            }
        }
        refresh_cards();
        Log.d(TAG, "Resume");
    }

    public void refresh_cards(){
        if(Constants.isNewSessionAdded){
            Constants.isNewSessionAdded = false;
            update_session_card();
        }
        if(Constants.isUpdateCollectiveSummary){
            Constants.isUpdateCollectiveSummary = false;
            update_collective_summary();
        }
    }

    /**
     * Update the topmost card
     */
    public void update_session_card(){
        if(mHomeFragmentView == null) return;
        View latestSessionSummaryView =
                mHomeFragmentView.findViewById(R.id.latest_activity_summary);
//        if (mLatestSessionSummary == null) {

            try {
                 mlatestSessionSummary =
                        new GetLatestSessionSummaryAsyncTask().execute().get();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(mlatestSessionSummary == null){
                mlatestSessionSummary = new SessionSummary();
            }
//        }
        TimeFmt timeFmt;
        String total_duration;
        if(mlatestSessionSummary != null){
            //Setting card title as date and time in locale setting
            TextView title_text = latestSessionSummaryView.findViewById(R.id.card_title);
            Date date = null;
            if(mlatestSessionSummary.getDate() > 0){
                date = new Date(mlatestSessionSummary.getDate());
            }else{
                date = new Date();
            }
            String dateString = new SimpleDateFormat("MMM dd YYYY HH:MM:SS EEE",
                    Locale.getDefault()).format(date);
            title_text.setText(dateString);

            //Setting session name as retrieved from the DB
            TextView session_name = latestSessionSummaryView.findViewById(R.id.session_name_card_tv);
            if(mlatestSessionSummary.getName().isEmpty()){
                session_name.setText("-");
            }else{
                session_name.setText(mlatestSessionSummary.getName());
            }

//            String sessionNameStr =
//                    "<b>"+getString(R.string.session_name_desc)+ "</b>"+ latestSessionSummary.getName();
//            session_name.setText(Html.fromHtml(sessionNameStr));


            TextView activity_type = latestSessionSummaryView.findViewById(R.id.type_of_activity_tv);
//        String activityTypeStr =
//                "Activity Type : " + latestSessionSummary.getActivity_type() + "";
            String activityTypeStr = String.format(Locale.getDefault(), "%s",
                     Constants.ActivityCodeMap.inverse().get(mlatestSessionSummary.getActivity_type()));
            activity_type.setText(Html.fromHtml(activityTypeStr));
//                    getString(R.string.activity_type_desc) + Constants.ActivityCodeMap.inverse().get(latestSessionSummary.getActivity_type());
            activity_type.setText(activityTypeStr);


            TextView duration =
                    latestSessionSummaryView.findViewById(R.id.duration_tv);
            timeFmt = Common.convertToTimeFmt((long)(mlatestSessionSummary.getDuration()*1000));
            total_duration =
                    String.format(Locale.getDefault(), "%02d:%02d:%02d", timeFmt.hr, timeFmt.min, timeFmt.sec);//+collective_summary_info.get(1).toString();

            String durationStr =  total_duration.toString();
            duration.setText(durationStr);

            TextView total_dataTV = latestSessionSummaryView.findViewById(R.id.total_data_tv);
            String totalDataStr = String.valueOf(mlatestSessionSummary.getSize());
            total_dataTV.setText(totalDataStr);

            String samplingRate = String.valueOf(mlatestSessionSummary.getSampling_freq());
            TextView samplingFrequency =
                    latestSessionSummaryView.findViewById(R.id.sampling_rate_tv);
            samplingFrequency.setText(samplingRate);

            String typesOfData = "0";
            if(mlatestSessionSummary.getActivity_type() > 0){
                typesOfData = String.valueOf(Constants.typesOfData);
            }

            TextView types_of_data_tv =
                    latestSessionSummaryView.findViewById(R.id.types_of_data_tv);
            types_of_data_tv.setText(typesOfData);

            String oneLineNote =  mlatestSessionSummary.getNote();
            TextView note =
                    latestSessionSummaryView.findViewById(R.id.text_note_summary_line_tv);
            if(!oneLineNote.isEmpty()) {
                note.setText(oneLineNote);
            }else{
                note.setText("-");
            }

            if(mlatestSessionSummary != null){
                TextView firmware_type = mHomeFragmentView.findViewById(R.id.firmware_type);
                TextView firmware_ver = mHomeFragmentView.findViewById(R.id.firmware_ver);
                LinearLayout firmware_details = mHomeFragmentView.findViewById(R.id.firmware_details);
                int type = mlatestSessionSummary.getFirmware_type();
                if(type >= 0){
                    if(Constants.FirmwareTypeMap.get(type)!= null){
                        firmware_type.setText(Constants.FirmwareTypeMap.get(type));
                    }
                    firmware_ver.setText(String.format(Locale.getDefault(), "Version %.2f", mlatestSessionSummary.getFirmware_ver()));
                    if(type == 0){
                        firmware_details.setBackgroundTintList(ColorStateList.valueOf(getColor(getContext(), R.color.red)));
                    }else{
                        firmware_details.setBackgroundTintList(ColorStateList.valueOf(getColor(getContext(), R.color.green)));
                    }
                }else{
                    firmware_type.setText("-");
                    firmware_ver.setText("-");
                    firmware_details.setBackgroundTintList(ColorStateList.valueOf(getColor(getContext(), R.color.colorPrimary)));
//                    view.setBackgroundColor(getColor(getContext(), R.color.offgray));
//                    firmware_type.setBackgroundColor(getColor(getContext(), R.color.gray));
//                    firmware_ver.setBackgroundColor(getColor(getContext(), R.color.gray));
                }

            }
        }
        final SessionSummary sessionSummary_f = mlatestSessionSummary;
        mHomeFragmentView.findViewById(R.id.latest_activity_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sessionSummary_f.getName().isEmpty()){
                    Common.okAlertMessage(getContext(),"No session yet");
                }else {
                    Fragment mapFragment =
                            MapFragment.newInstance(Constants.ActivityCodeMap.inverse().get(sessionSummary_f.getActivity_type()), sessionSummary_f.getSession_id(), sessionSummary_f.getDuration());
                    FragmentTransaction fragmentTransaction = null;
                    if (getFragmentManager() != null) {
                        fragmentTransaction = getFragmentManager()
                                .beginTransaction();
                        fragmentTransaction.replace(HomeFragment.this.getId(),
                                mapFragment,
                                MapFragment.class.getSimpleName());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }

    /**
     * Update the collective summary card
     */
    public void update_collective_summary(){
        if(mHomeFragmentView == null) return;
        View collective_summary_view =
                mHomeFragmentView.findViewById(R.id.collective_summary);
        TextView summary_title =
                collective_summary_view.findViewById(R.id.collective_summary_card_title);
        summary_title.setText(getString(R.string.collective_Summary_title_tv));

        //TODO uncomment this when more collective session fields are finalized


        try {
            List<Float> collective_summary_info =
                    new CollectiveSummaryPreference(getContext()).getCollSum();
//                    new GetCollectiveSummaryDetailsAsyncTask().execute().get();


//            collective_summary_view.findViewById(R.id.session_name_tv).setVisibility(View.GONE);
            TimeFmt timeFmt;
            String total_duration;
            TextView nSessions_tv =
                    collective_summary_view.findViewById(R.id.number_sessions_tv);
            Float nSessionCount = collective_summary_info.get(0);
            int sessionCount = nSessionCount.intValue();
//            String nSessions =
//                    getString(R.string.n_sessions) + sessionCount;
            nSessions_tv.setText(String.valueOf(sessionCount));

            TextView total_duration_tv =
                    collective_summary_view.findViewById(R.id.total_duration_tv);
            timeFmt = Common.convertToTimeFmt((long)(collective_summary_info.get(1)*1000));
            total_duration =
                    /*getString(R.string.total_duration_desc) + */String.format(Locale.getDefault(), "%02d:%02d:%02d", timeFmt.hr, timeFmt.min, timeFmt.sec);
            total_duration_tv.setText(total_duration);

            TextView total_data_tv =
                    collective_summary_view.findViewById(R.id.total_data_tv);
            float total_data = (float)collective_summary_info.get(2);
//            String total_data_str =
//                    getString(R.string.collective_summary_total_data) + (int)total_data;
            total_data_tv.setText(String.valueOf((int)total_data));

            TextView activities_tv =
                    collective_summary_view.findViewById(R.id.types_of_activity_tv);
            int activityTypeCOunt = (collective_summary_info.get(3)).intValue();
//            String activities =
//                    getString(R.string.activity_types_desc) + activityTypeCOunt;
//                    "Activity Types : " + Constants.ActivityCodeMap.inverse().get(52)
//                    +(Constants.ActivityCodeMap.inverse().get(50));
            activities_tv.setText(String.valueOf(activityTypeCOunt));
//
//            CollectiveSummaryReq collectiveSummaryReq = new CollectiveSummaryReq();
//            collectiveSummaryReq.total_duration =  collective_summary_info.get(1).longValue();
//            collectiveSummaryReq.total_size =  collective_summary_info.get(2).longValue();
//            collectiveSummaryReq.total_sessions =  7L;
//            List<Long> longs = new ArrayList<>();longs.add(11L);longs.add(52L);
//            collectiveSummaryReq.activity_types = longs;
//            CollectiveSummaryPreference collectiveSummaryPreference = new CollectiveSummaryPreference(getContext());
//            collectiveSummaryPreference.setActCodes(longs);
//            collectiveSummaryPreference.setTotSize(collectiveSummaryReq.total_size);
//            collectiveSummaryPreference.setTotSession(collectiveSummaryReq.total_sessions);
//            collectiveSummaryPreference.setTotDuration(collectiveSummaryReq.total_duration);
//            String userid = UserPreferences.getInstance(getContext()).getUserID();
//            new CollectiveSummaryImpl(getContext()).updateUserData(userid, collectiveSummaryReq);


        } catch (Exception e) {
            e.printStackTrace();
        }


        //Navigation to session summary page on clicking the collective summary
        collective_summary_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment sevenSessionsSummaryFragment = new SevenSessionsSummaryFragment();
                FragmentTransaction fragmentTransaction;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(HomeFragment.this.getId(),
                            sevenSessionsSummaryFragment,
                            SevenSessionsSummaryFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d(TAG, "Detach");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onGpsButtonPressed(String TAG,String message) {
        if (mListener != null) {
            mListener.onSessionStarted(TAG,message);
        }
    }

    /**
     * Check the bluetooth's availability before reconnection the recent connected device.
     */
//    private void autoConnection(){
//        // Get BluetoothManager for getting bluetoothAdapter object
//        final BluetoothManager bluetoothManager =
//                (BluetoothManager) MainActivity.shared().getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported o the device.
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(getContext(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
//
//        }
//        if (!((MainActivity)getActivity()).mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
//            return ;
//
//        }
//
//    }



    private void inflatePopup(Context context) {

//        if(context != null){
//            PopupMenu gpsSpeedPopupMenu = new PopupMenu(context,null);
//            MenuInflater inflater = gpsSpeedPopupMenu.getMenuInflater();
//            inflater.inflate(R.menu.gps_speed_menu_options, gpsSpeedPopupMenu.getMenu());
//            gpsSpeedPopupMenu.show();
//        }

        ViewGroup root = (ViewGroup) getActivity().getWindow()
                .getDecorView().getRootView();
        applyDim(root, 0.5f);

        if(context != null){

            //PopupWindow initialized here
            PopupWindow startGpsSpeedPopupWindow = new PopupWindow(context);
            //PopupWindow layout inflated here
            View startGpsSpeedPopupView = getLayoutInflater().inflate
                    (R.layout.start_gps_popup_layout,null);

            startGpsSpeedPopupWindow.setBackgroundDrawable
                    (getResources().getDrawable(R.drawable.background_rectangle));


            //Initially start button is disabled to prevent null entry
            Button startButton = startGpsSpeedPopupView.findViewById(R.id.gps_speed_start);

            //Add configurations to the indoor sport spinner
            Spinner indoorSportsSpinner = startGpsSpeedPopupView.findViewById(R.id.indoor_sport_options);

            //Initially only the outdoor spinner has "cycling" as a selection.
            //No other view is available for selection.
            indoorSportsSpinner.setEnabled(false);

            //The array adapter populate the views with spinner values
            ArrayAdapter indoorSportsAdapter = ArrayAdapter.createFromResource
                    (context,R.array.indoor_sports_array,R.layout.list_item_view_gps_speed);

            //Further initializations for the indoorSportsSpinner.
            indoorSportsAdapter.setDropDownViewResource(R.layout.list_item_view_gps_speed);
            indoorSportsSpinner.setAdapter(indoorSportsAdapter);

            /*
            If an item is selected, if it is other and it is re-selected, an edit text box
            appears in the same window.
             */

            //TODO Replace popup window instances with popupDialog

            indoorSportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView itemText = (TextView)view;
                    mActivityType = itemText.getText().toString().trim()+"_"+Constants.INDOOR;
                    prev_indoor_selected = itemText.getText().toString().trim()+"_"+Constants.INDOOR;;
                    /*IF the item is "OTHER", then all other edittext boxes should be disabled
                    As long as there is no text, the startbutton cannot be used
                     */

                    if(itemText.getText().toString().equalsIgnoreCase
                            (getString(R.string.other_sports))){
                        EditText indoorSportsEditText = startGpsSpeedPopupView.findViewById(R.id.indoor_other_sport_et);

                        startButton.setEnabled(false);

                        indoorSportsEditText.setVisibility(View.VISIBLE);
                        startGpsSpeedPopupWindow.setFocusable(true);
                        startGpsSpeedPopupWindow.update();

//                        indoorSportsEditText.setEnabled(true);

                        indoorSportsEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startButton.setEnabled(false);
                            }
                        });
                        indoorSportsEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if(indoorSportsEditText.getText().toString().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(indoorSportsEditText.getText().toString().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(indoorSportsEditText.getText().toString().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }
                        });
                    }else{
                        EditText editTextIndoor = (EditText) startGpsSpeedPopupView.findViewById
                                (R.id.indoor_other_sport_et);
                        editTextIndoor.setVisibility(View.GONE);
                        startButton.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    startButton.setEnabled(false);
                }
            });

            //Same set of configurations for the outdoor-sport spinner.
            Spinner outdoorSportsSpinner = startGpsSpeedPopupView.findViewById(R.id.outdoor_sport_options);
            ArrayAdapter outdoorSportsAdapter = ArrayAdapter.createFromResource
                    (context,R.array.outdoor_sports_array,R.layout.list_item_view_gps_speed);
            outdoorSportsAdapter.setDropDownViewResource(R.layout.list_item_view_gps_speed);
            outdoorSportsSpinner.setAdapter(outdoorSportsAdapter);

            outdoorSportsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TextView itemText = (TextView)view;
                    mActivityType = itemText.getText().toString().trim()+"_"+Constants.OUTDOOR;
                    prev_outdoor_selected = itemText.getText().toString().trim()+"_"+Constants.OUTDOOR;
                    startButton.setEnabled(false);

                    if(itemText.getText().toString().equalsIgnoreCase
                            (getString(R.string.other_sports))){
                        /*
                       The outdoor "other" sport spinner is set to editable
                         */
                        EditText outdoorEditText = startGpsSpeedPopupView.findViewById(R.id.outdoor_other_sport_et);
                        startGpsSpeedPopupWindow.setFocusable(true);
                        startGpsSpeedPopupWindow.update();
                        outdoorEditText.setVisibility(View.VISIBLE);

                        outdoorEditText.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                if(outdoorEditText.getText().toString().trim().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(outdoorEditText.getText().toString().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if(outdoorEditText.getText().toString().equals("")) {
                                    startButton.setEnabled(false);
                                }else{
                                    startButton.setEnabled(true);
                                }
                            }
                        });

                    }else{
                        startGpsSpeedPopupView.findViewById(R.id.outdoor_other_sport_et)
                                .setVisibility(View.GONE);
                        startButton.setEnabled(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });

            RadioButton outdoorSportsRadioButton = startGpsSpeedPopupView
                    .findViewById(R.id.outdoor_sport_choice);
            outdoorSportsRadioButton.setChecked(true);


            startGpsSpeedPopupView.findViewById(R.id.close_gps_speed_popup).setOnClickListener(v -> {
                startGpsSpeedPopupWindow.dismiss();
            });

            startGpsSpeedPopupView.findViewById(R.id.gps_speed_start).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(getActivity() != null
                            && mActivityType.toLowerCase().contains(getString(R.string.outdoor)) && !Common.isGpsEnable(getActivity())){
                        Toast.makeText(getContext(), getString(R.string.enable_gps), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mIsStopActivity = !mIsStopActivity;
                    Common.ACT_TYPE = mActivityType;
                    //Log.d(TAG, "Activity :"+activityType+", code="+Constants.ActivityCodeMap.get(activityType));
                    if(Constants.ActivityCodeMap.get(mActivityType) != null){
                        Device1_Parser.sendStopCmd(getContext());
                        ButtonBox_Parser.sendStopCmd(getContext());
                        Device1_Parser.sendStartActivityCmd(getContext(), Constants.ActivityCodeMap.get(mActivityType));
                    }else{
                        Log.d(TAG, "No activity code found");
                    }

                    CircleImageView gpsSpeedButton =
                            HomeFragment.this.getView().findViewById(R.id.gps_session_start_btn);
                    gpsSpeedButton.setImageResource(R.mipmap.pause_round);

                    Fragment gpsFragment = new GPSSpeedFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.fragment, gpsFragment,
                            GPSSpeedFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    startGpsSpeedPopupWindow.dismiss();
                    onGpsButtonPressed(TAG,"Checking for indoor or outdoor");
                }
            });

            RadioButton indoorRadioButton = startGpsSpeedPopupView.findViewById
                    (R.id.indoor_sport_choice);
            indoorRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    indoorSportsSpinner.setEnabled(true);
                    if(!indoorSportsSpinner.getSelectedItem().toString().
                            equals(getString(R.string.other_sports))){
                        startButton.setEnabled(true);
                    }
                    EditText outdoorEditText =
                            startGpsSpeedPopupView.findViewById(R.id.outdoor_other_sport_et);
                    EditText indoorEditText =
                            startGpsSpeedPopupView.findViewById(R.id.indoor_other_sport_et);
                    if(outdoorEditText.getVisibility() == View.VISIBLE){
                        outdoorEditText.setEnabled(false);
                    }

                    if(indoorEditText.getVisibility() == View.VISIBLE){
                        indoorEditText.setEnabled(true);
                        if(indoorEditText.getText().toString().equals("")){
                            startButton.setEnabled(false);
                        }
                    }
                }
            });

            indoorRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        indoorSportsSpinner.setEnabled(false);
                    }else{
                        mActivityType = prev_indoor_selected;
                    }
                }
            });

            outdoorSportsRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!outdoorSportsSpinner.getSelectedItem().toString().
                            equals(getString(R.string.other_sports))){
                        startButton.setEnabled(true);
                    }else{
                        startButton.setEnabled(false);
                    }
                    outdoorSportsSpinner.setEnabled(true);
                    EditText indoorEditText =
                            startGpsSpeedPopupView.findViewById(R.id.indoor_other_sport_et);
                    EditText outdoorEditText =
                            startGpsSpeedPopupView.findViewById(R.id.outdoor_other_sport_et);
                    if(indoorEditText.getVisibility() == View.VISIBLE){
                        indoorEditText.setEnabled(false);
                    }
                    if(outdoorEditText.getVisibility() == View.VISIBLE){
                        outdoorEditText.setEnabled(true);
                        if(outdoorEditText.getText().toString().equals("")){
                            startButton.setEnabled(false);
                        }
                    }
                }
            });


            outdoorSportsRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        outdoorSportsSpinner.setEnabled(false);
                    }else{
                        mActivityType = prev_outdoor_selected;
                    }
                }
            });

            startGpsSpeedPopupWindow.setContentView(startGpsSpeedPopupView);
            startGpsSpeedPopupWindow.showAtLocation(getView(), Gravity.CENTER, 10, 10);
            startGpsSpeedPopupWindow.setBackgroundDrawable
                    (new ColorDrawable(Color.YELLOW));
            startGpsSpeedPopupWindow.setElevation(30.0f);
            startGpsSpeedPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    clearDim(root);
                }
            });
        }
    }



    private void navigateToFragments(){

        try{
            if (UserPreferences.getInstance(getContext()).getPhone().length() == 0){
//                Fragment fragment = new LoginFragment();
//                FragmentTransaction fragmentTransaction = getActivity().
//                        getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment, fragment);
//                fragmentTransaction.addToBackStack(HomeFragment.class.getSimpleName());
//                fragmentTransaction.commit();
                Intent in = new Intent(getActivity(), LoginActivity.class);
                startActivity(in);
            }else{
                //ToDo: validate Profile details, if no information, the navigate to Profile page
                if ( ProfilePreferences.getInstance(getContext()).isEmpty()) {
//                    Fragment profileFragment = new ProfileFragment();
//                    FragmentTransaction fragmentTransaction = getFragmentManager()
//                            .beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment, profileFragment,"Profile Fragment");
//                    fragmentTransaction.addToBackStack(HomeFragment.class.getSimpleName());
//                    fragmentTransaction.commit();
                    Intent in = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(in);
                }
            }
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onSessionStarted(String TAG,String message);
    }

    void registerAndCheck(Object helper) {
        if (!mBus.isRegistered(helper)) {
            mBus.register(helper);
        }
    }

    /**
     * If more memory usage, then no activity start and ask user to transfer the data
     * @param memoryUsage
     */
    public void onEvent(final MemoryUsage memoryUsage) {
        Common.dismiss_wait_bar();
        Log.d(TAG, "Memory usage: ss="+memoryUsage.session_summaries+", pm="+memoryUsage.packet_memory);
        // If one of datapacket's memory or session summaries' memory usage is more than 90% , then no activity
            MainActivity.shared().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    if(memoryUsage.packet_memory > 90 || memoryUsage.session_summaries > 90) {
                        message = getString(R.string.out_of_memory_dev1);
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Alert")
                                .setMessage(message)
                                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(memoryUsage.packet_memory > 90 || memoryUsage.session_summaries > 90) {
                                            Device1_Parser.sendNotificationCmd(getContext());
                                        }
                                    }
                                })
                                .create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color to blue color
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black));  // Set text color to ligh gray color
                    }else{
                        message = getString(R.string.worn_helmet);
                        AlertDialog dialog = new AlertDialog.Builder(getContext())
                                .setTitle("Alert")
                                .setMessage(message)
                                .setNegativeButton("No",null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        inflatePopup(getContext());
                                    }
                                }).create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color to blue color
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getContext(), R.color.black));  // Set text color to ligh gray color
                    }
                }
            });
    }
}

