package com.decalthon.helmet.stability.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.Activities.FullScreenActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.model.NineAxisModels.NineAxis;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.util.concurrent.ExecutionException;

import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE2_3_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE2_9_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_GPS_SPEED;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE1_9_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE1_3_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.GPS_SPEED_NAME;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.DRAG;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.PINCH_ZOOM;
import static com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture.SINGLE_TAP;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //By default, the map fragment is shown on the pager
    private String fragmentType = FRAGMENT_NAME_DEVICE1_9_AXIS;

    //Individual subclasses are used for each inertial element
    //TODO One nine-axis object, add members acc, gyr, mag

//    //TODO Write  a method to clear all entries
//    private NineAxis.Accelerometer accelerometer;
//    private NineAxis.Gyroscope gyroscope;
//    private NineAxis.Magnetometer magnetometer;

    //These zoom variables are defined for capturing for an experimental trial
    //TODO If zoom effect has to exhibited comm to all graphs in the fragment, use these variables
    private float zoomX;
    private float zoomY;

    //TODO If the zoom effect has to be common to all graphs in the fragment, use lastPinched
    private String lastPinched;
    private String lastDragged;

    private float translateX;
    private float translateY;

    /* Fling events are treated as drag. Owing to a lack of  difference between drag and fling, a
    alternatives are being sought,for rendering fling and drag as separate events, coupled with
    regular X-Y translation changes
     */
    private String lastFlung;


    public CustomGraphFragment(){
        //Required empty public constructor
    }


    /*When any concrete CustomGraphFragment class is instantiated, this
    * constructor provides the required fragment type and corresponding view*/
    public CustomGraphFragment(final String fragmentType) {
        this.fragmentType = fragmentType;
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
     * @param inflater The callback argument which inflates a {@link CustomGraphFragment}
     * @param container The parent container which holds the graph fragment
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

        if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE1_9_AXIS)){
            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE1_3_AXIS)){
            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE2_9_AXIS)){
            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
        }else if(fragmentType.equalsIgnoreCase(FRAGMENT_NAME_DEVICE2_3_AXIS)){
            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
        }else if(fragmentType.equalsIgnoreCase(GPS_SPEED_NAME)){
            return inflater.inflate(R.layout.fragment_plot_graph,container,false);
        }

        return inflater.inflate(R.layout.fragment_plot_graph,container,false);

    }

    /**
     * Once the graph view is created, corresponding charts are displayed with session data.
     *
     * @param view The rectangular area of the screen under the actionbar that displays the graph.
     *
     * @param savedInstanceState Currently used only in the superclass context
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LineChart accLineChartView = view.findViewById(R.id.accelerometerChart);
        final LineChart gyrLineChartView = view.findViewById(R.id.gyroscopeChart);
        final LineChart magLineChartView = view.findViewById(R.id.magnetometerChart);

        switch(fragmentType){

            //Views defined for nine-axis data. Scrollable if rendered outside viewport bounds
            case FRAGMENT_NAME_DEVICE1_9_AXIS:

//                accelerometer = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Accelerometer();
//                gyroscope = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Gyroscope();
//                magnetometer = new NineAxis(CustomGraphFragment.this.getContext(),session_id).new Magnetometer();

                //TODO On destroy , clear all entries

                //Currently random data is generated in {@link CustomGraphFragment#getAxisEntries}
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                NineAxis.getInstance(CustomGraphFragment.this.getContext())
                                        .drawGraph(getString(R.string.acceleration), accLineChartView);
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NineAxis.getInstance(CustomGraphFragment.this.getContext())
                                    .drawGraph(getString(R.string.gyroscope), gyrLineChartView);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            NineAxis.getInstance(CustomGraphFragment.this.getContext())
                                    .drawGraph(getString(R.string.magnetometer), magLineChartView);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
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
                nineAxisGraphRefresh(accLineChartView,gyrLineChartView,magLineChartView);

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


                accLineChartView.getDescription().setPosition(width/2,layoutParams.height-100);
                gyrLineChartView.getDescription().setPosition(width/2,layoutParams.height-100);
                magLineChartView.getDescription().setPosition(width/2,layoutParams.height-100);

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
                                registerFullScreen(getString(R.string.acceleration),
                                        getString(R.string.device1_tv));
                            }
                        });
                //The gyroscope fullscreen handle
                ivGyr.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                registerFullScreen( getString(R.string.gyroscope),
                                        getString(R.string.device1_tv));
                            }
                        });
                //The magnetometer fullscreen handle
                ivMag.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                registerFullScreen(getString(R.string.magnetometer),
                                        getString(R.string.device1_tv));
                            }
                        });
                accLineChartView.setOnChartGestureListener(this);
                gyrLineChartView.setOnChartGestureListener(this);
                magLineChartView.setOnChartGestureListener(this);
                break;

            case FRAGMENT_NAME_DEVICE1_3_AXIS:
                long session_id1 = 1;
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
//                                registerFullScreen(ivAccT, threeAxisChartView);
                            }
                        });

                break;

        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
    }

    /**
     *
     * @param anyLineChart Placeholder for any linechart passed as an argument
     * @param anyLineData Placeholder for linedata passed as an argument
     */
