package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.activities.FullScreenActivity;
import com.decalthon.helmet.stability.database.entities.GpsSpeed;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.model.nineaxismodels.ChartType;
import com.decalthon.helmet.stability.model.nineaxismodels.NineAxis;
import com.decalthon.helmet.stability.model.nineaxismodels.QueryParameters;
import com.decalthon.helmet.stability.model.nineaxismodels.QueryType;
import com.decalthon.helmet.stability.model.nineaxismodels.SensorDataEntry;
import com.decalthon.helmet.stability.model.nineaxismodels.TabMetaData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.decalthon.helmet.stability.fragments.MapFragment.gpsSpeeds;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE2_3_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE2_9_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_GPS_SPEED;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE1_9_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE1_3_AXIS;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.DRAG;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.SINGLE_TAP;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.X_ZOOM;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.Y_ZOOM;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * The {@link CustomGraphFragment} is used to show individual graph fragments using a viewpager
 * The pager does not allow swiping to enable dragging and translation features for all
 * line graphs.
 */
public class CustomGraphFragment extends Fragment implements OnChartGestureListener {
    private static String TAG = CustomGraphFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Context mContext;

    //By default, the map fragment is shown on the pager
    private String fragmentType = FRAGMENT_NAME_DEVICE1_9_AXIS;

    //Individual subclasses are used for each inertial element
    //TODO One nine-axis object, add members ACC, gyr, mag

//    //TODO Write  a method to clear all entries
//    private NineAxis.Accelerometer accelerometer;
//    private NineAxis.Gyroscope gyroscope;
//    private NineAxis.Magnetometer magnetometer;

    //These zoom variables are defined for capturing for an experimental trial
    //TODO If zoom effect has to exhibited comm to all graphs in the fragment, use these variables
    private float zoomX;
    private float zoomY;
    private float last_zoomX;
    private float last_zoomY;

    //TODO If the zoom effect has to be common to all graphs in the fragment, use lastPinched
    private String lastPinched = "";
    private String lastDragged = "";

    private float translateX;
    private float translateY;
    long session_id, clicked_ts, session_start_ts;
    //    public static Map<String, LineData> SaveLineData = new HashMap<>();
    public static Map<String, TabMetaData> SAVE_TAB_DATA = new HashMap<>();
    public static float START_TS, END_TS;
    private final int MAX_INTERVAL = 180000;// Graph will display in then rabge [-180sec, 180sec]
    LineChart accChart, gyrChart, magChart;



    //    private String PATH = null;
//    private File root_dir;
    /* Fling events are treated as drag. Owing to a lack of  difference between drag and fling, a
    alternatives are being sought,for rendering fling and drag as separate events, coupled with
    regular X-Y translation changes
     */
    private String lastFlung;


    public CustomGraphFragment() {
        //Required empty public constructor
    }


