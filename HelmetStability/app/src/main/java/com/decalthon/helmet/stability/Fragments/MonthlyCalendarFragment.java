package com.decalthon.helmet.stability.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.Adapters.CalendarGridAdapter;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.CalendarUtils;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MonthlyCalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MonthlyCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyCalendarFragment extends Fragment{

    public static final String RESULT = "result";
    public static final String EVENT = "event";
    private static final int ADD_NOTE = 44;
    private static final String TAG = "Calendar Fragment";

    private static boolean swipeDisableFlag = true;

    private String mParam1;
    private String mParam2;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CalendarView mCalendarView;
//    private List<EventDay> mEventDays = new ArrayList<>();

    private List<Integer> mEventDays = new ArrayList<>();

    private Date earliestDate = null;
    private Date latestDate = null;

    private OnFragmentInteractionListener mListener;
    private static Context mContext;

    public MonthlyCalendarFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MonthlyCalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyCalendarFragment newInstance(String param1, String param2) {
        MonthlyCalendarFragment fragment = new MonthlyCalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
//        mCalendarView = view.findViewById(R.id.month_calendar_view);
//        registerMonthClickListener();
//        mCalendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
//            @Override
//            public void onChange() {
//                registerMonthClickListener();
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getContext(), "onForwardChange", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                System.out.println("onForwardChange");
//            }
//        });
//        mCalendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
//            @Override
//            public void onChange() {

//                    if(!swipeDisableFlag){
//                        mCalendarView.setSwipeEnabled(false);
//                        return;
//                    }
////                if(mCalendarView.getSelectedDates() != null){
//                    if(earliestDate.equals(mCalendarView.getFirstSelectedDate().getTime())){
//                        swipeDisableFlag = false;
//                    }

//                    registerMonthClickListener();
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "onPreviousChange", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    System.out.println("onPreviousChange");
//            }
//        });


//        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                addNote();
//            }
//        });
        List<Calendar> calendars = new ArrayList<>();
        List<EventDay> events = new ArrayList<>();

        //Calendar calendar1 = Calendar.getInstance();
        //events.add(new EventDay(calendar1, DrawableUtils.getThreeDots(getContext())));
//or
//        events.add(new EventDay(calendar, new Drawable()));
//or if you want to specify event label color
        /*Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        Calendar calendar4 = Calendar.getInstance();
        Calendar calendar5 = Calendar.getInstance();
        Calendar calendar6 = Calendar.getInstance();
        Calendar calendar7 = Calendar.getInstance();
        Calendar calendar8 = Calendar.getInstance();*/

        //1. Create a Date from String
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

//        String dateInString = "2020-02-01";
//        String dateInString1 = "2020-12-01";
//        String dateInString2 = "2020-11-10";
//        String dateInString3 = "2020-09-01";
//        String dateInString4 = "2020-09-30";


//        String dateInString = "2020-09-01";
//        String dateInString1 = "2020-09-01";
//        String dateInString2 = "2020-09-10";
//        String dateInString3 = "2020-09-01";
//        String dateInString4 = "2020-09-30";

//        String  dateInString = "2020-01-05";
//        String dateInString1 ="2020-01-10";
//        String dateInString2 = "2020-02-25";
//        String dateInString3 =  "2020-02-01";
//        String dateInString4 = "2020-01-08";
//        String dateInString5 = "2020-03-03";
//        String dateInString6 = "2020-01-26";





        Date date = null, date1 = null, date2=null, date3 = null,date4 = null ,date5 = null,
                date6 =null;
//        try {
//            date = sdf.parse(dateInString);
//            date1 = sdf.parse(dateInString1);
//            date2 = sdf.parse(dateInString2);
//            date3 = sdf.parse(dateInString3);

//            date = Constants.dateFormat.parse(dateInString);
//            date1 = Constants.dateFormat.parse(dateInString1);
//            date2 = Constants.dateFormat.parse(dateInString2);
//            date3 = Constants.dateFormat.parse(dateInString3);
//            date4 = Constants.dateFormat.parse(dateInString4);
//            date5 = Constants.dateFormat.parse(dateInString5);
//            date6 = Constants.dateFormat.parse(dateInString6);

//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        calendar2.setTime(date);
//        calendar3.setTime(date1);
//        calendar4.setTime(date2);
//        calendar5.setTime(date3);
//        calendar6.setTime(date4);
//        calendar7.setTime(date5);
//        calendar8.setTime(date6);
//

//        events.add(new EventDay(calendar2, DrawableUtils.getTwoDots(getContext())));
//        events.add(new EventDay(calendar3, DrawableUtils.getOneDot(getContext())));
//        events.add(new EventDay(calendar4, DrawableUtils.getSixDots(getContext())));

        // calendars.add(calendar1);
//        calendars.add(calendar2);
//        calendars.add(calendar3);
//        calendars.add(calendar4);
//        calendars.add(calendar5);
//        calendars.add(calendar6);
//        calendars.add(calendar7);
//        calendars.add(calendar8);

//        mCalendarView.setEvents(events);
//        mCalendarView.setSelectedDates(calendars);

//        List<Calendar> sessionDayList = mCalendarView.getSelectedDates();

//        ArrayList<Calendar> sortedCalendar = new ArrayList<>(calendars);
//        Collections.sort(sortedCalendar);

//        try {
//            earliestDate = getEarliestSessionDate(sortedCalendar).getTime();
////            latestDate = getMostRecentSessionDate(sortedCalendar).getTime();
//            Calendar yesterday = Calendar.getInstance();
//            yesterday.add(Calendar.DATE,-1);
//            latestDate = yesterday.getTime();
////            mCalendarView.setMinimumDate(getEarliestSessionDate(sortedCalendar));
//            Calendar monthEnd = Calendar.getInstance();
//            int lastDate = monthEnd.getActualMaximum(Calendar.DATE);
//            monthEnd.set(Calendar.DATE,lastDate);
//            mCalendarView.setMaximumDate(monthEnd);

//        } catch (Exception e) {
//            System.out.println("Correct the date format before passing it to getMostRecentSessionDate or etc");
//        }

        ArrayList<View> arrayList = new ArrayList<>();

//        Calendar monthToBeDisplayed = Calendar.getInstance();

//        monthToBeDisplayed.set(2020,Integer.parseInt(0),1);

//        String monthName = monthToBeDisplayed.getDisplayName(
//                Calendar.MONTH,
//                Calendar.LONG,
//                Locale.getDefault());
//        System.out.println(monthName + "monthName");
//
//        mCalendarView.findViewsWithText(arrayList,monthName
//                ,-1);
//        try {
//            mCalendarView.setDate(monthToBeDisplayed);
//        } catch (OutOfDateRangeException e) {
//            System.out.println("Check the date, this date cannot be displayed");
//        }
//        for(View view1 : arrayList){
//            view1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                        v.setBackgroundColor(Color.YELLOW);
//                    System.out.println("I am peekabooing");
//                    Fragment yearlyFragment = YearPagerFragment.newInstance(null,null);
//                    FragmentTransaction fragmentTransaction =
//                            getFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(MonthlyCalendarFragment.this.getId(),
//                            yearlyFragment,"Yearly Calendar Fragment");
//                    fragmentTransaction.commit();
//                }
//            });
//        }

//        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
//            @Override
//            public void onDayClick(EventDay eventDay) {
//                //previewNote(eventDay);
//                if(eventDay.isEnabled()) {
//                    String str = eventDay.toString();
//                    Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
//                    Fragment sevenSessionSummaryFragment = SevenSessionsSummaryFragment
//                            .newInstance(null, null);
//                    FragmentManager fragmentManager = getFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.add(MonthlyCalendarFragment.this.getId(), sevenSessionSummaryFragment);
//                    fragmentTransaction.commit();
//                }
//            }
//        });

//        List<Calendar> disabledDates = getDisabledDates(sortedCalendar,earliestDate,latestDate);
//        for(Calendar calendar : disabledDates){
//            System.out.println(calendar.getTime() + " check of timestamp");
//        }
//        mCalendarView.setDisabledDays(disabledDates);

//        if(isEarliestDateReached()){
//            //Disabling swiping and page movement
//            mCalendarView.setSwipeEnabled(false);
//            mCalendarView.setOnPreviousPageChangeListener(null);
//            mCalendarView.setOnForwardPageChangeListener(null);
//        }

//        ActionBar mActionBar =
//                ( (MainActivity) getActivity() ).getSupportActionBar();
//        View actionBarView = mActionBar.getCustomView();
//        actionBarView.findViewById(R.id.back_link).setVisibility(View.VISIBLE);
//        TextView titleText = actionBarView.findViewById(R.id.title_text);
//        titleText.setText(R.string.calendar_fragment_title_text);
//        titleText.setVisibility(View.VISIBLE);

//        actionBarView.findViewById(R.id.gps_session_start_btn).setVisibility(View.GONE);
//        actionBarView.findViewById(R.id.profile_link).setVisibility(View.GONE);
//        actionBarView.findViewById(R.id.ble_device_connectivity).setVisibility(View.GONE);
//        actionBarView.findViewById(R.id.logout_link).setVisibility(View.GONE);

//        CircleImageView backButton = actionBarView.findViewById(R.id.back_link);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.shared().onBackPressed();
//            }
//        });
//
        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView monthView = view.findViewById(R.id.days_grid_view);
        TextView monthTextView = view.findViewById(R.id.month_view_tv);

        TextView leftCaretView =
                view.findViewById(R.id.left_month_pager);
        TextView rightCaretView =
                view.findViewById(R.id.right_month_pager);

        leftCaretView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewroot = view.getRootView();
                ViewPager2 viewPager2 =
                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                int currentPage =  viewPager2.getCurrentItem();
                viewPager2.setCurrentItem( currentPage - 1 );
            }
        });

        rightCaretView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewroot = view.getRootView();
                ViewPager2 viewPager2 =
                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                int currentPage =  viewPager2.getCurrentItem();
                viewPager2.setCurrentItem( currentPage + 1 );

                Log.d(TAG, "onClick: Right caret press");
            }
        });

        Calendar displayedMonth = Calendar.getInstance();
