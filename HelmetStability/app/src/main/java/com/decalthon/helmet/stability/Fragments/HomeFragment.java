package com.decalthon.helmet.stability.Fragments;

import android.Manifest;
import android.app.AlertDialog;
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
import com.decalthon.helmet.stability.BLE.Device1_Parser;
import com.decalthon.helmet.stability.DB.DatabaseHelper;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Common;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.CsvGenerator;
import com.decalthon.helmet.stability.Utilities.Helper;
import com.decalthon.helmet.stability.Utilities.UniqueKeyGen;
import com.decalthon.helmet.stability.model.InternetCheck;
import com.decalthon.helmet.stability.preferences.DevicePreferences;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.requests.ProfileReq;
import com.decalthon.helmet.stability.webservice.requests.UserInfoReq;
import com.decalthon.helmet.stability.webservice.responses.ErrorCodes;
import com.decalthon.helmet.stability.webservice.responses.ErrorMessages;
import com.decalthon.helmet.stability.webservice.services.AvatarService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import at.grabner.circleprogress.CircleProgressView;
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
import static java.lang.StrictMath.random;


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

    private LocationManager mLocationManager;
    private static int percentage = 0;
    private static boolean isDone = false;
    private String activityType = "";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        view.findViewById(R.id.latest_activity_summary);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapFragment = MapFragment.newInstance(" SESSION 7", "Latest session");
                FragmentTransaction fragmentTransaction = null;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add(R.id.fragment, mapFragment, MapFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        view.findViewById(R.id.all_activity_basic_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment sevenSessionsSummaryFragment= new SevenSessionsSummaryFragment();
                FragmentTransaction fragmentTransaction = null;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.add
                            (HomeFragment.this.getId()
                                    ,sevenSessionsSummaryFragment,SevenSessionsSummaryFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            }
        });

        view.findViewById(R.id.calendar_icon)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment calendarFragment  =
                                MonthlyCalendarFragment.newInstance
                                        (Integer.valueOf(Calendar.getInstance().get(Calendar.MONTH)).toString(),
                                                Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)).toString());
                        FragmentTransaction fragmentTransaction = null;
                        if (getFragmentManager() != null) {
                            fragmentTransaction = getFragmentManager()
                                    .beginTransaction();
                            fragmentTransaction.add
                                    (R.id.fragment,calendarFragment,"Calendar Fragment");
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
        Log.d(TAG, "onActivityCreated");
        try{
            mActionBar = ( (MainActivity) getActivity() ).getSupportActionBar();

            View actionBarView = mActionBar.getCustomView();

            CircleImageView profileShortcut =
                    actionBarView.findViewById(R.id.profile_link);

            CircleProgressView deviceStatusShortcut = (CircleProgressView)
                    actionBarView.findViewById(R.id.ble_device_connectivity);

            CircleImageView gpsSpeedShortcut =
                    actionBarView.findViewById(R.id.gps_session_start_btn);

            CircleImageView logoutMenuShorcut =
                    actionBarView.findViewById(R.id.logout_link);

            CircleImageView backLink =
                    actionBarView.findViewById(R.id.back_link);
            backLink.setVisibility(View.GONE);


            ((MainActivity)getActivity()).showProgressCircle(getContext(),0);

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
                    fragmentTransaction.replace(R.id.fragment, profileFragment,"Profile Fragment");
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
                    fragmentTransaction.replace(R.id.fragment, deviceFragment,DeviceFragment.class.getSimpleName());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            //Without any view, GPS speed can be logged for reference and recorded in the database

            gpsSpeedShortcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog helmetDialog = new AlertDialog.Builder(getContext())
                            .setTitle("Alert")
                            .setMessage("Have you worn helmet properly?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    inflatePopup(getContext());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    helmetDialog.show();
                    helmetDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor
                            (Color.parseColor("#FF1B5AAC"));
                    helmetDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor
                            (Color.parseColor("#D3D3D3"));
                }
            });

            //The last icon on the extreme right, provides a logout option

            logoutMenuShorcut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
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
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        new InternetCheck(isInternet -> {
            if (!isInternet) {
                Common.isInternetAvailable(getContext());
            }
        });
        Common.wait(50);
        navigateToFragments();