    /*When any concrete CustomGraphFragment class is instantiated, this
     * constructor provides the required fragment type and corresponding view*/
    public CustomGraphFragment(final String fragmentType, Long session_id, Long clicked_ts, Long start_ts) {
        this.fragmentType = fragmentType;
        this.clicked_ts = clicked_ts;
        this.session_id = session_id;
        this.session_start_ts = start_ts;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomGraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomGraphFragment newInstance(String param1, String param2) {
        CustomGraphFragment fragment = new CustomGraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment creation
     *
     * @param savedInstanceState Any restore-able data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Every page is associated with a chart and inflated based on the fragmentType
     *
     * @param inflater           The callback argument which inflates a {@link CustomGraphFragment}
     * @param container          The parent container which holds the graph fragment
     * @param savedInstanceState Any restore-able data
     * @return The view of the fragment, a rectangular area on the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //Nine axis fragment includes accelerometer, gyroscope and magnetometer readings

        /*
        These are for three-dimensional acceleration, three-dimensional rotation and
        three dimensional magnetic field values
         */

//        if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE1_9_AXIS)){
//            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
//        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE1_3_AXIS)){
//            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
//        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE2_9_AXIS)){
//            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
//        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE2_3_AXIS)){
//            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
//        }else if(fragmentType.equalsIgnoreCase(GPS_SPEED_NAME)){
//            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
//        }

//        try{
//            root_dir = null;
//            PATH = getContext().getPackageName() + File.separator + Constants.CHART_LOG_DIR;
//            root_dir = FileUtilities.createDirIfNotExists(PATH);
//        }catch (Exception ex){
//
//        }

        View anygraphView = inflater.inflate(R.layout.fragment_plot_graph, container, false);
        accChart = anygraphView.findViewById(R.id.accelerometerChart);
        gyrChart = anygraphView.findViewById(R.id.gyroscopeChart);
        magChart = anygraphView.findViewById(R.id.magnetometerChart);
        return anygraphView;
    }

//    /**
//     *
//     * @param tabType
//     */
//    private void saveChart(String tabType){
//        if(root_dir != null){
//            try{
//                File chartFile = new File(root_dir, tabType);
//                FileWriter writer = new FileWriter(chartFile,false);
//                writer.close();
//            }catch (Exception ex){
//                Log.d(TAG, ""+ex.getMessage());
//            }
////            lineChart.saveToPath(tabType, chartFile.getAbsolutePath());
////            ToDo: saveToPath(String title, String path) or saveToGallery(String title)
//        }
//    }
//
//    /**
//     * get chart from file
//     * @param tabType
//     */
//    private void getChart(String tabType){
//        if(root_dir != null){
//            File chartFile = new File(root_dir, tabType);
//            if(chartFile.exists()){
//
//                //ToDo: getChart() from file
//
//            }
//        }
//    }

    /**
     * Once the graph view is created, corresponding charts are displayed with session data.
     *
     * @param view               The rectangular area of the screen under the actionbar that displays the graph.
     * @param savedInstanceState Currently used only in the superclass context
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LineChart accLineChartView = accChart;
        final LineChart gyrLineChartView = gyrChart;
        final LineChart magLineChartView = magChart;
        if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType) == null) {
            CustomGraphFragment.SAVE_TAB_DATA.put(fragmentType, new TabMetaData());
        }

        switch (fragmentType) {
            //Views defined for nine-axis data. Scrollable if rendered outside viewport bounds
            case FRAGMENT_NAME_DEVICE1_9_AXIS:
                Common.show_wait_bar_count(getContext(), "Loading 9-axis Dev1 graph", 3);
                //Currently random data is generated in {@link CustomGraphFragment#getAxisEntries}
//                LineData lineDataAcc = SaveLineData.get(getString(R.string.acceleration)+getString(R.string.device1_tv)+getString(R.string.nine_axis));
//                if(lineDataAcc==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long t1 = System.currentTimeMillis();
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device1_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.acceleration);
                            queryParameters.chartType = ChartType.ACC;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(accLineChartView, queryParameters);
//                                NineAxis.getInstance()
//                                        .drawGraph(getString(R.string.acceleration), accLineChartView, getString(R.string.device1_tv), getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        long t2 = System.currentTimeMillis();
                        System.out.println("Time Acc=" + (t2 - t1));
                        Common.dismiss_wait_bar_count();
                    }
                }).start();

//                LineData lineDataGyr = SaveLineData.get(getString(R.string.gyroscope)+getString(R.string.device1_tv)+getString(R.string.nine_axis));
//                if(lineDataGyr==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long t1 = System.currentTimeMillis();
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device1_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.gyroscope);
                            queryParameters.chartType = ChartType.GYR;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).gyrChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(gyrLineChartView, queryParameters);
//                                NineAxis.getInstance()
//                                        .drawGraph(getString(R.string.gyroscope),
//                                                gyrLineChartView,
//                                                getString(R.string.device1_tv),
//                                                getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        long t2 = System.currentTimeMillis();
                        System.out.println("Time Gyr=" + (t2 - t1));
                        Common.dismiss_wait_bar_count();
                    }
                }).start();


//                LineData lineDataMag = SaveLineData.get(getString(R.string.magnetometer)+getString(R.string.device1_tv)+getString(R.string.nine_axis));
//                if(lineDataMag==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long t1 = System.currentTimeMillis();
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device1_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.magnetometer);
                            queryParameters.chartType = ChartType.MAG;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).magChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(magLineChartView, queryParameters);
//                                NineAxis.getInstance()
//                                        .drawGraph(getString(R.string.magnetometer), magLineChartView, getString(R.string.device1_tv), getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        long t2 = System.currentTimeMillis();
                        System.out.println("Time Mag=" + (t2 - t1));
                        Common.dismiss_wait_bar_count();
                    }
                }).start();
                //Points are added to the graph similar to occupy an area on the graph viewport.

                /*In the method below,the points are added such that every update appears to have happened
                every increment of NineAxis#milliSecondUpdater} with available random data
                 */
//                try {
////                    staticUpdateAll(accelerometer,gyroscope,magnetometer);
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }

                /*
                 * Once the updated time was calculated for the entire data set the  datasets are
                 * updated, other words, spaced out in the time gap decided by the millisecond updater.
                 * This was done as part of testing the graph capability
                 */
//                timeUpdateCharts(accelerometer,gyroscope,magnetometer);

                /*
                 * This overloaded method, prepares the chart for all the nine-axes graphs
                 */
//                prepareChart(accLineChartView,gyrLineChartView,magLineChartView);

                /*
                 * Each line dataset is associated with linedata to populate the graphs with points
                 * */
//                setDataNineAxis(accLineChartView,accelerometer.accChartData);
//                setDataNineAxis(gyrLineChartView,gyroscope.gyroChartData);
//                setDataNineAxis(magLineChartView,magnetometer.magnetoChartData);

                // All graphs are refreshed once to invalidate any existing plots with a new plot
                nineAxisGraphRefresh(accLineChartView, gyrLineChartView, magLineChartView);

                /*
                 *Once the charts have been created, the views are shifter to the centres of the
                 * respective datasets.
                 */

//                accLineChartView.moveViewToX(accelerometer.getAccLineDataSetX().getXMax()/2);
//                gyrLineChartView.moveViewToX(gyroscope.gyroLineDataSetX.getXMax()/2);
//                magLineChartView.moveViewToX(magnetometer.getMagLineDataSetX().getXMax()/2);

                ViewGroup.LayoutParams layoutParams = view.findViewById(R.id.accFrame).
                        getLayoutParams();

                //Using the displaymetrics, the title of the charts are added
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;


                accLineChartView.getDescription().setPosition(width / 2, layoutParams.height - 100);
                gyrLineChartView.getDescription().setPosition(width / 2, layoutParams.height - 100);
                magLineChartView.getDescription().setPosition(width / 2, layoutParams.height - 100);

                /*This feature is under development. Individual views can be translated
                to a fullscreen
                 */

                final ImageView ivAcc = view.findViewById(R.id.accFS);
                final ImageView ivGyr = view.findViewById(R.id.gyroFS);
                final ImageView ivMag = view.findViewById(R.id.magFS);

