package com.decalthon.helmet.stability.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.BLE.ButtonBox_Parser;
import com.decalthon.helmet.stability.BLE.Device1_Parser;
import com.decalthon.helmet.stability.BLE.Device_Parser;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.DeviceModels.MemoryUsage;
import com.decalthon.helmet.stability.preferences.DevicePreferences;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.responses.ErrorCodes;
import com.decalthon.helmet.stability.webservice.responses.ErrorMessages;
import com.decalthon.helmet.stability.webservice.services.AvatarService;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import at.grabner.circleprogress.CircleProgressView;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.decalthon.helmet.stability.Utilities.ViewDimUtils.applyDim;
import static com.decalthon.helmet.stability.Utilities.ViewDimUtils.clearDim;


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
    private String activityType = "";
     private static SessionSummary latestSessionSummary;
    //A BLE device list adapter instance
    private BluetoothAdapter mBluetoothAdapter;
    //Event bus instance use for onEvent actions
    private EventBus mBus = EventBus.getDefault();

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
        actionbarView = view.findViewById(R.id.home_action_bar);

        CircleImageView profileImageView = view.findViewById(R.id.profile_btn);

        //Restoration of profile photo from shared preferences
        UserPreferences userPreferences = UserPreferences.getInstance(getContext());

        profileImageView.setImageBitmap(BitmapFactory.decodeFile
                (userPreferences.getProfilePhoto()));
        if(userPreferences.getProfilePhoto().equals("default")){
            ;
        }
        else{
            profileImageView.setImageBitmap(BitmapFactory.decodeFile
                    (userPreferences.getProfilePhoto()));
        }
        //Navigation to profile edit page
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment profileFragment = new ProfileFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.add(HomeFragment.this.getId(), profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        CircleImageView sessionStartImageView =
                actionbarView.findViewById(R.id.gps_session_start_btn);

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
//        gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog helmetDialog = new AlertDialog.Builder(getContext())
//                        .setTitle("Alert")
//                        .setMessage("Have you worn helmet properly?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                inflatePopup(getContext());
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create();
//                helmetDialog.show();
//                helmetDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor
//                        (Color.parseColor("#FF1B5AAC"));
//                helmetDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor
//                        (Color.parseColor("#D3D3D3"));
//            }
//        });



        //Device connectivity and data progress indicator
        CircleProgressView bleStatusProgressView =
                view.findViewById(R.id.ble_device_btn);
        bleStatusProgressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment deviceFragment = new DeviceFragment();
                FragmentTransaction fragmentTransaction =
                        getFragmentManager().beginTransaction();
                fragmentTransaction.add(HomeFragment.this.getId(),
                        deviceFragment, DeviceFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        //Latest session summary, topmost card
        View latestSessionSummaryView =
                view.findViewById(R.id.latest_activity_summary);
        if (latestSessionSummary == null) {
            try {
                latestSessionSummary =
                        new GetLatestSessionSummaryAsyncTask().execute().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Setting card title as date and time in locale setting
        TextView title_text = latestSessionSummaryView.findViewById(R.id.card_title);
        Date date = new Date(latestSessionSummary.getDate());
        String dateString = new SimpleDateFormat("MMM dd YYYY HH:MM:SS EEE",
                Locale.getDefault()).format(date);
        title_text.setText(dateString);

        //Setting session name as retrieved from the DB
        TextView session_name = latestSessionSummaryView.findViewById(R.id.session_name_tv);
        String sessionNameStr =
                getString(R.string.session_name_desc) + latestSessionSummary.getName();
        session_name.setText(sessionNameStr);


        TextView activity_type = latestSessionSummaryView.findViewById(R.id.activity_type_tv);
//        String activityTypeStr =
//                "Activity Type : " + latestSessionSummary.getActivity_type() + "";
        String activityTypeStr =
                getString(R.string.activity_type_desc) + Constants.ActivityCodeMap.inverse().get(52);
        activity_type.setText(activityTypeStr);

        TextView duration =
                latestSessionSummaryView.findViewById(R.id.duration_tv);
        String durationStr =
                getString(R.string.duration_desc) + latestSessionSummary.getDuration() + "";
        duration.setText(durationStr);

        TextView total_dataTV = latestSessionSummaryView.findViewById(R.id.total_data_tv);
        String totalDataStr =
                getString(R.string.total_data_desc) + (latestSessionSummary.getTotal_data() / 1024);
        total_dataTV.setText(totalDataStr);

        String samplingRate = getString(R.string.sampling_frequency_desc) +
                String.valueOf(latestSessionSummary.getSampling_freq());
        TextView samplingFrequency =
                latestSessionSummaryView.findViewById(R.id.sampling_rate_tv);
        samplingFrequency.setText(samplingRate);

        String oneLineNote =  getString(R.string.note_desc)  + latestSessionSummary.getNote();
        TextView note =
                latestSessionSummaryView.findViewById(R.id.text_note_summary_line_tv);
        note.setText(oneLineNote);
//        if(note.get)
        //A click on the first card view navigates to tracker
        //(A) Outdoor tracker is the GPS map view
        //(B) Indoor tracker is the TimelineView
        view.findViewById(R.id.latest_activity_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapFragment = MapFragment.newInstance(" SESSION 7", "Latest session");
                FragmentTransaction fragmentTransaction = null;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(HomeFragment.this.getId(), mapFragment,
                            MapFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        //This is the second card view - collective summary
        View collective_summary_view =
                view.findViewById(R.id.collective_summary);
        TextView summary_title =
                collective_summary_view.findViewById(R.id.card_title);
        summary_title.setText(getString(R.string.collective_Summary_title_tv));

        try {
            List<Float> collective_summary_info =
                    new GetCollectiveSummaryDetailsAsyncTask().execute().get();

            collective_summary_view.findViewById(R.id.session_name_tv).setVisibility(View.GONE);

            TextView activities_tv =
                    collective_summary_view.findViewById(R.id.activity_type_tv);
            String activities =
                    "Activity Types : " + collective_summary_info.get(3).toString();
//                    "Activity Types : " + Constants.ActivityCodeMap.inverse().get(52)
//                    +(Constants.ActivityCodeMap.inverse().get(50));
            activities_tv.setText(activities);

            TextView total_duration_tv =
                    collective_summary_view.findViewById(R.id.duration_tv);
            String total_duration =
                    "Total Duration : " + collective_summary_info.get(1).toString();
            total_duration_tv.setText(total_duration);

            TextView total_data_tv =
                    collective_summary_view.findViewById(R.id.total_data_tv);
            float total_data = (float)collective_summary_info.get(0);
            String total_data_str =
                    "Total Data (KB):" + (int)total_data;
            total_data_tv.setText(total_data_str);

        } catch (ExecutionException | InterruptedException e) {
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


        //Calendar icon, clicking on calendaar icon navigates to daywise
        // ..calendar
        view.findViewById(R.id.calendar_icon)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: check on click");
                        Fragment calendarFragment =
                                CalendarPagerFragment.newInstance
                                        (MonthlyCalendarFragment.class.getSimpleName(), null);
                        FragmentTransaction fragmentTransaction =
                                null;
                        if (getFragmentManager() != null) {
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.add
                                    (HomeFragment.this.getId(), calendarFragment,
                                            CalendarPagerFragment.class.getSimpleName());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
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

            CircleImageView gpsSpeedShortcut =
                    actionBarView.findViewById(R.id.gps_session_start_btn);

            CircleImageView logoutMenuShorcut =
                    actionBarView.findViewById(R.id.logout_link);

            /*CircleImageView backLink =
                    actionBarView.findViewById(R.id.back_link);
            backLink.setVisibility(View.GONE);*/

            ((MainActivity)getActivity()).showProgressCircle( Device_Parser.get_txf_status());

            UserPreferences userPreferences = UserPreferences.getInstance(getContext());

            if(userPreferences.getProfilePhoto().equals("default")){
                ;
            }
            else{
                profileShortcut.setImageBitmap(BitmapFactory.decodeFile
                        (userPreferences.getProfilePhoto()));
            }
            //Profile can be edited on clicking the profileShortcut

            profileShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(R.id.fragment,
                            profileFragment,ProfileFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            //BLE connections can be seen and modified on clicking the device view
            deviceStatusShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment deviceFragment = new DeviceFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(R.id.fragment, deviceFragment,
                            DeviceFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            //Without any view, GPS speed can be logged for reference and recorded in the database

            gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(getContext().getResources().getString(R.string.device1_tv));
                    if(deviceDetails == null || deviceDetails.mac_address == null || !deviceDetails.connected) {
                        Common.okAlertMessage(getContext(), getString(R.string.connect_helmet_dev));
                        return;
                    }else{
                        Device1_Parser.sendMemoryCmd(getContext());
                    }
//                    AlertDialog helmetDialog = new AlertDialog.Builder(getContext())
//                            .setTitle("Alert")
//                            .setMessage("Have you worn helmet properly?")
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    inflatePopup(getContext());
//                                }
//                            })
//                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .create();
//                    helmetDialog.show();
//                    helmetDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor
//                            (Color.parseColor("#FF1B5AAC"));
//                    helmetDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor
//                            (Color.parseColor("#D3D3D3"));
                }
            });

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
                                    UserPreferences.getInstance(getContext()).clear();
                                    ProfilePreferences.getInstance(getContext()).clear();
                                    DevicePreferences.getInstance(getContext()).clear();
                                    navigateToFragments();
                                }
                            })
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FF1B5AAC")); // Set text color to blue color
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#D3D3D3"));  // Set text color to ligh gray color
                }
            });
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
//        new InternetCheck(isInternet -> {
//            if (!isInternet) {
//                Common.isInternetAvailable(getContext());
//            }
//        });
//        Common.wait(50);

        navigateToFragments();
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
//                //new DatabaseHelper.UpdateSensorData().execute(4l);
//                DatabaseHelper.insertGPS(4l);
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

        Log.d(TAG, "Resume");
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
                    activityType = itemText.getText().toString().trim()+"_"+Constants.INDOOR;
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
                    activityType = itemText.getText().toString().trim()+"_"+Constants.OUTDOOR;

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
                    Common.ACT_TYPE = activityType;
                    //Log.d(TAG, "Activity :"+activityType+", code="+Constants.ActivityCodeMap.get(activityType));
                    if(Constants.ActivityCodeMap.get(activityType) != null){
                        Device1_Parser.sendStopCmd(getContext());
                        ButtonBox_Parser.sendStopCmd(getContext());
                        Device1_Parser.sendStartActivityCmd(getContext(), Constants.ActivityCodeMap.get(activityType));
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

//    /**
//     * The progress bar shall indicate the following:
//     * 1. Context: The Fragment context
//     * 2. The total change in value in percentage measure
//     * 3. The number of devices that are connected.
//     *
//     * Data volumes are computed as follows:
//     * 1. For All devices, the structured data is grouped as byte received.
//     * 2. The quantum change in available data is calculated as per the total burst data
//     * 3. The remaining data is calculated over full capacity.
//     * 4. It is also necessary to present a device as connected or disconnected.
//     *
//     * Algo:
//     * If (context.valid){
//     *      any_devices_connected = false
//     *      devices_connected_count = 0
//     *      for(device_id : Devices){
//     *          device_connected = connection_status.get(device_id)
//     *          .checkConnection()
//     *          if(device_connected.isTrue){
//     *              devices_connected += 1
//     *          }
//     *      }
//     *      if(devices_connected.isFalse){
//     *          //No progress possible.
//     *          return
//     *      }else if()
//     *      and devices_connected <= NUM_DEVICES) {
//     *      }else{
//     *          print ("")
//     *      }
//     * }
//     * @param context
//     * @param increase
//     */
//    public void showProgressCircle(Context context,float increasePercent){
//
//        boolean anyDeviceConnected = false;
//        int deviceConnectedCount = 0;
//        CircleProgressView dataLoadProgressView;
//        ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
//        dataLoadProgressView = actionbar.getCustomView().findViewById
//                (R.id.ble_device_connectivity);
//        if(context != null){
//            for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet() ){
//                System.out.println("Print DEVICE "+entry.getKey());
//                if(entry.getValue().connected){
//                    deviceConnectedCount += 1;
//                    anyDeviceConnected = true;
//                }
//            }
//            if(!anyDeviceConnected){
//                dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
//                dataLoadProgressView.setValueAnimated(24,1000);
//                return;
//            }else{
//                switch(deviceConnectedCount){
//                    case 1:
//                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.yellow));
//                        break;
//                    case 2:
//                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.orange));
//                        break;
//                    case 3:
//                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.green));
//                        break;
//                    default:
//                        dataLoadProgressView.setFillCircleColor(getResources().getColor(R.color.red));
//                        dataLoadProgressView.setFillCircleColor(232);
//                }
//            }
//        }
//    }

//        if(context != null){
//            //These are the inputs to the objectanimator
//            //1. A view that has to be rotated 3-d
//            //2. The rotation parameter.
//            //3. The extent of rotation.
//            CoordinatorLayout progressImageLayout =
//                    (CoordinatorLayout) mActionBar.getCustomView().findViewById(R.id.ble_device_connectivity);
//
//            int maxHeight = progressImageLayout.getLayoutParams().height;
//            ImageView tempView = mActionBar.getCustomView().findViewById
//                    (R.id.ble_device_connectivity_dev1);
//            int maxWidth = tempView.getWidth();
//            ImageView outerCircle = mActionBar.getCustomView().findViewById
//                    (R.id.ble_device_data_progress_dev1);
////            ImageView middleCircle = /
////            int [] progressCircleHeights = new int[3];
//
////            ImageView tempView =
////                    progressImageLayout.findViewById(R.id.device_data_level1);
////            progressCircleHeights[0] = tempView.getHeight();
////
////            progressCircleHeights[1] =
////                    progressImageLayout.findViewById(R.id.device_data_level2).getHeight();
////            progressCircleHeights[2] =
////                    progressImageLayout.findViewById(R.id.device_data_level3).getHeight();
////            int commonCenter = progressImageLayout.getWidth() / 2;
//
////
////            progressImageLayout.setPivotX
////                    (progressImageLayout.getWidth()/2);
////            progressImageLayout.setPivotY
////                    (progressImageLayout.getHeight()/2);
////            getActivity().runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    float rotationDone = 0f;
////                    if(progressImageLayout != null) {
////                        while(rotationDone < 360) {
////                            rotationDone += 30;
////                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat
////                                    (progressImageLayout, "rotation", rotationDone);
////                            objectAnimator.setDuration(10);
////                            objectAnimator.start();
////                            try {
////                                Thread.sleep(1000);
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////                }
////            });
//
//
//                float rotationDone = 30;
//                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat
//                        (outerCircle,"rotation" ,increase);
//
//                objectAnimator.setDuration(3000);
//                objectAnimator.start();
//            }
//        }

//        if(context != null){
//            //Write the concentric circle logic here
//
//            //Create a set of drawables.. Here a drawable is used.
////            Drawable circularProgressDrawable =
////                    getResources().getDrawable(R.drawable.concentric_progress);
//            ImageView deviceDataStatus =
//                    mActionBar.getCustomView().findViewById(R.id.ble_device_connectivity);
//
////            ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
//                    deviceDataStatus.setImageDrawable
//                            (getResources().getDrawable(R.drawable.concentric_progress,null));
//            LayerDrawable progressLayerDrawable = (LayerDrawable) deviceDataStatus.getDrawable();
//            RotateDrawable rotateDrawable = (RotateDrawable) progressLayerDrawable.getDrawable(1);
//            ObjectAnimator mAnimator = ObjectAnimator.ofFloat
//                    (progressLayerDrawable,"rotation",0,360f);
//            mAnimator.setDuration(300);
//            mAnimator.start();
//
////                    AnimatedVectorDrawable animationDrawable = (AnimatedVectorDrawable) getResources().
////                            getDrawable(R.drawable.animate_progress);
////                    deviceDataStatus.setImageDrawable(animationDrawable);
////                    AnimatedVectorDrawable animatedVectorDrawable =
////                            (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.animate_progress);
////
////                    animationDrawable.start();
////                }
////            });
////            //Obtain height and weight of the circular area
////            float width = deviceDataStatus.getWidth();
////            float height = deviceDataStatus.getHeight();
////            float startAngle = 0;
////
////            //Pass percentage as an argument
////            percentage = percentage + 10;
////            float endAngle = percentage / 100;
////            float radius = 0.4f * width;
//
//
//
//        }


    private void navigateToFragments(){

        try{
            if (UserPreferences.getInstance(getContext()).getPhone().length() == 0){
                Fragment fragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = getActivity().
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment, fragment);
                fragmentTransaction.addToBackStack(LoginFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }else{
                //ToDo: validate Profile details, if no information, the navigate to Profile page
                if ( ProfilePreferences.getInstance(getContext()).isEmpty()) {
                    Fragment profileFragment = new ProfileFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, profileFragment,ProfileFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
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
        Log.d(TAG, "Memory usage: ss="+memoryUsage.session_summaries+", pm="+memoryUsage.packet_memory);
        // If one of datapacket's memory or session summaries' memory usage is more than 90% , then no activity
            MainActivity.shared().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = "";
                    if(memoryUsage.packet_memory > 90 || memoryUsage.session_summaries > 90) {
                        message = getString(R.string.out_of_memory_dev1);
                    }else{
                        message = getString(R.string.worn_helmet);
                    }
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Alert");
                    alert.setMessage(message);
                    alert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            if(memoryUsage.packet_memory > 90 || memoryUsage.session_summaries > 90) {
//                                Device1_Parser.sendNotificationCmd(getContext());
//                            }else{
                                inflatePopup(getContext());
//                            }
                        }
                    });
                    alert.show();

                }
            });
    }

private class GetLatestSessionSummaryAsyncTask extends AsyncTask<Void,
            Void,SessionSummary>{

        @Override
        protected SessionSummary doInBackground(Void... voids) {
            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().getLatestSessionSummary();
        }

    }

    private class GetCollectiveSummaryDetailsAsyncTask extends AsyncTask<Void,
            Void,List<Float>>{
        @Override
        protected List<Float> doInBackground(Void... voids) {
            int totalDataSize =
                    SessionCdlDb.getInstance(mContext).getSessionDataDAO().getTotalDataInBytes() / 1024;
            Float duration_total =
                    SessionCdlDb.getInstance(mContext).getSessionDataDAO().getAllActivitiesTotalTime();
            Integer [] activity_codes =
                    SessionCdlDb.getInstance(mContext).getSessionDataDAO().getAllActivityTypes();
            List<Float> summary_list = new ArrayList<>();
            summary_list.add((float) totalDataSize);
            summary_list.add(duration_total);
            for(Integer activity : activity_codes){
                summary_list.add(Float.valueOf(activity));
            }
            return  summary_list;
        }

        @Override
        protected void onPostExecute(List<Float> floats) {

        }
    }
}