//        displayedMonth.set(Calendar.MONTH,
//                displayedMonth.get(Calendar.MONTH - Integer.parseInt(mParam1)));
        displayedMonth.add(Calendar.MONTH , -(Integer.parseInt(mParam1)) );
        String monthName = displayedMonth.getDisplayName(Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault());
        monthTextView.setText(monthName);

        registerMonthDoubleTapListener(monthTextView);

        int maxDays = displayedMonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar dayOne = displayedMonth;
        dayOne.set(Calendar.DATE,1);

        int startingDay = dayOne.get(Calendar.DAY_OF_WEEK) - 1;

        int currentMonthNum =  displayedMonth.get(Calendar.MONTH) + 1;

        String[] precedingDaysCells = new String[startingDay];
        String[] currentMonthDayCells = new String[maxDays];

        for(int prec_i = 0; prec_i < startingDay - 1 ; prec_i++ ){
            precedingDaysCells[prec_i] = "";
        }

        for(int day_i = 0 ; day_i < maxDays; day_i++ ){
            currentMonthDayCells[day_i] = "" + (day_i + 1);
        }

        for(Map.Entry<Date,Integer> entry : CalendarUtils.dateMap.entrySet()){
            Date date = entry.getKey();
            String month_num =
                    new SimpleDateFormat("MM",Locale.getDefault()).format(date);
            String day_num =
                    new SimpleDateFormat("dd",Locale.getDefault()).format(date);
            if(Integer.parseInt(month_num) == currentMonthNum){
                mEventDays.add(Integer.parseInt(day_num));
            }
        }

        ArrayList<String> gridCells =
                new ArrayList<>(Arrays.asList(CalendarUtils.dayNameCells));
        gridCells.addAll(Arrays.asList(precedingDaysCells));
        gridCells.addAll(Arrays.asList(currentMonthDayCells));

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(),7);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        monthView.setLayoutManager(gridLayoutManager);

        CalendarGridAdapter calendarGridAdapter =
                new CalendarGridAdapter(gridCells,
                        (ArrayList<Integer>) mEventDays,
                        displayedMonth.get(displayedMonth.get(Calendar.MONTH)));
        monthView.setAdapter(calendarGridAdapter);
    }

    private void registerMonthDoubleTapListener(TextView monthTextView) {
        monthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment yearPager =
                        CalendarPagerFragment.newInstance
                                (YearlyCalendarFragment.class.getSimpleName(),null);
                FragmentTransaction fragmentTransaction =
                        ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(MonthlyCalendarFragment.this.getParentFragment().getId(),
                        yearPager,
                        "Yearly" +
                                " Calendar Fragment");
                fragmentTransaction.addToBackStack(CalendarPagerFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });
//        monthTextView.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector =
//                    new GestureDetector(MainActivity.shared(),
//                            new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//                    Fragment fragment =
//                            CalendarPagerFragment.newInstance(YearlyCalendarFragment.class.getSimpleName(), null);
//                    FragmentTransaction fragmentTransaction =
//                            getFragmentManager().beginTransaction();
//                    fragmentTransaction.add(R.id.main_activity, fragment,
//                            "Yearly Calendar Fragment");
//                    fragmentTransaction.commit();
//                    return super.onDoubleTap(e);
//                }
//                @Override
//                public boolean onSingleTapConfirmed(MotionEvent event) {
//                    Log.d("onSingleTapConfirmed", "onSingleTap");
//                    return false;
//                }
//            });
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });


    }

    private void getCurrentCalendarInfo() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();
    }

    private void registerMonthClickListener() {

        ArrayList<View> arrayList = new ArrayList<>();

        Calendar monthToBeDisplayed = Calendar.getInstance();

        monthToBeDisplayed.set(Integer.parseInt(mParam2),Integer.parseInt(mParam1),1);

        String monthName = monthToBeDisplayed.getDisplayName(
                Calendar.MONTH,
                Calendar.LONG,
                Locale.getDefault());

//        mCalendarView.findViewsWithText(arrayList,monthName
//                ,-1);
//        try {
//            mCalendarView.setDate(monthToBeDisplayed);
//        } catch (OutOfDateRangeException e) {
//            System.out.println("Check the date, this date cannot be displayed");
//        }
        for(View view1 : arrayList){
            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        v.setBackgroundColor(Color.YELLOW);
//                    Fragment yearlyFragment = YearPagerFragment.newInstance(null,null);
//                    FragmentTransaction fragmentTransaction =
//                            getFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(MonthlyCalendarFragment.this.getId(),
//                            yearlyFragment,"Yearly Calendar Fragment");
//                    fragmentTransaction.commit();
                }
            });
        }
    }