                //The accelerometer fullscreen handle
                ivAcc.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                etString(R.string.acceleration), accLineChartView, getString(R.string.device1_tv), getString(R.string.nine_axis)
//                                registerFullScreen(getString(R.string.acceleration),
//                                        getString(R.string.device1_tv), getString(R.string.nine_axis));
                                float zoomx = accLineChartView.getScaleX();
                                float minx = accLineChartView.getLowestVisibleX();
                                registerFullScreen(fragmentType, ChartType.ACC.toString(), zoomx, minx);
                            }
                        });
                //The gyroscope fullscreen handle
                ivGyr.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                registerFullScreen(getString(R.string.gyroscope),
//                                        getString(R.string.device1_tv), getString(R.string.nine_axis));
                                float zoomx = gyrLineChartView.getScaleX();
                                float minx = gyrLineChartView.getLowestVisibleX();
                                registerFullScreen(fragmentType, ChartType.GYR.toString(), zoomx, minx);
                            }
                        });
                //The magnetometer fullscreen handle
                ivMag.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                registerFullScreen(getString(R.string.magnetometer),
//                                        getString(R.string.device1_tv), getString(R.string.nine_axis));
                                float zoomx = magLineChartView.getScaleX();
                                float minx = magLineChartView.getLowestVisibleX();
                                registerFullScreen(fragmentType, ChartType.MAG.toString(), zoomx, minx);
                            }
                        });

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println("New entries adding");
//                        Common.wait(6000);
//                        long t1 = System.currentTimeMillis();
//                        try {
//                            NineAxis.getInstance()
//                                    .drawGraph_udpate(getString(R.string.acceleration), accLineChartView, getString(R.string.device1_tv), getString(R.string.nine_axis));
//                        } catch (ExecutionException | InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        long t2 = System.currentTimeMillis();
////                        float time = 50000;
////                        float x, y, z;
////                        Random random = new Random();
////                        for (int i = 0; i < 3000; i++) {
////                            time += 10;
////                            x = 10*random.nextFloat();y = 10*random.nextFloat();z = 10*random.nextFloat();
////                            accLineChartView.getData().getDataSetByIndex(0).addEntry(new Entry(time/1000.0f, x));
////                            accLineChartView.getData().getDataSetByIndex(1).addEntry(new Entry(time/1000.0f, y));
////                            accLineChartView.getData().getDataSetByIndex(2).addEntry(new Entry(time/1000.0f, z));
////
////                            gyrLineChartView.getData().getDataSetByIndex(0).addEntry(new Entry(time/1000.0f, x));
////                            gyrLineChartView.getData().getDataSetByIndex(1).addEntry(new Entry(time/1000.0f, y));
////                            gyrLineChartView.getData().getDataSetByIndex(2).addEntry(new Entry(time/1000.0f, z));
////
////                            magLineChartView.getData().getDataSetByIndex(0).addEntry(new Entry(time/1000.0f, x));
////                            magLineChartView.getData().getDataSetByIndex(1).addEntry(new Entry(time/1000.0f, y));
////                            magLineChartView.getData().getDataSetByIndex(2).addEntry(new Entry(time/1000.0f, z));
////
////                        }
////                        accLineChartView.notifyDataSetChanged();
////                        accLineChartView.invalidate();
////                        gyrLineChartView.notifyDataSetChanged();
////                        gyrLineChartView.invalidate();
////                        magLineChartView.notifyDataSetChanged();
////                        magLineChartView.invalidate();
//                        System.out.println("Update entries is done: time="+(t2-t1));
//                    }
//                }).start();


                accLineChartView.setOnChartGestureListener(this);
                gyrLineChartView.setOnChartGestureListener(this);
                magLineChartView.setOnChartGestureListener(this);

                break;

            case FRAGMENT_NAME_DEVICE1_3_AXIS:
                Common.show_wait_bar_count(getContext(), "Loading 3-axis Dev1 graph", 1);
                long session_id1 = 1;
                view.findViewById(R.id.gyrFrame).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.magFrame).setVisibility(View.INVISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device1_tv);
                            queryParameters.axes = getString(R.string.three_axis);
                            queryParameters.graphType = getString(R.string.acceleration);
                            queryParameters.chartType = ChartType.ACC;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(accLineChartView, queryParameters);

//                            NineAxis.getInstance()
//                                    .drawGraph(getString(R.string.acceleration), accLineChartView, getString(R.string.device1_tv), getString(R.string.three_axis));
                            //gyrLineChartView.setVisibility(View.GONE);
                            //magLineChartView.setVisibility(View.GONE);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Common.dismiss_wait_bar_count();
                    }
                }).start();
//                NineAxis.Accelerometer threeAxis =
//                        new NineAxis(CustomGraphFragment.this.getContext(),session_id1).new Accelerometer();

                //Accelerometer readings are randomly generated in a similar fashion to Nine-Axes
