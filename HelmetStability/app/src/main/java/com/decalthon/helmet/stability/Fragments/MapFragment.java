package com.decalthon.helmet.stability.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.greenrobot.event.EventBus;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.database.entities.GpsSpeed;
import com.decalthon.helmet.stability.database.entities.MarkerData;
import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.MainApplication;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.generic.TimeFmt;
import com.decalthon.helmet.stability.model.MarkerNote;
import com.decalthon.helmet.stability.model.indoor_timeline.IndoorMarker;
import com.decalthon.helmet.stability.model.indoor_timeline.MarkerViewType;
import com.decalthon.helmet.stability.preferences.CsvPreference;
import com.decalthon.helmet.stability.workmanager.WorkMgrHelper;
import com.github.vipulasri.timelineview.TimelineView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.decalthon.helmet.stability.fragments.GPSSpeedFragment.sessionCdlDb;
import static com.decalthon.helmet.stability.utilities.Constants.INTER_MARKER_COUNT;

//Frequently used static constants


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


/**
 * The map fragment is used to display trackers for a session.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

//    private static final String activityType =
//            Constants.ActivityCodeMap.inverse().get(52);
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String activityType;
    private float activityDuration;

    private GoogleMap mMap;
    private ArrayList<Long> full_Schedule_list = new ArrayList<>();
//    private MapView mapView;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private LatLngBounds bounds;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private Long mParam2;

    private Marker outdoorMarkerCurrent;
    private View indoorMarkerCurrent;
    private CardView currentIndoorCard;
    private MarkerData markerDataCurrent;
    private static String noteFromId;

    private static String TAG = MapFragment.class.getSimpleName();

    private static int currentMessageId;
    private EventBus mBus = EventBus.getDefault();

    //Session specific variables
    private static MarkerData markerData;

    private OnFragmentInteractionListener mListener;
    /**ArrayList of LatLng Objects, added from project assets*/
    private List<MarkerData> markerDatas;
//    ONLY FOR TESTING PURPOSE. DO NOT USE THIS;

    public static List<GpsSpeed> gpsSpeeds;
//    private List<GpsSpeed> gpsSpeeds;
    private  ArrayList<LatLng> markerPosList = new ArrayList<>();

    private  ArrayList<LatLng> gpsPosList = new ArrayList<>();
    private long session_id = 4;
    private SessionSummary curSessionSummary;

    /**Fragment specific local variables*/
    private Context mContext;
    private Dialog dialog;
    private PopupWindow graphPromptDialog;
    private int exactPos;
    private TextView dateTv,timeTv,sessionNameTv,durationTv,sessionDateSizeTv,
            sessionDataTypesTv, sessionActivityTypeTv, textNoteTv;
    private final String timeFormat = "HH:mm:ss a dd-MM-yyyy";



    /**Zoom variables for scale implementation*/
/*    private double zoomLevelArray[] =  new double[20];
    private double zoomLevelCurrent;*/

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, long param2, float param3){
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2, param2);
        args.putFloat(ARG_PARAM3,param3);
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
            activityType = getArguments().getString(ARG_PARAM1); // Cyclic_Outdoor
//            activityType = "indoor";
            session_id = getArguments().getLong(ARG_PARAM2);
            activityDuration = getArguments().getFloat(ARG_PARAM3);