//    private void setDataNineAxis(LineChart anyLineChart, LineData anyLineData) {
//        anyLineChart.setData(anyLineData);
//    }

    /**
     * Nine axis session data with readings based on a format
     * @param accelerometer The accelerometer POJO
     * @param gyroscope The gyroscope POJO
     * @param magnetometer The magnetometer POJO
     */
//    private void getAxisEntries(NineAxis.Accelerometer accelerometer, NineAxis.Gyroscope gyroscope,
//                                NineAxis.Magnetometer magnetometer) throws ExecutionException, InterruptedException {
//        accelerometer.getReadingsAcc();
//        gyroscope.getReadingsGyr();
//        magnetometer.getReadingsMag();
//    }

    /**
     * Static rendering of all chart data, frequency plotted up to
     * {@link com.decalthon.helmet.stability.Utilities.Constants#DATA_LIST_SIZE}
     *
     *
     *
     * @param accelerometer The accelerometer POJO
     * @param gyroscope The gyroscope POJO
     * @param magnetometer The magnetometer POJO
     */

//    private void staticUpdateAll(NineAxis.Accelerometer accelerometer, NineAxis.Gyroscope gyroscope, NineAxis.Magnetometer magnetometer) throws ExecutionException, InterruptedException {
//        accelerometer.simulateStaticMilliSecondUpdate();
//        gyroscope.simulateStaticMilliSecondUpdate();
//        magnetometer.simulateStaticMilliSecondUpdate();
//    }

    /**
     * Prepare the line chart views for with lineData. See calculations base on
     * {@link NineAxis.Accelerometer#displayStaticMilliSecondUpdate()}
     *
     * @param accelerometer The accelerometer POJO
     * @param gyroscope The gyroscope POJO
     * @param magnetometer The magnetometer POJO
     */
//    private void timeUpdateCharts(NineAxis.Accelerometer accelerometer, NineAxis.Gyroscope gyroscope, NineAxis.Magnetometer magnetometer) {
//        accelerometer.displayStaticMilliSecondUpdate();
//        gyroscope.displayStaticMilliSecondUpdate();
//        magnetometer.displayStaticMilliSecondUpdate();
//    }

    /**
     * Configure line charts with similar formatting and display attributes
     * Overloaded to accomodate upto 3 types of LineCharts
     *
     * @param lineChartAxisType1 First chart of three possible motion variables
     * @param lineChartAxisType2 Second chart of motion variable, as needed, optional
     * @param lineChartAxisType3 Third chart of motion variable, as needed, optional
     */