//                try {
//                    threeAxis.getReadingsAcc();
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                //Spread distribution of upto 60000 points plotted with even time gaps
//                try {
//                    threeAxis.simulateStaticMilliSecondUpdate();
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                threeAxis.displayStaticMilliSecondUpdate();
//
//                //Only the first graph is used from the layout
//                final LineChart threeAxisChartView = view.findViewById(R.id.accelerometerChart);
//
//                //Prepare chart parameters only for the three-axis plot
//                prepareChart(threeAxisChartView);
//
//                view.findViewById(R.id.gyrFrame).setVisibility(View.GONE);
//                view.findViewById(R.id.magFrame).setVisibility(View.GONE);
//
//                //Update the three-axis data manually instead of using a nine-axis wrapper method
//                setDataNineAxis(threeAxisChartView,threeAxis.accChartData);
//
//                //Displays titles
//                ViewGroup.LayoutParams layoutParamsThreeAxis = view.findViewById(R.id.accFrame).
//                        getLayoutParams();
//
//                DisplayMetrics displayMetricsThreeAxis = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetricsThreeAxis);
//                int heightPixelsT = displayMetricsThreeAxis.heightPixels;
//                int widthPixelsT = displayMetricsThreeAxis.widthPixels;
//
//                threeAxisChartView.invalidate();
//                threeAxisChartView.moveViewToX(threeAxis.getAccLineDataSetX().getXMax()/2);
//                threeAxisChartView.getDescription().setPosition(widthPixelsT/2,layoutParamsThreeAxis.height-100);

                /*
                Rendering single three axis graph as fullscreen
                 */
                final ImageView ivAccT = view.findViewById(R.id.accFS);

                ivAccT.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //registerFullScreen(getString(R.string.acceleration), getString(R.string.device1_tv), getString(R.string.three_axis));
                                float zoomx = accLineChartView.getScaleX();
                                float minx = accLineChartView.getLowestVisibleX();
                                registerFullScreen(fragmentType, ChartType.ACC.toString(), zoomx, minx);
                            }
                        });

                break;

            case FRAGMENT_NAME_DEVICE2_9_AXIS:
                Common.show_wait_bar_count(getContext(), "Loading 9-axis Dev2 graph", 3);
//                accelerometer = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Accelerometer();
//                gyroscope = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Gyroscope();
//                magnetometer = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Magnetometer();

                //TODO On destroy , clear all entries

                //Currently random data is generated in {@link CustomGraphFragment#getAxisEntries}
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device2_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.acceleration);
                            queryParameters.chartType = ChartType.ACC;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(accLineChartView, queryParameters);
//                            NineAxis.getInstance()
//                                    .drawGraph(getString(R.string.acceleration), accLineChartView, getString(R.string.device2_tv), getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Common.dismiss_wait_bar_count();
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device2_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.gyroscope);
                            queryParameters.chartType = ChartType.GYR;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).gyrChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(gyrLineChartView, queryParameters);
//                            NineAxis.getInstance()
//                                    .drawGraph(getString(R.string.gyroscope),
//                                            gyrLineChartView,
//                                            getString(R.string.device2_tv),
//                                            getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Common.dismiss_wait_bar_count();
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device2_tv);
                            queryParameters.axes = getString(R.string.nine_axis);
                            queryParameters.graphType = getString(R.string.magnetometer);
                            queryParameters.chartType = ChartType.MAG;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).magChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(magLineChartView, queryParameters);
//                            NineAxis.getInstance()
//                                    .drawGraph(getString(R.string.magnetometer), magLineChartView, getString(R.string.device1_tv), getString(R.string.nine_axis));
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Common.dismiss_wait_bar_count();
                    }
                }).start();

                //Points are added to the graph similar to occupy an area on the graph viewport.

                /*In the method below,the points are added such that every update appears to have happened
                every increment of NineAxis#milliSecondUpdater} with available random data
                 */
//                try {
////                    staticUpdateAll(accelerometer,gyroscope,magnetometer);
//                } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
//                }

                /*
                 * Once the updated time was calculated for the entire data set the  datasets are
                 * updated, other words, spaced out in the time gap decided by the millisecond updater.
                 * This was done as part of testing the graph capability
                 */
//                timeUpdateCharts(accelerometer,gyroscope,magnetometer);

                /*
                 * This overloaded method, prepares the chart for all the nine-axes graphs
                 */
//                prepareChart(accLineChartView,gyrLineChartView,magLineChartView);

                /*
                 * Each line dataset is associated with linedata to populate the graphs with points
                 * */
//                setDataNineAxis(accLineChartView,accelerometer.accChartData);
//                setDataNineAxis(gyrLineChartView,gyroscope.gyroChartData);
//                setDataNineAxis(magLineChartView,magnetometer.magnetoChartData);

                // All graphs are refreshed once to invalidate any existing plots with a new plot
                nineAxisGraphRefresh(accLineChartView, gyrLineChartView, magLineChartView);

                /*
                 *Once the charts have been created, the views are shifter to the centres of the
                 * respective datasets.
                 */

//                accLineChartView.moveViewToX(accelerometer.getAccLineDataSetX().getXMax()/2);
//                gyrLineChartView.moveViewToX(gyroscope.gyroLineDataSetX.getXMax()/2);
//                magLineChartView.moveViewToX(magnetometer.getMagLineDataSetX().getXMax()/2);

                ViewGroup.LayoutParams layoutParams2 = view.findViewById(R.id.accFrame).
                        getLayoutParams();

                //Using the displaymetrics, the title of the charts are added
                DisplayMetrics displayMetrics2 = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics2);
                int width22 = displayMetrics2.widthPixels;


                accLineChartView.getDescription().setPosition(width22 / 2, layoutParams2.height - 100);
                gyrLineChartView.getDescription().setPosition(width22 / 2, layoutParams2.height - 100);
                magLineChartView.getDescription().setPosition(width22 / 2, layoutParams2.height - 100);

                /*This feature is under development. Individual views can be translated
                to a fullscreen
                 */

                final ImageView ivAcc1 = view.findViewById(R.id.accFS);
                final ImageView ivGyr1 = view.findViewById(R.id.gyroFS);
                final ImageView ivMag1 = view.findViewById(R.id.magFS);

                //The accelerometer fullscreen handle
                ivAcc1.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                float zoomx = accLineChartView.getScaleX();
                                float minx = accLineChartView.getLowestVisibleX();