//    private boolean isEarliestDateReached(){
//        if((mCalendarView.getFirstSelectedDate().getTime()).equals(earliestDate)){
//            return true;
//        }
//        return false;
//    }

    private Calendar getEarliestSessionDate(ArrayList<Calendar> calendarArrayList) {
        return calendarArrayList.get(0);
    }

    private Calendar getMostRecentSessionDate(ArrayList<Calendar> calendarArrayList) {
        return calendarArrayList.get(calendarArrayList.size() - 1);
    }

    private static List<Calendar> getDisabledDates(ArrayList<Calendar> enabledDates, Date dateStart, Date dateEnd) {
        ArrayList<Calendar> disabledCalendarDates = new ArrayList<>();

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        System.out.println("calendar");
        try {
            cal1.setTime(dateStart);

            cal2.setTime(dateEnd);


        } catch (NullPointerException e) {
            System.out.println("Check cal1 and check date1 for NPE");
        } catch (Exception e) {
            System.out.println("Check any other exception other than NPE");
        }

        int checkIndex = 0;

        while ((!cal1.after(cal2)) ) {
            if(!(cal1.getTime()).equals(enabledDates.get(checkIndex).getTime())) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(cal1.getTime());
                disabledCalendarDates.add(calendar);
            }else{

                while(enabledDates.get(checkIndex).equals(cal1)){
                    checkIndex = (checkIndex + 1) % enabledDates.size();
                }
            }
            cal1.add(Calendar.DATE, 1);
        }
        return disabledCalendarDates;
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