//            if(activityType.toLowerCase().contains(getString(R.string.activity_outdoor).toLowerCase()))  {
//                session_id = 4;
//            }else{
//                session_id = 8;
//            }
        }
    }


    /**
     * The mapFragment is associated with a map object using the the getMapAsync method
     * @param inflater The callback argument which inflates a {@link MapFragment}
     * @param container The parent container which holds the map fragment
     * @param savedInstanceState Any restore-able data
     * @return A view with a map object
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the map Fragment
//        TODO just for now
        View view;

        if(activityType.toLowerCase().contains(getString(R.string.activity_outdoor).toLowerCase()))  {
            view =  inflater.inflate(R.layout.fragment_outdoor_map, container, false);
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }else{
            view =  inflater.inflate(R.layout.fragment_indoor_map, container, false);
            /*TimeLineViewAdapter timeLineViewAdapter;
            ArrayList<IndoorTimestamp> datalist = new ArrayList<>();
            datalist.add(new IndoorTimestamp(
                    (new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date())),
                    "HELLO"));
            datalist.add(new IndoorTimestamp((new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date())),
                    ""));
            datalist.add(new IndoorTimestamp((new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date())),
                    "BYE"));
            datalist.add(new IndoorTimestamp((new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date())),
                    "HELLO"));
            datalist.add(new IndoorTimestamp((new SimpleDateFormat(timeFormat, Locale.getDefault()).format(new Date())),
                    "TEST"));

            RecyclerView indoorRecyclerView = view.findViewById(R.id.indoor_recycler_view);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                    (getContext(),RecyclerView.VERTICAL,false);
            timeLineViewAdapter = new TimeLineViewAdapter(datalist);
            indoorRecyclerView.setLayoutManager(linearLayoutManager);
            indoorRecyclerView.setAdapter(timeLineViewAdapter);
            indoorRecyclerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomViewFragment customViewFragment = CustomViewFragment.newInstance
                            (session_id,1l, 1l);
                    FragmentTransaction ftxn = getFragmentManager()
                            .beginTransaction();
                    ftxn.replace(MapFragment.this.getId(),
                            customViewFragment,
                            CustomViewFragment.class.getSimpleName());
                    ftxn.addToBackStack(MapFragment.class.getSimpleName());
                    ftxn.commit();
                }
            });*/
        }

        view.setClickable(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.back_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.shared().onBackPressed();
            }
        });
         dateTv = view.findViewById(R.id.date_value_tv);
         timeTv = view.findViewById(R.id.time_value_tv);
         sessionNameTv = view.findViewById(R.id.session_name_value_tv);
         durationTv = view.findViewById(R.id.session_duration_value_tv);
         sessionDateSizeTv = view.findViewById(R.id.session_data_size_value_tv);
         sessionDataTypesTv =
                 view.findViewById(R.id.session_data_types_value_tv);
         sessionActivityTypeTv =  view.findViewById(R.id.activity_type_value);
        sessionActivityTypeTv.setText(activityType);
        textNoteTv = view.findViewById(R.id.text_note_tv);

        new GetSessionDisplayHeaderAsyncTask().execute();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * The action bar UI is changed for the user needs in the Map and Graph fragments
     * @param savedInstanceState Any restore-able data
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Update
        registerAndCheck(this);
        sessionCdlDb = SessionCdlDb.getInstance();

        try{
            new LoadMarkerGpsData().execute().get();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            acceptList();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if(activityType.toLowerCase().contains(getString(R.string.activity_indoor).toLowerCase())){
//            TimeLineViewAdapter timeLineViewAdapter;
//            RecyclerView indoorRecyclerView =
//                    MapFragment.this.getView().findViewById(R.id
//                    .indoor_recycler_view);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager
//                    (getContext(), RecyclerView.VERTICAL, false);
//            int timestamp_count = 0;
//            Long [] marker_relative_timestamps = new Long[markerDatas.size()];
//            Integer [] marker_nums = new Integer[markerDatas.size()];
//            long first_timestamp = markerDatas.get(0).getMarker_timestamp();
//            for(MarkerData markerData : markerDatas){
//                marker_relative_timestamps[timestamp_count] =
//                        ( markerData.getMarker_timestamp() - first_timestamp ) / 1000;
//                marker_nums[timestamp_count++] = markerData.getMarkerNumber();
//            }
//
//            long last_timestamp =
//                    markerDatas.get(markerDatas.size() - 1).getMarker_timestamp();
//            long range_of_schedule = ( last_timestamp - first_timestamp )/1000;
//            int regular_timestamps_count =
//                    (int) (range_of_schedule/Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL) ;
//            Long [] absolute_timestamps =
//                    new Long[regular_timestamps_count];
//
//            int regular_counter = 0;
//            int interval = Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL;
//            for(int reg_i = interval; reg_i <= range_of_schedule ; reg_i += interval ){
//                absolute_timestamps[regular_counter++] = (long)reg_i;
//            }
//
//            //Merge the marker and regular timestamp arrays here
//
//            List<Long> marker_timestamp_list =
//                    Arrays.asList(marker_relative_timestamps);
//            List<Long> absolute_timestamps_list =
//                    Arrays.asList(absolute_timestamps);
//            ArrayList<Long> full_Schedule_list = new ArrayList<>();
//            full_Schedule_list.addAll(marker_timestamp_list);
//            full_Schedule_list.addAll(absolute_timestamps_list);
//            Collections.sort(full_Schedule_list);
//
//            timeLineViewAdapter = new TimeLineViewAdapter(full_Schedule_list,
//                    first_timestamp, marker_nums);
//            indoorRecyclerView.setLayoutManager(linearLayoutManager);
//            indoorRecyclerView.setAdapter(timeLineViewAdapter);

//            indoorRecyclerView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    CustomViewFragment customViewFragment = CustomViewFragment.newInstance
//                            (FRAGMENT_NAME_DEVICE1_9_AXIS, "plot");
//                    FragmentTransaction ftxn = getFragmentManager()
//                            .beginTransaction();
//                    ftxn.replace(MapFragment.this.getId(),
//                            customViewFragment,
//                            CustomViewFragment.class.getSimpleName());
//                    ftxn.addToBackStack(MapFragment.class.getSimpleName());
//                    ftxn.commit();
//                }
//            });
//        }
//       Common.wait(2000);
    }

    private class LoadMarkerGpsData extends  AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            if(mMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
                mMap.clear();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            markerPosList.clear();
            gpsPosList.clear();
            markerDatas = sessionCdlDb.getMarkerDataDAO().getMarkerData(session_id);
            if(markerDatas == null || markerDatas.size() == 0){
                return null;
            }
            for(MarkerData markerData: markerDatas){
                markerPosList.add(new LatLng(markerData.lat, markerData.lng));
            }
            if(activityType.toLowerCase().contains(getString(R.string.activity_outdoor).toLowerCase())) {
                gpsSpeeds = sessionCdlDb.gpsSpeedDAO().getGpsSpeed(markerDatas.get(0).marker_timestamp-1, markerDatas.get(markerDatas.size()-1).marker_timestamp+1);

                for(GpsSpeed gpsSpeed: gpsSpeeds){
                    gpsPosList.add(new LatLng(gpsSpeed.latitude, gpsSpeed.longitude));
                }
            }else{
                //todo: coding for indoor activities
            }

            return null;
        }
    }

    /**
     * This callback is fired when the map object is available as a GoogleMap Object.
     * @param googleMap A googleMap object that is associated with the MapFragment
     */
    @Override
    public void onMapReady(GoogleMap googleMap)  {
        mMap = googleMap;

        /**Basic, passive setting for the map*/

        mMap.setMaxZoomPreference(Constants.ZOOM_BUILDING);
        mMap.setMinZoomPreference(Constants.ZOOM_EARTH);

        /**Optional, based on zoom level */
        mMap.getUiSettings().setZoomControlsEnabled(true);

        /**Optional, on change of camera tilt/bearing**/
        mMap.getUiSettings().setCompassEnabled(true);

        try{
            acceptList();
            registerClickLatLng();
            registerMarkerDialog();
//            zoomInit();
        }
        catch (IOException E) {
            E.printStackTrace();
        }
    }

    /**
     * The list of LatLng marker positions are accepted in {@link MapFragment#acceptList()}
     * @throws IOException Possible when reading data from the assets folder
     */
    private void acceptList() throws IOException {
        String temp;

        /*Three datasets used. Third(and largest asset) uncommented*/
//        InputStream iStream = getActivity().getAssets().open("ride1.txt");
//        InputStream iStream = getActivity().getAssets().open("ride2.txt");

        /*Java-based parsing of double values from strings in the file*/

//        java.io.InputStream iStream = getActivity().getAssets().open("ride3.txt");
//        BufferedReader bufRead = new BufferedReader(new java.io.InputStreamReader(iStream));
//        double latitude, longitude;
//        while ((temp = bufRead.readLine()) != null) {
//            String[] coords = temp.split(",");
//            latitude = Double.parseDouble(coords[0]);
//            longitude = Double.parseDouble(coords[1]);
//            coordList.add(new LatLng(latitude, longitude));
//        }


        /**
         * The callback plots evenly spaced markers in the activity area ( bounding box )
         */

        if(activityType.toLowerCase().contains((getString(R.string.outdoor).toLowerCase())) && (mMap != null)){
            mMap.setOnMapLoadedCallback(
                    new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            /**On every map load, hidden markers
                             * (Except first and last) are plotted
                             * on the map
                             * */
                            displayPositionMarkersOnMap();
                        }
                    }
            );
        }

        else  if(activityType.toLowerCase().contains((getString(R.string.indoor).toLowerCase()))){
           displayIndoorMarkersOnTimeline();
        }
    }

    private void displayIndoorMarkersOnTimeline() {

        TimeLineViewAdapter timeLineViewAdapter;
        RecyclerView indoorRecyclerView =
                MapFragment.this.getView().findViewById(R.id
                        .indoor_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager
                (getContext(), RecyclerView.VERTICAL, false);
        int timestamp_count = 0;
        Long [] marker_relative_timestamps = new Long[markerDatas.size()];
        Integer [] marker_nums = new Integer[markerDatas.size()];
        long first_timestamp = markerDatas.get(0).getMarker_timestamp();
        String[] note_markers = new String[markerDatas.size()];
        //for(MarkerData markerData : markerDatas){
        for (int i = 0; i < markerDatas.size(); i++) {
            MarkerData markerData = markerDatas.get(i);

            marker_relative_timestamps[timestamp_count] =
                    ( markerData.getMarker_timestamp() - first_timestamp ) / 1000;
            if(markerData.note.trim().length() > 0){
                note_markers[timestamp_count] = markerData.note;
            }else{
                note_markers[timestamp_count] = "";
            }
            marker_nums[timestamp_count++] = i;
        }

        long last_timestamp;
        if(full_Schedule_list.size() > 0){
            last_timestamp = first_timestamp +
                    1000 * full_Schedule_list.get(full_Schedule_list.size() - 1);
        }else{
            last_timestamp =
                    markerDatas.get(markerDatas.size() - 1).getMarker_timestamp();
        }
        long range_of_schedule = ( last_timestamp - first_timestamp )/1000;
        int regular_timestamps_count =
                (int) (range_of_schedule/Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL) ;
        Long [] absolute_timestamps =
                new Long[regular_timestamps_count];

        int regular_counter = 0;
        int interval = Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL;
        for(int reg_i = interval; reg_i <= range_of_schedule ; reg_i += interval ){
            absolute_timestamps[regular_counter++] = (long)reg_i;
        }

        //Merge the marker and regular timestamp arrays here
        List<Long> marker_timestamp_list =
                Arrays.asList(marker_relative_timestamps);
        List<Long> absolute_timestamps_list =
                Arrays.asList(absolute_timestamps);

        full_Schedule_list.addAll(marker_timestamp_list);
        full_Schedule_list.addAll(absolute_timestamps_list);
        Collections.sort(full_Schedule_list);
        ///////// Ajit:start
        long duration = ( last_timestamp - first_timestamp ); // millisecond
        int total_regular_marker =
                (int) (duration/((float)Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL*1000)) ;
        Long [] reg_relative_timestamps =
                new Long[total_regular_marker];

        int counter = 0;
        int delta = (Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL)*1000;
        for(int reg_i = delta; reg_i <= duration ; reg_i += delta ){
            reg_relative_timestamps[counter++] = (long)reg_i;
        }

        int size = markerDatas.size() + reg_relative_timestamps.length;
        IndoorMarker[] indoorMarkers = new IndoorMarker[size];
        indoorMarkers[0] = new IndoorMarker(MarkerViewType.START, first_timestamp);
        int indoor_marker_count = 1;
        int marker_counter = 1;
        for(int i=0; i < reg_relative_timestamps.length; i++){
            long cur_ts = reg_relative_timestamps[i] + first_timestamp;
            while(cur_ts >= markerDatas.get(marker_counter).marker_timestamp){
                MarkerData markerData = markerDatas.get(marker_counter);
                indoorMarkers[indoor_marker_count++] = new IndoorMarker(MarkerViewType.USER, markerData.marker_timestamp, markerData);
                System.out.println("markerdata, indoor_marker_count="+indoor_marker_count+", marker_counter="+marker_counter+", marker_id="+markerData.markerNumber);
                marker_counter++;
            }
            indoorMarkers[indoor_marker_count++] = new IndoorMarker(MarkerViewType.REGULAR, cur_ts);
            System.out.println("regular, indoor_marker_count="+indoor_marker_count+", marker_counter="+marker_counter);

        }
        while(marker_counter < markerDatas.size()-1){
            MarkerData markerData = markerDatas.get(marker_counter);
            indoorMarkers[indoor_marker_count++] = new IndoorMarker(MarkerViewType.USER, markerData.marker_timestamp, markerData);
            System.out.println("markerdata, indoor_marker_count="+indoor_marker_count+", marker_counter="+marker_counter+", marker_id="+markerData.markerNumber);
            marker_counter++;
        }
        indoorMarkers[indoor_marker_count] = new IndoorMarker(MarkerViewType.STOP, markerDatas.get(markerDatas.size()-1).marker_timestamp, markerDatas.get(markerDatas.size()-1));
        timeLineViewAdapter = new TimeLineViewAdapter(indoorMarkers);
        ///////// Ajit:End


//        timeLineViewAdapter = new TimeLineViewAdapter(full_Schedule_list,
//                first_timestamp, marker_nums, note_markers);

        indoorRecyclerView.setLayoutManager(linearLayoutManager);
        indoorRecyclerView.setAdapter(timeLineViewAdapter);
    }

    /**
     * Display markers on map based on positions read from the assets file (ride3.txt)
     */
    private void  displayPositionMarkersOnMap(){

        /**
         * Latitude and Longitude bounds are built from the entire
         * dataset maintaining bounds within left-bottom and
         * right-upper bounds
         * */
        if(markerPosList.size() == 0){
           markerPosList.add(new LatLng(48.8587741,2.2069771)); // Paris's location

            Toast.makeText(getContext(), "No Data is available", Toast.LENGTH_LONG);
        }
        //LatLng savedFirst = markerPosList.get(0);
//
//        for (LatLng markable : markerPosList) {
//            builder.include(markable);
//        }

        for (LatLng markable : gpsPosList) {
            builder.include(markable);
        }
        MarkerOptions markerOptions =  new MarkerOptions();
        for(int marker_i = 1; marker_i < markerPosList.size()-1; marker_i=marker_i+INTER_MARKER_COUNT){
            MarkerData markerData = markerDatas.get(marker_i);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
            if(markerData.note.trim().length() > 0){
                bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            }
            mMap.addMarker( new MarkerOptions()
                    .position(markerPosList.get(marker_i))
                    .icon(bitmapDescriptor)
            ).setTag(markerData);
        }



        /**A polyline wraps the marker bound area, and creates
         * an end to end geographical bound in the covered region
         * */

        mMap.addPolyline(new PolylineOptions()
                .addAll(gpsPosList)
                .width(6)
                .color(Color.BLUE));
        LatLng savedLast = markerPosList.get(markerPosList.size() - 1);

        /**First and last markers are made visible
         * using respective MarkerOptions objects
         * */

//        mMap.addMarker(new MarkerOptions()
//                .position(savedFirst)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                .draggable(false)
//                .visible(true));

        mMap.addMarker(new MarkerOptions()
                .position(savedLast)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(false)
                .visible(true)).setTag(markerDatas.get(markerDatas.size()-1));

        /**The statements below build the bounding box
         * rom the LatLng position assets  and moves camera
         * to the bounding box
         * */

        bounds = builder.build();
        int padding = 0;

        /*This factory method updates the new bounds of the Google Map based
        * on the included markers*/

        CameraUpdate updater = CameraUpdateFactory.newLatLngBounds(bounds,padding);

        mMap.moveCamera(updater);

    }

//    public static void addRideMarkerOnMap(Location location){
//        markerPosList.add(new LatLng(location.getLatitude() , location.getLongitude()));
//        markerData = new MarkerData();
//        markerData.marker_timestamp = System.currentTimeMillis();
////        addMarkerDataEntry(markerData);
//    }

    /**
     * Based on the proximity to the polyline, as defined by {@link Constants#POLYLINE_DISTANCE}
     * a dialog menu displays options for graph display
     */
    private void registerClickLatLng() {
        mMap.setOnMapClickListener(
                new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        exactPos = -1;
                        double difference = -1;
                        double clickLat = latLng.latitude;
                        double clicklong = latLng.longitude;
                        //exactPos = new LatLng(clickLat,clicklong);
                        difference = calcDeviation(latLng);
                        if(difference < Constants.POLYLINE_DISTANCE) {
//                            promptMenu();
                        Log.d(TAG, "onMapClick: " + difference);
                        if(exactPos >= 0){
                            CustomGraphFragment.clear();
                            CustomViewFragment customViewFragment = CustomViewFragment.newInstance
                                    (session_id, gpsSpeeds.get(exactPos).timestamp, markerDatas.get(0).marker_timestamp);
                            FragmentTransaction ftxn = getFragmentManager()
                                    .beginTransaction();
                            ftxn.replace(MapFragment.this.getId(),
                                    customViewFragment,
                                    CustomViewFragment.class.getSimpleName());
                            ftxn.addToBackStack(CustomViewFragment.class.getSimpleName());
                            ftxn.commit();
                        }

                        }
                    }
                }
        );

    }

    /**
     * Based on LatLng position, the distance from nearest polyline is obtained
     * @param exactPos The position of reference.
     * @return The deviation from the position in metre.
     */
    private double calcDeviation(LatLng exactPos) {
        long millis1 = 0 ,millis2 = 0;
        double distanceFromPolyline = 0.0;

        LatLng testPoint;

        if (exactPos == null) {
            testPoint = gpsPosList.get(0);
        } else {
            testPoint = exactPos;
        }


        millis1 = System.currentTimeMillis();
        System.out.println("mil11-->" + millis1);
        LatLng nearestPoint = findNearestPoint(testPoint, gpsPosList);
        millis2 = System.currentTimeMillis();
        System.out.println("List size" + gpsPosList.size());
        System.out.println("mill2-->" + millis2);
        System.out.println("Time taken-->" + (millis2 - millis1));
        Log.e("NEAREST POINT: ", "" + nearestPoint);
        distanceFromPolyline = SphericalUtil.computeDistanceBetween(testPoint, nearestPoint);
        Log.e("DISTANCE: ", "" + distanceFromPolyline);
        return distanceFromPolyline;
    }

    /**
     * Small note system for displayed markers
     */
    private void registerMarkerDialog() {
        //Called after displaying position markers on map

        /**Change marker color to green*/
//        final BitmapDescriptor bitmapDescriptorOrange
//                = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
//
//        final BitmapDescriptor bitmapDescriptorYellow
//                = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        dialog = new Dialog(mContext);

        /**Approximation of 100m distance from polyline
         * (1)If the map click corresponds to a region
         * within 100m from the nearest polyline, then
         * a dialog box pops up
         * (2)The user can then check plot graphs of
         * accelerometer, gyroscope and magnetometer readings
         * */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                outdoorMarkerCurrent = marker;
                markerDataCurrent = (MarkerData) marker.getTag();
                if(markerDataCurrent != null) {
                    showDialog(markerDataCurrent.getNote(), markerDataCurrent.marker_timestamp, markerDataCurrent.note_timestamp);
                }else{
                    markerDataCurrent = new MarkerData();
                    showDialog("", new Date().getTime(), 0);
                }
//                //TODO Shift this to a common function
//                TextView dtTemp ;
//                @SuppressLint("SimpleDateFormat") SimpleDateFormat parseformat =
//                        new SimpleDateFormat
//                                ("dd MMM yyyy " + "HH:mm");
//                dtTemp = dialog.findViewById(R.id.date_time_view);

                //TODO Shift this to a common function.
//                final EditText editText = dialog.findViewById(R.id.EditTextView);


                //TODO Do not shift this
                /*
                * Marker tags are used to save and restore marker-specific saved information
                * */
                if(markerDataCurrent != null)
                {
//                    editText.setText(markerDataCurrent.toString());

//                    clickMessageId = markerData.messageId;
                }

                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                mParam1 = data.getStringExtra("Note");
                if(mParam1 != null) {
                    Log.v(TAG, "Data passed from Child fragment = " + mParam1);;
                }
            }
        }
    }

    private void showDialog(String note, long marker_timestamp,
                            long note_timestamp) {
        try{
            MarkerDialogFragment markerDialogFragment = null;
            if(activityType.toLowerCase().contains(getString(R.string.outdoor).toLowerCase())){
                markerDialogFragment = MarkerDialogFragment.newInstance(note,"1", marker_timestamp, note_timestamp);
            }else if(activityType.toLowerCase().contains(getString(R.string.indoor).toLowerCase())){
                markerDialogFragment =
                        MarkerDialogFragment.newInstance(note,"3", marker_timestamp, note_timestamp);
            }
            markerDialogFragment.show(getFragmentManager(), MarkerDialogFragment.class.getSimpleName());
        }catch (Exception ex){
            Log.d(TAG, "Error="+ex.getMessage());
        }
    }

    private void showDialogOld(String note, long marker_timestamp,
                            long note_timestamp) {
        Fragment markerDialogFragment = new Fragment();
        if(activityType.toLowerCase().contains(getString(R.string.outdoor).toLowerCase())){
            markerDialogFragment = MarkerDialogFragment.newInstance(note,"1", marker_timestamp, note_timestamp);
        }
        if(activityType.toLowerCase().contains(getString(R.string.indoor).toLowerCase())){
            markerDialogFragment =
                    MarkerDialogFragment.newInstance(note,"3", marker_timestamp, note_timestamp);
        }
//        markerDialogFragment.setTargetFragment(this,0);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.add(this.getId(),markerDialogFragment, MarkerDialogFragment.class.getSimpleName())
                .addToBackStack(MarkerDialogFragment.class.getSimpleName()).commit();
    }

    private static void addMarkerDataEntry(MarkerData markerData) {
        new MapFragment.InsertMarkerDataAsyncTask().execute(markerData);
    }

    private static void updateMarkerDataEntrgraphy(MarkerData markerData){
        new MapFragment.UpdateNoteAsyncTask().execute(markerData);
    }

    private void getMarkerNote(int markerSelected){
        new MapFragment.RetrieveNoteAsyncTask().execute(markerSelected);
    }