//                                registerFullScreen(getString(R.string.acceleration), getString(R.string.device2_tv), getString(R.string.nine_axis));
                                registerFullScreen(fragmentType, ChartType.ACC.toString(), zoomx, minx);
                            }
                        });
                //The gyroscope fullscreen handle
                ivGyr1.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                registerFullScreen(getString(R.string.gyroscope),
//                                        getString(R.string.device2_tv),
//                                        getString(R.string.nine_axis));
                                float zoomx = gyrLineChartView.getScaleX();
                                float minx = gyrLineChartView.getLowestVisibleX();
                                registerFullScreen(fragmentType, ChartType.GYR.toString(), zoomx, minx);
                            }
                        });
                //The magnetometer fullscreen handle
                ivMag1.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                float zoomx = magLineChartView.getScaleX();
                                float minx = magLineChartView.getLowestVisibleX();
//                                registerFullScreen(getString(R.string.magnetometer), getString(R.string.device1_tv), getString(R.string.nine_axis));
                                registerFullScreen(fragmentType, ChartType.MAG.toString(), zoomx, minx);
                            }
                        });
                accLineChartView.setOnChartGestureListener(this);
                gyrLineChartView.setOnChartGestureListener(this);
                magLineChartView.setOnChartGestureListener(this);
                break;

            case FRAGMENT_NAME_DEVICE2_3_AXIS:
                Common.show_wait_bar_count(getContext(), "Loading 3-axis Dev2 graph", 1);
                long session_id2 = 1;
                view.findViewById(R.id.gyrFrame).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.magFrame).setVisibility(View.INVISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            QueryParameters queryParameters = new QueryParameters();
                            queryParameters.device_id = getString(R.string.device2_tv);
                            queryParameters.axes = getString(R.string.three_axis);
                            queryParameters.graphType = getString(R.string.acceleration);
                            queryParameters.chartType = ChartType.ACC;
                            queryParameters.click_ts = clicked_ts;
                            queryParameters.session_id = session_id;
                            queryParameters.session_start_ts = session_start_ts;
                            queryParameters.fragmentType = fragmentType;
                            if (CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData.size() == 0) {
                                queryParameters.queryType = QueryType.INITIAL_LOAD;
                                queryParameters.tStart = queryParameters.click_ts - MAX_INTERVAL;
                                queryParameters.tEnd = queryParameters.click_ts + MAX_INTERVAL;
                            } else {
                                queryParameters.queryType = QueryType.RELOAD;
                            }
                            NineAxis.getInstance().drawGraph(accLineChartView, queryParameters);
//                            NineAxis.getInstance()
//                                    .drawGraph(getString(R.string.acceleration), accLineChartView, getString(R.string.device2_tv), getString(R.string.three_axis));
                            //gyrLineChartView.setVisibility(View.GONE);
                            //magLineChartView.setVisibility(View.GONE);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        Common.dismiss_wait_bar_count();
                    }
                }).start();

                final ImageView ivAcc_dev2_3axis = view.findViewById(R.id.accFS);
                ivAcc_dev2_3axis.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                float zoomx = accLineChartView.getScaleX();
                                float minx = accLineChartView.getLowestVisibleX();