//    private void prepareChart(LineChart lineChartAxisType1 ,
//                              LineChart lineChartAxisType2,
//                              LineChart lineChartAxisType3) {
//        prepareChart(lineChartAxisType1);
//        prepareChart(lineChartAxisType2);
//        prepareChart(lineChartAxisType3);
//    }

    /**
     * Overloaded method, for configuring each linechart type.
     * @param anyLineChart Used for single-graph specific modification.
     *                     Future extensions can include initialization objects
     */
//    private void prepareChart(LineChart anyLineChart) {
//
//        /*Chart specific settings*/
//        anyLineChart.setNoDataText("No data at the moment");
//        anyLineChart.setDragEnabled(true);
//        anyLineChart.setDrawGridBackground(false);
//        anyLineChart.setPinchZoom(true);
//        anyLineChart.getLegend().setEnabled(true);
//        anyLineChart.getXAxis().setDrawGridLines(true);
//        anyLineChart.getXAxis().setDrawAxisLine(true);
//
//        /*Legend-specific settings*/
//        prepareLegend(anyLineChart);
//
//        /*Axis specific settings*/
//        XAxis xAxis = anyLineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        YAxis yAxisRight = anyLineChart.getAxisRight();
//        yAxisRight.setEnabled(false);
//
//        /*Data representation settings*/
//        LineData lineData = new LineData();
//        lineData.setValueTextColor(Color.BLACK);
//        anyLineChart.setData(lineData);
//
//        /*Default 1x zoom rendered for every iteration. loop used instead of default 2x zoom
//        * for increase in zoom level
//        * NOTE: Fling actions are not recognized at any zoom level above 1*/
//        for(int i = 0; i < 7; i++) {
//            anyLineChart.zoom(i,0,0,0, YAxis.AxisDependency.LEFT);
//        }
////        anyLineChart.zoom(7,0,0,0,YAxis.AxisDependency.LEFT);
//
//        /*The invalidate() library method refreshes the chart and
//         * graph contents*/
//        anyLineChart.invalidate();
//    }


    /**
     * Invalidates any existing chart data and replaces with a fresh plot graph.
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

//
//    /**
//     * For each line chart, X-axis is configured as time and Y-axis values are color-coded
//     * @param anyLineChart The chart that needs the standardized legend format
//     */
//    private void prepareLegend(LineChart anyLineChart) {
//        Legend legend = anyLineChart.getLegend();
//        legend.setForm(Legend.LegendForm.CIRCLE);
//        anyLineChart.getDescription().setText("TIME(s)");
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//    }

    private void registerFullScreen(final String graph_type, final String device_id) {

//        Fragment fullScreenFragment = FullScreenFragment.newInstance("Accelerometer","GRAPH1");
//        FragmentManager fragmentManager = getChildFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.addToBackStack("Checking fullscreen");
        Intent in = new Intent(getActivity(), FullScreenActivity.class);
        in.putExtra("GRAPH_TYPE", graph_type);
        in.putExtra("DEVICE_ID",device_id);
        in.putExtra("DATA_SIZE", 1);
        startActivity(in);
//
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Callbacks when a touch-gesture has started on the chart (ACTION_DOWN)
     *
     * @param me The motion event type
     * @param lastPerformedGesture The gesture that invoked the "start" callback
     */
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Default implementation

    }



    //=====CODE UNDER DEVELOPMENT==========//
    private long millisecond1,millisecond2;


    /**
     * Callbacks when a touch-gesture has ended on the chart (ACTION_UP, ACTION_CANCEL)
     *
     * @param me The motion event type
     * @param lastPerformedGesture The gesture that invoked the "end" callback
     */
    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        //Default implementation
        millisecond2 = System.currentTimeMillis();
        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = anygraphView.findViewById(R.id.accelerometerChart);
        LineChart gyroscope = anygraphView.findViewById(R.id.gyroscopeChart);
        LineChart magnetometer = anygraphView.findViewById(R.id.magnetometerChart);

        long diff = (millisecond2 - millisecond1);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (lastPerformedGesture == DRAG && lastDragged.equals("Accelerometer")
                            || (lastPerformedGesture == SINGLE_TAP && lastFlung.equals("Accelerometer"))) {
                        gyroscope.moveViewToX(accelerometer.getLowestVisibleX());
                        magnetometer.moveViewToX(accelerometer.getLowestVisibleX());
                    }

                    if (lastPerformedGesture == DRAG && lastDragged.equals("Gyroscope")
                            || (lastPerformedGesture == SINGLE_TAP && lastFlung.equals("Gyroscope"))) {
                        accelerometer.moveViewToX(gyroscope.getLowestVisibleX());
                        magnetometer.moveViewToX(gyroscope.getLowestVisibleX());
                    }

                    if (lastPerformedGesture == DRAG && lastDragged.equals("Magnetometer")
                            || (lastPerformedGesture == SINGLE_TAP && lastFlung.equals("Magnetometer"))) {
                        gyroscope.moveViewToX(magnetometer.getLowestVisibleX());
                        accelerometer.moveViewToX(magnetometer.getLowestVisibleX());
                    }
                }
                catch (NullPointerException e){
                    System.out.println("Waiting for init");
                }

            }
        });

        if(lastPerformedGesture == PINCH_ZOOM && lastPinched.equals("Accelerometer")) {
            gyroscope.zoom(zoomX,zoomY,gyroscope.getWidth()/2,gyroscope.getHeight()/2);
            magnetometer.zoom(zoomX,zoomY,magnetometer.getWidth()/2,magnetometer.getHeight()/2);
        }

        if(lastPerformedGesture == PINCH_ZOOM && lastPinched.equals("Gyroscope")) {
            accelerometer.zoom(zoomX,zoomY,accelerometer.getWidth()/2,accelerometer.getHeight()/2);
            magnetometer.zoom(zoomX,zoomY,magnetometer.getWidth()/2,magnetometer.getHeight()/2);
        }

        if(lastPerformedGesture == PINCH_ZOOM && lastPinched.equals("Magnetometer")) {
            gyroscope.zoom(zoomX,zoomY,gyroscope.getWidth()/2,gyroscope.getHeight()/2);
            accelerometer.zoom(zoomX,zoomY,accelerometer.getWidth()/2,accelerometer.getHeight()/2);
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

        System.out.println("Gesture has ended"+lastPerformedGesture);

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
        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = anygraphView.findViewById(R.id.accelerometerChart);
        LineChart gyroscope = anygraphView.findViewById(R.id.gyroscopeChart);
        LineChart magnetometer = anygraphView.findViewById(R.id.magnetometerChart);
        accelerometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastFlung = "Accelerometer";
                System.out.println("View obtained"+v);
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
                System.out.println("View obtained"+v);
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
        try{
            LineChart accelerometer = anygraphView.findViewById(R.id.accelerometerChart);
            LineChart gyroscope = anygraphView.findViewById(R.id.gyroscopeChart);
            LineChart magnetometer = anygraphView.findViewById(R.id.magnetometerChart);
//            System.out.println(accelerometer.toString()+ " " + gyroscope.toString()+ " "
//            + magnetometer.toString() );
            System.out.println("X-range"+gyroscope.getVisibleXRange()+"HIghest visible X"+gyroscope.getHighestVisibleX()+"Lowest visible X"+gyroscope.getLowestVisibleX());
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
            millisecond2 = System.currentTimeMillis();
        }
        catch (NullPointerException ne){
            System.out.println("All graphs missing, or no values");
        }
        finally{
            System.out.println(anygraphView.toString());
            System.out.println("Three or nine axes plotted");
        }
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
        System.out.println("Oncharttranslate cale");
        View anygraphView = CustomGraphFragment.this.getView();
        LineChart accelerometer = anygraphView.findViewById(R.id.accelerometerChart);
        LineChart gyroscope = anygraphView.findViewById(R.id.gyroscopeChart);
        LineChart magnetometer = anygraphView.findViewById(R.id.magnetometerChart);
        translateX = dX;
//        System.out.println(translateX+"TranslateX");
        translateY = dY;
        accelerometer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastDragged = "Accelerometer";
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
}