//    private void removeMarkerData(MarkerData tag){
//        new MapFragment.RemoveNoteAsyncTask().execute(tag);
//    }

    /**
     * Utility function
     * @param p Test location
     * @param start Start of polyline reference line
     * @param end End of polyline reference line
     * @return Distance in metres between test location and the nearest polyline
     */
    private LatLng findNearestPoint(final LatLng p, final LatLng start, final LatLng end) {
        if (start.equals(end)) {
            return start;
        }

        /***/

        final double s0lat = Math.toRadians(p.latitude);
        final double s0lng = Math.toRadians(p.longitude);
        final double s1lat = Math.toRadians(start.latitude);
        final double s1lng = Math.toRadians(start.longitude);
        final double s2lat = Math.toRadians(end.latitude);
        final double s2lng = Math.toRadians(end.longitude);

        double s2s1lat = s2lat - s1lat;
        double s2s1lng = s2lng - s1lng;
        final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng)
                / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);
        if (u <= 0) {
            return start;
        }
        if (u >= 1) {
            return end;
        }

        return new LatLng(start.latitude + (u * (end.latitude - start.latitude)),
                start.longitude + (u * (end.longitude - start.longitude)));

    }

    /**Utilitt used to calculate nearest polyline distance*/

    private LatLng findNearestPoint(LatLng test, List<LatLng> target) {
        /**Initially no distance is covered or checked*/

        double distance = -1;

        /**The point under consideration is passed before as an argument*/
        LatLng minimumDistancePoint = test;

        /**Null values and potential exceptions checked*/
        if (test == null || target == null) {
            return minimumDistancePoint;
        }


        /**Every point in the polygon is considered for nearest line in geofence*/
        for (int i = 0; i < target.size(); i++) {
            LatLng point = target.get(i);

            /**Points are indexed from 1 to n*/
            int segmentPoint = i + 1;

            /**When a full check is complete*/
            if (segmentPoint >= target.size()) {
                segmentPoint = 0;
            }

            double currentDistance = PolyUtil.distanceToLine(test, point, target.get(segmentPoint));

            /**Minimum computing function*/
            if (distance == -1 || currentDistance < distance) {
                distance = currentDistance;
                exactPos = segmentPoint;
                //Use of overloaded note
                minimumDistancePoint = findNearestPoint(test, point, target.get(segmentPoint));
            }
        }

        return minimumDistancePoint;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        gpsPosList.clear();
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
    }



    private static class InsertMarkerDataAsyncTask extends AsyncTask<MarkerData,Void,Void> {
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
         * @param markerDatagrams The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(MarkerData... markerDatagrams) {
            sessionCdlDb.getMarkerDataDAO().insertMarkerData(markerData);
            Log.d(TAG,"Marker Data has been inserted");
            return null;
        }
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

    }

    private static class RetrieveNoteAsyncTask extends AsyncTask<Integer,Void,Void>{

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
         * @param integers The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Integer... integers) {
            noteFromId  = sessionCdlDb.getMarkerDataDAO().getMarkerNote(integers);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<MarkerData,Void,Void>{

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
         * @param markerData The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(MarkerData... markerData) {
            sessionCdlDb.getMarkerDataDAO().updateMarkerData(markerData);
            return null;
        }
    }


    private static class UpdateSessionSummary extends  AsyncTask<SessionSummary,Void,Void>{
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
         * @param sessionSummaries The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(SessionSummary... sessionSummaries) {
            sessionCdlDb.getSessionDataDAO().updateSessionSummary(sessionSummaries[0]);
            return null;
        }
    }

    public class TimeLineViewAdapter extends RecyclerView.Adapter{
//        private final long firstTimestamp ;
//        private final Long [] all_marker_timestamps ;
//        private final int last_index ;
        long progress;
        Date dateStart;
        int marker_data_index = 0;
        Integer [] markerNums;
        String [] note_markers;
        private int note_marker_counter = -1;
        private Map<Integer, Integer> note_pos_map = new HashMap<>();
        private IndoorMarker[] indoorMarkers;


        public TimeLineViewAdapter(IndoorMarker[] indoorMarkers) {
            this.indoorMarkers = indoorMarkers;
        }

//        public TimeLineViewAdapter(ArrayList<Long> timestamps,
//                                   long first_timestamp,
//                                   Integer[] markerNums,
//                                   String[] note_markers) {
//            this.all_marker_timestamps = timestamps.toArray(new Long[0]);
//            this.last_index = all_marker_timestamps.length - 1;
//            this.dateStart =  new Date(first_timestamp);
//            this.firstTimestamp = first_timestamp;
//            this.progress = first_timestamp;
//            this.markerNums = markerNums;
//            this.note_markers = note_markers;
//
//
//        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_timeline, null);
            return new TimeLineViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            try{
                IndoorMarker indoorMarker = indoorMarkers[position];
                switch (indoorMarker.markerViewType){
                    case START:
                        ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(Color.TRANSPARENT);
                        ((TimeLineViewHolder) holder).mTimelineView.getMarker().setAlpha(0);
                        break;
                    case STOP:
                        ((TimeLineViewHolder) holder).mTimelineView.setMarkerColor(Color.RED);
                        break;
                    case REGULAR:
                        ((TimeLineViewHolder) holder).mTimelineView.setMarkerColor(getResources().getColor(R.color.regular_marker_color));
                        ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.regular_marker_color));
                        ((TimeLineViewHolder) holder).holderCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //Add Graph Navigation here
                                CustomGraphFragment.clear();
                                CustomViewFragment customViewFragment = CustomViewFragment.newInstance
                                        (session_id, indoorMarker.timestamp, markerDatas.get(0).marker_timestamp);

                                FragmentTransaction ftxn = getFragmentManager()
                                        .beginTransaction();
                                ftxn.replace(MapFragment.this.getId(),
                                        customViewFragment,
                                        CustomViewFragment.class.getSimpleName());
                                ftxn.addToBackStack(CustomViewFragment.class.getSimpleName());
                                ftxn.commit();
                            }
                        });
                        break;
                    case USER:
                        if(indoorMarker.markerData != null){
                            MarkerData markerData = indoorMarker.markerData;
                            TimelineView timelineview =
                                    ((TimeLineViewHolder) holder).mTimelineView;
                            if(timelineview.getTag() instanceof MarkerData){
                                markerData = (MarkerData)timelineview.getTag();
                            }else{
//                                timelineview.setTag(markerData);
                                ((TimeLineViewHolder) holder).holderCard.setTag(markerData);
                            }
                            if(!(markerData.note.isEmpty())){
                                timelineview.setMarkerColor(getResources().getColor(R
                                        .color.non_empty_note_marker_color));
                                ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.non_empty_note_marker_color));
                            }else{
                                timelineview.setMarkerColor(getResources().getColor(R.color.empty_note_marker_color));
                                ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.empty_note_marker_color));
                            }

                            ((TimeLineViewHolder) holder).holderCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    currentIndoorCard = (CardView) view;
                                    indoorMarkerCurrent =
                                            ((TimeLineViewHolder) holder).mTimelineView;
                                    markerDataCurrent = (MarkerData) currentIndoorCard.getTag();
                                    if(markerDataCurrent != null ){
//                                    markerDataCurrent.marker_timestamp =
//                                            new Date(progress + 1000*all_marker_timestamps[cur_note_marker]).getTime();

                                    }else{
                                        markerDataCurrent = new MarkerData();
                                        markerDataCurrent.session_id =
                                                MapFragment.this.session_id;
                                        markerDataCurrent.marker_timestamp =
                                                new Date().getTime();
                                    }
                                    showDialog( markerDataCurrent.getNote(),
                                            markerDataCurrent.marker_timestamp,
                                            markerDataCurrent.note_timestamp);
                                }
                            });
                        }
                        break;

                }
                TextView indoorDateView  =
                        ((TimeLineViewHolder) holder).holderCard.findViewById(R.id.indoor_text_timeline_date);
                indoorDateView.setText(new SimpleDateFormat("HH:mm:ss",
                        Locale.getDefault()).format(new Date(indoorMarker.timestamp)));
            }catch (Exception ex){
                Log.d(TAG, "Position="+position+", exception="+ex.getMessage());
            }

        }