//                                registerFullScreen(getString(R.string.acceleration),  getString(R.string.device2_tv), getString(R.string.three_axis));
                                registerFullScreen(fragmentType, ChartType.ACC.toString(), zoomx, minx);
                            }
                        });

                break;

            case FRAGMENT_NAME_GPS_SPEED:

                Map<Integer, SensorDataEntry> gpsMaps = CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData;
                if (gpsSpeeds != null && gpsSpeeds.size() > 0) {
                    Common.show_wait_bar_count(getContext(), "Loading GPS Speed graph", 1);
                    String key = getString(R.string.acceleration) + getString(R.string.device2_tv) + "GPS_speed";
                    LineChart gpsSpeedChartView = accLineChartView;
                    LineData gpsSpeedLineData = new LineData();
                    ;

//                if(gpsSpeedLineData == null){
                    LineDataSet lineDataSetX = new LineDataSet(null, "speed(km/h)");
                    lineDataSetX.setColor(Color.BLUE);
                    NineAxis.formatPlotCircles(lineDataSetX);
                    gpsSpeedChartView.setNoDataText("Loading...");
                    gpsSpeedChartView.setDragEnabled(true);
                    gpsSpeedChartView.setDrawGridBackground(false);
                    gpsSpeedChartView.setPinchZoom(true);
                    gpsSpeedChartView.getLegend().setEnabled(true);
                    gpsSpeedChartView.getXAxis().setDrawGridLines(true);
                    gpsSpeedChartView.getXAxis().setDrawAxisLine(true);
                    gpsSpeedChartView.getAxisLeft().setTextColor(Color.WHITE);
                    gpsSpeedChartView.getXAxis().setTextColor(Color.WHITE);
                    gpsSpeedChartView.getLegend().setTextColor(Color.WHITE);

                    long timestamp_start = gpsSpeeds.get(0).timestamp;
                    for (GpsSpeed gpsSpeed : gpsSpeeds) {
                        lineDataSetX.addEntry(new Entry((float) (gpsSpeed.getTimestamp() - timestamp_start) / 1000.0f,
                                gpsSpeed.speed));
                        SensorDataEntry sensorDataEntry = new SensorDataEntry();
                        sensorDataEntry.setXval(gpsSpeed.speed);
                        sensorDataEntry.setYval(0.0f);
                        sensorDataEntry.setZval(0.0f);
                        gpsMaps.put((int) (gpsSpeed.getTimestamp() - timestamp_start), sensorDataEntry);
                        //System.out.println("cur ts="+gpsSpeed.getTimestamp()+", timestamp_start="+timestamp_start+", diff="+(gpsSpeed.getTimestamp() -timestamp_start));
                    }
                    CustomGraphFragment.SAVE_TAB_DATA.get(fragmentType).accChartData = gpsMaps;
                    Common.dismiss_wait_bar_count();
                    //gpsSpeedLineData = new LineData();

                    gpsSpeedLineData.addDataSet(lineDataSetX);
//                    CustomGraphFragment.SaveLineData.put(key, gpsSpeedLineData);
//                }
                    gpsSpeedChartView.setData(gpsSpeedLineData);
//                gpsSpeedChartView.setScaleX(0.5f);


                    Legend legend = gpsSpeedChartView.getLegend();
                    legend.setForm(Legend.LegendForm.CIRCLE);
                    gpsSpeedChartView.getDescription().setText(mContext.getString(R.string.time_measure));
                    gpsSpeedChartView.getDescription().setTextColor(Color.WHITE);
                    legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

                    XAxis xAxis = gpsSpeedChartView.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTextColor(Color.WHITE);
                    YAxis yAxisRight = gpsSpeedChartView.getAxisRight();
                    yAxisRight.setEnabled(false);
                    gpsSpeedChartView.invalidate();

                    final ImageView ivAcc_gps_speed = view.findViewById(R.id.accFS);

                    ivAcc_gps_speed.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                registerFullScreen(getString(R.string.acceleration),  getString(R.string.device2_tv), "GPS_speed");
                                    float zoomx = gpsSpeedChartView.getScaleX();
                                    float minx = gpsSpeedChartView.getLowestVisibleX();
                                    registerFullScreen(fragmentType, ChartType.GPS_SPEED.toString(), zoomx, minx);
                                }
                            });
                }

                try {
                    TextView gps_speed_tv =
                            view.findViewById(R.id.accFrame).findViewById(R.id.graph1_tv);
                    gps_speed_tv.setText("GPS Speed");
                    TextView gps_speed_tv2 =
                            view.findViewById(R.id.gyrFrame).findViewById(R.id.graph2_tv);
                    TextView gps_speed_tv3 =
                            view.findViewById(R.id.magFrame).findViewById(R.id.graph3_tv);
                    gps_speed_tv2.setVisibility(View.INVISIBLE);
                    gps_speed_tv3.setVisibility(View.INVISIBLE);
                } catch (Exception ex) {

                }
                view.findViewById(R.id.gyroscopeChart).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.magnetometerChart).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.gyroFS).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.magFS).setVisibility(View.INVISIBLE);

                break;
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    /**
     * Invalidates any existing chart data and replaces with a fresh plot graph.
     *
     * @param accLineChartView The accelerometer LineChart
     * @param gyrLineChartView The gyroscope LineChart
     * @param magLineChartView the magnetometer LineChart
     */
    private void nineAxisGraphRefresh(LineChart accLineChartView, LineChart gyrLineChartView, LineChart magLineChartView) {
        accLineChartView.invalidate();
        gyrLineChartView.invalidate();
        magLineChartView.invalidate();

        /*Each event on a graph view invokes the appropriate callbacks of the OnChartGestureListener*/
        accLineChartView.setOnChartGestureListener(this);
        gyrLineChartView.setOnChartGestureListener(this);
        magLineChartView.setOnChartGestureListener(this);
    }

    private void registerFullScreen(final String graph_type, final String device_id, String axis_type) {
        Intent in = new Intent(getActivity(), FullScreenActivity.class);
        in.putExtra("GRAPH_TYPE", graph_type);
        in.putExtra("DEVICE_ID", device_id);
        in.putExtra("AXIS_TYPE", axis_type);
        in.putExtra("DATA_SIZE", 1);

        startActivity(in);
    }

    private void registerFullScreen(final String fragmentType, final String chart_type, float zoomx, float minx) {
        Intent in = new Intent(getActivity(), FullScreenActivity.class);
        in.putExtra("FRAG_TYPE", fragmentType);
        in.putExtra("CHART_TYPE", chart_type);
        in.putExtra("ZOOM_X", zoomx);
        in.putExtra("MIN_X", minx);
        startActivity(in);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mContext = context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me                   The motion event type
     * @param lastPerformedGesture The gesture that invoked the "start" callback
     */
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        System.out.println("-----------onChartGestureStart-----------: ts=" + System.currentTimeMillis()+", gesture="+lastPerformedGesture);

        //Default implementation
    }


    //=====CODE UNDER DEVELOPMENT==========//
    private long millisecond1, millisecond2;


    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me                   The motion event type
     * @param lastPerformedGesture The gesture that invoked the "end" callback
     */
    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Default implementation
        millisecond2 = System.currentTimeMillis();