//        new DatabaseHelper.GetLastPktNum().execute();
//        new DatabaseHelper.DeleteAll().execute((long) 1);
//        if(!isDone){
//            CsvGenerator csvGenerator = new CsvGenerator(getContext());
//            csvGenerator.generateCSV(1);
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
    }

    @Override
    public void onStart() {
        super.onStart();

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
                                if(outdoorEditText.getText().toString().equals("")) {
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
                        Device1_Parser.sendStartActivityCmd(getContext(), Constants.ActivityCodeMap.get(activityType));
                    }else{
                        Log.d(TAG, "No activity code found");
                    }
                    Fragment gpsFragment = new GPSSpeedFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, gpsFragment,"GPS Fragment");
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
                fragmentTransaction.addToBackStack(Constants.LOGIN_FRAGMENT);
                fragmentTransaction.commit();
            }else{
//               upload_img();
                //saveProfile();
//                getProfile();
            }
        }catch (Exception ex){
            Log.d(TAG, ex.getMessage());
        }
    }

//    public void getProfile(){
//        ProfileService profileService = new ProfileService();
//        Request request  = profileService.getProfile(getContext());
//        if(request != null) {
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .writeTimeout(10, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build();
//            okHttpClient.newCall(request).enqueue(new Callback() {
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    try (ResponseBody responseBody = response.body()) {
//                        String json = response.body().string();
//                        if (response.isSuccessful()) {
//                            System.out.println("response=" + json);
//                            ProfileResp profileResp = new GsonBuilder().create().fromJson(json, ProfileResp.class);
//                            UserPreferences.getInstance(getContext()).saveName(profileResp.name);
//                            ProfilePreferences profilePreferences = ProfilePreferences.getInstance(getContext());
//                            profilePreferences.saveDob(profileResp.dob);
//                            profilePreferences.saveGender(String.valueOf(profileResp.gender));
//                            profilePreferences.saveHeight(profileResp.height);
//                            profilePreferences.saveWeight(profileResp.weight);
//                        }else if(json.contains("errorMessage")){
//                            ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
//                            getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
//                                }
//                            });
//                            if(errorMessages.errorCode == ErrorCodes.NO_USER_FOUND.getCode() ||
//                                    errorMessages.errorCode == ErrorCodes.NO_RECORD_FOUND.getCode()) {
//                                // Update the UI
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        // Reset UI
//                    }
//                }
//
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
//
//    }

//    public void saveProfile(){
//        ProfileReq profileReq = new ProfileReq();
//        profileReq.dob = "1999-06-06";
//        profileReq.gender = 'M';
//        profileReq.height = 172;
//        profileReq.weight = 72;
//        profileReq.name = "PK Misra";
//
//        ProfileService profileService = new ProfileService();
//        Request request  = profileService.saveProfile(getContext(), profileReq);
//        if(request != null) {
//            OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .writeTimeout(10, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                    .build();
//
//            okHttpClient.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    try (ResponseBody responseBody = response.body()) {
//                        String json = response.body().string();
//                        if (response.isSuccessful()) {
//                            System.out.println("response=" + json);
//                            ProfileResp profileResp = new GsonBuilder().create().fromJson(json, ProfileResp.class);
//                            UserPreferences.getInstance(getContext()).saveName(profileResp.name);
//                            ProfilePreferences profilePreferences = ProfilePreferences.getInstance(getContext());
//                            profilePreferences.saveDob(profileResp.dob);
//                            profilePreferences.saveGender(String.valueOf(profileResp.gender));
//                            profilePreferences.saveHeight(profileResp.height);
//                            profilePreferences.saveWeight(profileResp.weight);
//                        }else if(json.contains("errorMessage")){
//                            ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
//                            getActivity().runOnUiThread(new Runnable() {
//                                public void run() {
//                                    Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
//                                }
//                            });
//                            if(errorMessages.errorCode == ErrorCodes.NO_USER_FOUND.getCode() ||
//                                    errorMessages.errorCode == ErrorCodes.NO_RECORD_FOUND.getCode()) {
//                                // Update the UI
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        // Reset UI
//                    }
//
//                }
//
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    e.printStackTrace();
//                    // Reset UI
//                }
//            });
//        }
//    }

    public void upload_img(){

        try {
            AvatarService avatarService = new AvatarService();
            InputStream is = getActivity().getAssets().open("ajit.jpg");
            File file = new File("temp.jpg");
            Helper.copyInputStreamToFile(is, file);
            Request req = avatarService.uploadAvatarImg(getContext(), file);
            if(req != null){
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                okHttpClient.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            String json = response.body().string();
                            if (response.isSuccessful()) {
                                System.out.println("response="+json);
                            }else if(json.contains("errorMessage")){
                                ErrorMessages errorMessages = new GsonBuilder().create().fromJson(json, ErrorMessages.class);
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getContext(), errorMessages.errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                });
                                if(errorMessages.errorCode == ErrorCodes.NO_USER_FOUND.getCode() ||
                                        errorMessages.errorCode == ErrorCodes.NO_RECORD_FOUND.getCode()) {
                                }
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class UpdateSensorData extends AsyncTask<Long,Void,Void> {

        @Override
        protected Void doInBackground(Long... longs) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
            List<SensorDataEntity> sensorDataEntityList = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket((long) 1);
            long ts_ms = 1586326298640L;//1586268823340

            SensorDataEntity[] sensorDataEntities = new SensorDataEntity[sensorDataEntityList.size()];
            for (int i= 0; i< sensorDataEntityList.size(); i++){
                sensorDataEntities[i] = sensorDataEntityList.get(i);
                long ts_ms_i = ts_ms + i*10;
                sensorDataEntities[i].ax_9axis_dev2 = sensorDataEntities[i].ax_9axis_dev1;
                sensorDataEntities[i].ay_9axis_dev2 = sensorDataEntities[i].ay_9axis_dev1;
                sensorDataEntities[i].az_9axis_dev2 = sensorDataEntities[i].az_9axis_dev1;

                sensorDataEntities[i].gx_9axis_dev2 = sensorDataEntities[i].gx_9axis_dev1;
                sensorDataEntities[i].gy_9axis_dev2 = sensorDataEntities[i].gy_9axis_dev1;
                sensorDataEntities[i].gz_9axis_dev2 = sensorDataEntities[i].gz_9axis_dev1;

                sensorDataEntities[i].mx_9axis_dev2 = sensorDataEntities[i].mx_9axis_dev1;
                sensorDataEntities[i].my_9axis_dev2 = sensorDataEntities[i].my_9axis_dev1;
                sensorDataEntities[i].mz_9axis_dev2 = sensorDataEntities[i].mz_9axis_dev1;
            }

            try {
                sessionCdlDb.getSessionDataDAO().updateSessionPacket(sensorDataEntities);
            }catch (android.database.sqlite.SQLiteConstraintException e){
                if(sensorDataEntities != null && sensorDataEntities.length > 0){
                    Log.d(TAG, "packet #:"+sensorDataEntities[0].packet_number+", time="+sensorDataEntities[0].dateMillis);
                }
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class DeleteSensorData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SessionCdlDb sessionCdlDb = SessionCdlDb.getInstance(MainActivity.shared().getApplicationContext());
            List<SensorDataEntity> sensorDataEntityList = sessionCdlDb.getSessionDataDAO().getSessionEntityPacket((long) 1);
            sessionCdlDb.getSessionDataDAO().deleteSessionPackets();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onSessionStarted(String TAG,String message);
    }


}