//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//            System.out.println("position="+position);
//            if(position == 0 || position == last_index){
//                ((TimeLineViewHolder) holder).mTimelineView.setMarkerColor(Color.RED);
//            }else if( (all_marker_timestamps[position] % Constants.INDOOR_REGULAR_TIMESTAMPS_INTERVAL) == 0 ){
//                // TODO Check use-case for  marker timestamp itself % NDOOR_REGULAR_TIMESTAMPS_INTERVAL
//                ((TimeLineViewHolder) holder).mTimelineView.setMarkerColor(getResources().getColor(R.color.offgray));
//                ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.offgray));
//                ((TimeLineViewHolder) holder).mTimelineView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //Add Graph Navigation here
////                        CustomViewFragment customViewFragment = CustomViewFragment.newInstance
////                                (FRAGMENT_NAME_DEVICE1_9_AXIS,"plot");
////                        CustomViewFragment customViewFragment = CustomViewFragment.newInstance
////                                (session_id,1l, 1l);
//                        CustomViewFragment customViewFragment = CustomViewFragment.newInstance
//                                (session_id, progress + 1000*all_marker_timestamps[position], markerDatas.get(0).marker_timestamp);
//
//                        FragmentTransaction ftxn = getFragmentManager()
//                                .beginTransaction();
//                        ftxn.replace(MapFragment.this.getId(),
//                                customViewFragment,
//                                CustomViewFragment.class.getSimpleName());
//                        ftxn.addToBackStack(MapFragment.class.getSimpleName());
//                        ftxn.commit();
//                    }
//                });
//
//            }else{
//                int cur_note_marker_temp = 0;
//                if(note_pos_map.containsKey(position)){
//                    cur_note_marker_temp = note_pos_map.get(position);
//                }else if(note_marker_counter < markerNums.length - 1) {
//                    note_marker_counter++;
//                    cur_note_marker_temp = note_marker_counter;
//                    note_pos_map.put(position, note_marker_counter);
//                }
//
//                final int cur_note_marker = cur_note_marker_temp;
//                MarkerData markerData = markerDatas.get(markerNums[cur_note_marker]);
//                TimelineView timelineview =
//                        ((TimeLineViewHolder) holder).mTimelineView;
//                if(timelineview.getTag() instanceof MarkerData){
//                    markerData = (MarkerData)timelineview.getTag();
//                }else{
//                    timelineview.setTag(markerData);
//                }
//                if(!(markerData.note.isEmpty())){
//                    timelineview.setMarkerColor(getResources().getColor(R
//                            .color.orange));
//                    ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.orange));
//                }else{
//                    timelineview.setMarkerColor(getResources().getColor(R.color.yellow));
//                    ((TimeLineViewHolder) holder).holderCard.setCardBackgroundColor(getResources().getColor(R.color.yellow));
//                }
////                markerDataCurrent = new MarkerData();
////                markerDataCurrent.session_id = session_id;
////                markerDataCurrent.note = note_markers[cur_note_marker];
//
//                timelineview.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        indoorMarkerCurrent = view;
//                        currentIndoorCard =
//                                ((TimeLineViewHolder) holder).holderCard;
//                        markerDataCurrent = (MarkerData) view.getTag();
//                        if(markerDataCurrent != null ){
//                            markerDataCurrent.marker_timestamp =
//                                    new Date(progress + 1000*all_marker_timestamps[cur_note_marker]).getTime();
//                            showDialog( markerDataCurrent.getNote(),
//                                    markerDataCurrent.marker_timestamp,
//                                    markerDataCurrent.note_timestamp);
//                        }else{
//                            markerDataCurrent = new MarkerData();
//                            markerDataCurrent.setMarkerNumber(markerNums[cur_note_marker]);
//                            markerDataCurrent.session_id =
//                                    MapFragment.this.session_id;
//                            markerDataCurrent.marker_timestamp =
//                                    new Date(progress + 1000*all_marker_timestamps[position ]).getTime();
//                            showDialog(note_markers[cur_note_marker],
//                                    new Date().getTime(),
//                                    0);
//                        }
//                    }
//                });
//            }
//
//            TextView indoorDateView  =
//                    ((TimeLineViewHolder) holder).holderCard.findViewById(R.id.indoor_text_timeline_date);
//            indoorDateView.setText(new SimpleDateFormat("HH:mm:ss",
//                    Locale.getDefault()).format(new Date(progress + 1000*all_marker_timestamps[position])));
//        }
        @Override
        public int getItemCount() {
            return  indoorMarkers.length;
        }

        @Override
        public int getItemViewType(int position) {
                return TimelineView.getTimeLineViewType(position, getItemCount());
        }

    }

    public static class TimeLineViewHolder extends RecyclerView.ViewHolder {
        TimelineView mTimelineView;
        TextView mdateTimeTv;
        CardView holderCard;

        TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            mTimelineView =  itemView.findViewById(R.id.timeline);
            mdateTimeTv = itemView.findViewById(R.id.indoor_text_timeline_date);
            holderCard = itemView.findViewById(R.id.info_card_view);
            mTimelineView.initLine(viewType);
        }
    }

    private static class GraphListAdapter extends BaseAdapter {

        public GraphListAdapter() {

        }

        @Override
        public int getCount() {
            return Constants.TAB_ID_MAPS.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View graphListEntryView = view.findViewById(R.id.graph_list_entry_template);
            TextView graph_title = graphListEntryView.findViewById(R.id.graph_list_entry_title);
            graph_title.setText(Constants.TAB_ID_MAPS.get(i));
            return graphListEntryView;
        }
    }

    void registerAndCheck(Object helper) {
        if (!mBus.isRegistered(helper)) {
            mBus.register(helper);
        }
    }

    public class GetSessionDisplayHeaderAsyncTask extends  AsyncTask<Void,Void,
            SessionSummary> {
//          String dateFilePattern = "yyyyMMdd_HHmmss";
//         SimpleDateFormat dateFileFormat = new SimpleDateFormat(dateFilePattern);
        @Override
        protected SessionSummary doInBackground(Void... voids) {
            return SessionCdlDb.getInstance().getSessionDataDAO().getSessionSummaryById(session_id);
        }



        @Override
        protected void onPostExecute(SessionSummary sessionSummary) {
            curSessionSummary = sessionSummary;

            super.onPostExecute(sessionSummary);
//            int act_code = curSessionSummary.getActivity_type();
//            String session_name = Constants.ActivityCodeMap.inverse().get(act_code);
//            String date_str = dateFileFormat.format(sessionSummary.getDate());
//            session_name += "_"+date_str;
            Date date = new Date(sessionSummary.getDate());
            String dateStr = new SimpleDateFormat("dd/MM/YYYY",
                    Locale.getDefault()).format(date);
            String timeStr = new SimpleDateFormat("HH:mm",
                    Locale.getDefault()).format(date);
            String sessionNameStr = sessionSummary.getName();
            String act_type_str = Constants.ActivityCodeMap.inverse().get(curSessionSummary.getActivity_type());
            TimeFmt timeFmt = Common.convertToTimeFmt((long)(sessionSummary.getDuration()*1000));
            String sessionDuration = String.format(Locale.getDefault(), "%02d:%02d:%02d", timeFmt.hr, timeFmt.min, timeFmt.sec);
                    //String.format(Locale.getDefault(),"%.1f sec", sessionSummary.getDuration());
            dateTv.setText(dateStr);
            timeTv.setText(timeStr);
            sessionNameTv.setText(sessionNameStr);

            durationTv.setText(sessionDuration);
            String sessionDataSize =
                    String.valueOf(sessionSummary.getSize()) +"KB";
            sessionDateSizeTv.setText(sessionDataSize);
            String typesOfData = String.valueOf(Constants.typesOfData);
            sessionDataTypesTv.setText(typesOfData);
            sessionActivityTypeTv.setText(act_type_str);


            if(sessionSummary.getNote().trim().length() == 0){
                textNoteTv.setText(getString(R.string.hint_txt));
            }else{
                textNoteTv.setText(sessionSummary.getNote());
            }


            textNoteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        MarkerDialogFragment markerDialogFragment =
                                MarkerDialogFragment.newInstance(sessionSummary.getNote(), "2", sessionSummary.getDate(), sessionSummary.getNote_timestamp());
                        markerDialogFragment.show(getFragmentManager(), MarkerDialogFragment.class.getSimpleName());
                    }catch (Exception ex){
                        Log.d(TAG, "Error="+ex.getMessage());
                    }


//                    Fragment markerDialogFragment =
//                            MarkerDialogFragment.newInstance(sessionSummary.getNote(), "2", sessionSummary.getDate(), sessionSummary.getNote_timestamp());
//                    FragmentTransaction fragmentTransaction =
//                            getFragmentManager().beginTransaction();
//                    fragmentTransaction.add(MapFragment.this.getId(),
//                            markerDialogFragment, MarkerDialogFragment.class.getSimpleName());
//                    fragmentTransaction.addToBackStack(MarkerDialogFragment.class.getSimpleName());
//                    fragmentTransaction.commit();


//                    Fragment mapFragment = MapFragment.newInstance(" SESSION 7", "Latest session");
//                    FragmentTransaction fragmentTransaction = null;
//                    if (getFragmentManager() != null) {
//                        fragmentTransaction = getFragmentManager()
//                                .beginTransaction();
//                        fragmentTransaction.replace(HomeFragment.this.getId(),
//                                mapFragment,
//                                MapFragment.class.getSimpleName());
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
//                    }
                }
            });
        }
    }

    public void onEvent(final MarkerNote markerNote){
        try{
            boolean isUpdated = false;
            switch (markerNote.noteType){
                case "1": // for update the MarkerData's note (outdoor)
                {
                    markerDataCurrent.note = markerNote.note;
                    markerDataCurrent.note_timestamp = new Date().getTime();

                    outdoorMarkerCurrent.setTag(markerDataCurrent);

                    final BitmapDescriptor bitmapDescriptorOrange
                            = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);

                    final BitmapDescriptor bitmapDescriptorYellow
                            = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    if (!markerDataCurrent.note.isEmpty()) {
                        outdoorMarkerCurrent.setIcon(bitmapDescriptorOrange);
                    } else {
                        outdoorMarkerCurrent.setIcon(bitmapDescriptorYellow);
                    }

                    Log.d(TAG, "onEvent: Received a marker data note");
                    new UpdateNoteAsyncTask().execute(markerDataCurrent);
                    isUpdated = true;
                }
                break;
                case "2": // for update the session summary's note
                {
                    if(markerNote.note.isEmpty()){
                        TextView text_note_tv = MapFragment.this.getView().findViewById(R.id.text_note_tv);
                        text_note_tv.setText(getResources().getString(R.string.hint_txt));
                    }else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textNoteTv.setText(markerNote.note);
                            }
                        });
                    }
                        curSessionSummary.setNote(markerNote.note);
                        curSessionSummary.setNote_timestamp(new Date().getTime());
                    new UpdateSessionSummary().execute(curSessionSummary);
                    isUpdated = true;
                    Constants.isNewSessionAdded = true;
                }
                break;

                case "3": // for update the MarkerData's note (indoor)
                {
                    markerDataCurrent.note = markerNote.note;
                    markerDataCurrent.note_timestamp = new Date().getTime();
                    Log.d(TAG,
                            "onEvent: markerDataindoor" + markerDataCurrent.getMarker_timestamp());
                    currentIndoorCard.setTag(markerDataCurrent);

                    if (!markerDataCurrent.note.isEmpty()) {
                        ((TimelineView) this.indoorMarkerCurrent).setMarkerColor(getResources().getColor(R.color.non_empty_note_marker_color));
                        currentIndoorCard.setCardBackgroundColor(getResources().getColor(R.color.non_empty_note_marker_color));
                    } else {
                        ((TimelineView) this.indoorMarkerCurrent).setMarkerColor(getResources().getColor(R.color.empty_note_marker_color));
                        currentIndoorCard.setCardBackgroundColor(getResources().getColor(R.color.empty_note_marker_color));

                    }

                    new UpdateNoteAsyncTask().execute(markerDataCurrent);
                    isUpdated = true;
                }
                break;

                default:
                    Log.d(TAG, "No option");
            }
            // If something update in text, CSV generation's process will start aftr some momemnt
            if(isUpdated){
                CsvPreference.getInstance(MainApplication.getAppContext()).addSessionId(session_id);
                new WorkMgrHelper(MainApplication.getAppContext()).oneTimeCSVGenerationRequest();
            }
        }catch (Exception ex){
            Log.d(TAG, "OnEvent: error msg="+ex.getMessage());
        }

    }
}