//        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = accChart;
        LineChart gyroscope = gyrChart;
        LineChart magnetometer = magChart;

        accelerometer.setScaleYEnabled(false);
        gyroscope.setScaleYEnabled(false);
        magnetometer.setScaleYEnabled(false);

        long diff = (millisecond2 - millisecond1);
        System.out.println("-----------onChartGestureEnd-----------: ts=" + System.currentTimeMillis()+", gesture="+lastPerformedGesture);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {


                    if (lastPerformedGesture == DRAG || lastPerformedGesture == SINGLE_TAP) {
                        float prevX = Float.MIN_VALUE;
                        while(Math.abs(prevX - translateX) > 0.0001){
                            prevX = translateX;
                            Common.wait(50);
//                            System.out.println("Wait...");
                        }
//                        System.out.println("Syncing...");
//                        boolean isHighest = false;
                        if (lastDragged.equals("Accelerometer")) {
                            //Todo: get highest and Lowest visibleX
                            gyroscope.moveViewToX(accChart.getLowestVisibleX());
                            magnetometer.moveViewToX(accChart.getLowestVisibleX());
                            accelerometer.moveViewToX(accChart.getLowestVisibleX());
//                            System.out.println("Lowest Visible X=" + accelerometer.getLowestVisibleX() + "Highest Visible X" + accelerometer.getHighestVisibleX());
                        } else if (lastDragged.equals("Gyroscope")) {
                            //Todo: get highest and Lowest visibleX
                            accelerometer.moveViewToX(gyroscope.getLowestVisibleX());
                            magnetometer.moveViewToX(gyroscope.getLowestVisibleX());
                            gyroscope.moveViewToX(gyroscope.getLowestVisibleX());
                        } else if (lastDragged.equals("Magnetometer")) {
                            //Todo: get highest and Lowest visibleX
                            gyroscope.moveViewToX(magnetometer.getLowestVisibleX());
                            accelerometer.moveViewToX(magnetometer.getLowestVisibleX());
                            magnetometer.moveViewToX(magnetometer.getLowestVisibleX());
                        }
                    }
//                    if (translateX < -20) {
//                        System.out.println("Drag to Left");
//                        //Todo:
//                        // check highest X is about 10% less than availabe data' highest x
//                        // load more data for right side
////                            isHighest = true;
//                    } else if (translateX > 20f) {
//                        System.out.println("Drag to Right");
//                        //Todo:
//                        // check lowest X is about 10% greater than availabe data' lowest x
//                        // load more data to left side
////                            isHighest = false;
//                    }
                } catch (NullPointerException e) {
                    System.out.println("Waiting for init");
                }
            }
        }).start();

        if (lastPerformedGesture == X_ZOOM || lastPerformedGesture == Y_ZOOM) {
            boolean zoom_out = false;
            float visible_min_x = Float.MIN_VALUE, visible_max_x = Float.MAX_VALUE;
            if (lastPinched.equals("Accelerometer")) {
                gyroscope.zoom(zoomX,0, gyroscope.getWidth() / 2.0f,gyroscope.getHeight() / 2.0f);
                magnetometer.zoom(zoomX, 0, magnetometer.getWidth() / 2.0f, magnetometer.getHeight() / 2.0f);
                visible_min_x = accChart.getLowestVisibleX();
                visible_max_x = accChart.getHighestVisibleX();

//                Log.d(TAG, "onChartGestureEnd: Visible min x"+visible_min_x);
//                Log.d(TAG, "onChartGestureEnd: Visible max x" + visible_max_x);

                gyroscope.moveViewToX(visible_min_x);
                magnetometer.moveViewToX(visible_min_x);
                accelerometer.moveViewToX(visible_min_x);

                visible_min_x = accChart.getLowestVisibleX();
                visible_max_x = accChart.getHighestVisibleX();

//                Log.d(TAG, "onChartGestureEnd: Visible min x"+visible_min_x);
//                Log.d(TAG, "onChartGestureEnd: Visible max x" + visible_max_x);

            } else if (lastPinched.equals("Gyroscope")) {
                accelerometer.zoom(zoomX, 0, accelerometer.getWidth() / 2.0f, accelerometer.getHeight() / 2.0f);
                magnetometer.zoom(zoomX, 0  , magnetometer.getWidth() / 2.0f, magnetometer.getHeight() / 2.0f);

                visible_min_x = gyrChart.getLowestVisibleX();
                visible_max_x = gyrChart.getHighestVisibleX();

//                Log.d(TAG, "onChartGestureEnd: Visible min x"+visible_min_x);
//                Log.d(TAG, "onChartGestureEnd: Visible max x" + visible_max_x);

                magnetometer.moveViewToX(visible_min_x);
                accelerometer.moveViewToX(visible_min_x);
                gyroscope.moveViewToX(visible_min_x);

//                Log.d(TAG, "onChartGestureEnd: Visible min x"+visible_min_x);
//                Log.d(TAG, "onChartGestureEnd: Visible max x" + visible_max_x);

            } else if (lastPinched.equals("Magnetometer")) {
//            if(zoomX - last_zoomX > 0)
                gyroscope.zoom(zoomX, 0, gyroscope.getWidth() / 2.0f, gyroscope.getHeight() / 2.0f);
                accelerometer.zoom(zoomX, 0, accelerometer.getWidth() / 2.0f, accelerometer.getHeight() / 2.0f);

                visible_min_x = magChart.getLowestVisibleX();
                visible_max_x = magChart.getHighestVisibleX();

                gyroscope.moveViewToX(visible_min_x);
                accelerometer.moveViewToX(visible_min_x);
                magnetometer.moveViewToX(visible_min_x);
            }

        }


//        try {
//            if (lastPerformedGesture == DRAG && lastDragged.equals("Accelerometer")
//            || (lastPerformedGesture == FLING && lastFlung.equals("Accelerometer"))) {
//                gyroscope.moveViewToX(accelerometer.getLowestVisibleX());
//                magnetometer.moveViewToX(accelerometer.getLowestVisibleX());
//            }
//
//            if (lastPerformedGesture == DRAG && lastDragged.equals("Gyroscope")
//            || (lastPerformedGesture == FLING && lastFlung.equals("Gyroscope"))) {
//                accelerometer.moveViewToX(gyroscope.getLowestVisibleX());
//                magnetometer.moveViewToX(gyroscope.getLowestVisibleX());
//            }
//
//            if (lastPerformedGesture == DRAG && lastDragged.equals("Magnetometer")
//            || (lastPerformedGesture == FLING && lastFlung.equals("Magnetometer"))) {
//                gyroscope.moveViewToX(magnetometer.getLowestVisibleX());
//                accelerometer.moveViewToX(magnetometer.getLowestVisibleX());
//            }
//        }
//        catch (NullPointerException e){
//            System.out.println("Waiting for init");
//        }

//        try {
//            if (lastPerformedGesture == FLING && lastFlung.equals("Accelerometer")) {
//                gyroscope.moveViewToX(accelerometer.getLowestVisibleX());
//                magnetometer.moveViewToX(accelerometer.getLowestVisibleX());
//            }
//
//            if (lastPerformedGesture == FLING && lastFlung.equals("Gyroscope")) {
//                accelerometer.moveViewToX(gyroscope.getLowestVisibleX());
//                magnetometer.moveViewToX(gyroscope.getLowestVisibleX());
//            }
//
//            if (lastPerformedGesture == FLING && lastFlung.equals("Magnetometer")) {
//                gyroscope.moveViewToX(magnetometer.getLowestVisibleX());
//                accelerometer.moveViewToX(magnetometer.getLowestVisibleX());
//            }
//        }
//        catch (NullPointerException e){
//            System.out.println("Waiting for init");
//        }
        System.out.println("Gesture has ended" + lastPerformedGesture);
    }

    /**
     * Callbacks when the chart is longpressed.
     *
     * @param me
     */
    @Override
    public void onChartLongPressed(MotionEvent me) {
        //Default implementation
    }

    /**
     * Callbacks when the chart is double-tapped.
     *
     * @param me
     */
    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        //Default implementation
    }

    /**
     * Callbacks when the chart is single-tapped.
     *
     * @param me
     */
    @Override
    public void onChartSingleTapped(MotionEvent me) {
        //Default implementation
        System.out.println("callin single tap");
//        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = accChart;
        LineChart gyroscope = gyrChart;
        LineChart magnetometer = magChart;
        accelerometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Accelerometer";
                System.out.println("View obtained" + v);
                return false;
            }
        });
        gyroscope.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Gyroscope";
                return false;
            }
        });
        magnetometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Magnetometer";
                return false;
            }
        });
    }

    /**
     * Callbacks then a fling gesture is made on the chart.
     *
     * @param me1
     * @param me2
     * @param velocityX
     * @param velocityY
     */
    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        //Default implementation
        System.out.println("callin fling");
        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = anygraphView.findViewById(R.id.accelerometerChart);
        LineChart gyroscope = anygraphView.findViewById(R.id.gyroscopeChart);
        LineChart magnetometer = anygraphView.findViewById(R.id.magnetometerChart);
        accelerometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Accelerometer";
                System.out.println("View obtained" + v);
                return false;
            }
        });
        gyroscope.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Gyroscope";
                return false;
            }
        });
        magnetometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Magnetometer";
                return false;
            }
        });
    }

    /**
     * Callbacks when the chart is scaled / zoomed via pinch zoom gesture.
     *
     * @param me
     * @param scaleX scalefactor on the x-axis
     * @param scaleY scalefactor on the y-axis
     */
    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        millisecond1 = System.currentTimeMillis();
        View anygraphView = CustomGraphFragment.this.getView();
        try {
            LineChart accelerometer = accChart;
            LineChart gyroscope = gyrChart;
            LineChart magnetometer = magChart;
//            System.out.println(accelerometer.toString()+ " " + gyroscope.toString()+ " "
//            + magnetometer.toString() );
//            System.out.println("X-range" + gyroscope.getVisibleXRange() + "HIghest visible X" + gyroscope.getHighestVisibleX() + "Lowest visible X" + gyroscope.getLowestVisibleX());
//            chartTouchListenerAcc = new CustomChartTouchListener(accelerometer);
//            chartTouchListenerGyr = new CustomChartTouchListener(gyroscope);
//            chartTouchListenerMag = new CustomChartTouchListener(magnetometer);
            accelerometer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    lastPinched = "Accelerometer";
                    return false;
                }
            });
            gyroscope.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    lastPinched = "Gyroscope";
                    return false;
                }
            });
            magnetometer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    lastPinched = "Magnetometer";
                    return false;
                }
            });
            zoomX = scaleX;
            zoomY = scaleY;
            // System.out.println("zoomX="+zoomX+", zoomY="+zoomY+" motionevent="+me.getAction());
            millisecond2 = System.currentTimeMillis();
        } catch (NullPointerException ne) {
            System.out.println("All graphs missing, or no values");
        }
//        finally {
//            System.out.println(anygraphView.toString());
//            System.out.println("Three or nine axes plotted");
//        }
    }


    /**
     * Callbacks when the chart is moved / translated via drag gesture.
     *
     * @param me
     * @param dX translation distance on the x-axis
     * @param dY translation distance on the y-axis
     */
    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        System.out.println("Oncharttranslate cale");
//        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = accChart;
        LineChart gyroscope = gyrChart;
        LineChart magnetometer = magChart;
        translateX = dX;
//        System.out.println("TranslateX="+translateX+", Timestamp="+System.currentTimeMillis());
//        translateY = dY;
        accelerometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastDragged = "Accelerometer";
                //System.out.println("drag=dx" + dX + ", dY=" + dY);
                return false;
            }
        });
        gyroscope.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastDragged = "Gyroscope";
                return false;
            }
        });
        magnetometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastDragged = "Magnetometer";
                return false;
            }
        });

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

    public static void clear() {
        for (Map.Entry<String, TabMetaData> entry : SAVE_TAB_DATA.entrySet()
        ) {
            entry.getValue().clear();
        }
    }
}
