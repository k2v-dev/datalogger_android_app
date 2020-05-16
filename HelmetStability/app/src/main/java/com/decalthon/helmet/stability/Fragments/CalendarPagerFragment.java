package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.adapters.CalendarPagerAdapter;
import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int NUM_PAGES = 10;
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String calendarType;
    private int calendarValue;
    private int clickedYear;

    private OnFragmentInteractionListener mListener;
    private ViewPager2 viewPager;
    private FragmentStateAdapter calendarPagerAdapter;
    private Context mContext;
    private View leftNavigationView;
    private View rightNavigationView;

    public CalendarPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YearPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarPagerFragment newInstance(String param1, int param2, int mYearPassed) {
        CalendarPagerFragment fragment = new CalendarPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3,mYearPassed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            calendarType = getArguments().getString(ARG_PARAM1);
            calendarValue = getArguments().getInt(ARG_PARAM2);
            clickedYear = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_pager,
                container, false);
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

        List<Long> allSessionTimestamps = new ArrayList<>();
        try {
            allSessionTimestamps =
                    new GetAllSessionSummaryDatesAsyncTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Date earliestSession = new Date( allSessionTimestamps.get(allSessionTimestamps.size() - 1) );
        Date latestSession =
                new Date(allSessionTimestamps.get(0));

        String earliestYear =
                new SimpleDateFormat("YYYY", Locale.getDefault()).format(earliestSession);
        String earliestMonth =
                new SimpleDateFormat("MM", Locale.getDefault()).format(earliestSession);
        String earliestDay =
                new SimpleDateFormat("dd", Locale.getDefault()).format(earliestSession);
        String latestYear =
                new SimpleDateFormat("YYYY", Locale.getDefault()).format(latestSession);
        String latestMonth =
                new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String latestDay =
                new SimpleDateFormat("dd", Locale.getDefault()).format(latestSession);
        final int session_range_year =
                Integer.parseInt(latestYear) - Integer.parseInt(earliestYear);
//        int session_range_month =
//                Math.abs( ( session_range_year * 12 ) - Integer.parseInt(latestMonth) - Integer.parseInt(earliestMonth) );
        int monthDiff = Integer.parseInt(latestMonth) - Integer.parseInt(earliestMonth);

        int earliestMonthIndex = Integer.parseInt(earliestMonth);

        final int session_range_month =
            (Math.abs( ( 12 * session_range_year ) + monthDiff));
        viewPager = view.findViewById(R.id.calendar_pager);

        if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())){
            calendarPagerAdapter =
                    new CalendarPagerAdapter(this,getString(R.string.months),
                            session_range_month + 1,
                            allSessionTimestamps);
            viewPager.setAdapter(calendarPagerAdapter);
        }else if(calendarType.equals(YearlyCalendarFragment.class.getSimpleName())){

            Fragment yearlyCalendarFragment = YearlyCalendarFragment.newInstance(calendarValue,Integer.parseInt(earliestYear));
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(this.getId(),yearlyCalendarFragment,CalendarPagerFragment.class.getSimpleName());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
//            calendarPagerAdapter =
//                    new CalendarPagerAdapter(this,getString(R.string.years),
//                            session_range_year + 1,allSessionTimestamps);
        }

        if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())) {
            if (calendarValue == -1) {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(calendarPagerAdapter.getItemCount(),
                                true);
                    }
                }, 20);
            } else {
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int month_diff = (clickedYear - Integer.parseInt(earliestYear)) * 12
                                - (earliestMonthIndex - calendarValue);
                        viewPager.setCurrentItem(month_diff + 1, true);
                    }
                }, 20);
            }
        }


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                registerPageLimits(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("VIEWPAGER", "onPageScrolled: Page scrolled " +position);
                registerPageLimits(position);
            }
        });




//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                if (position == 0) {
//                    MainActivity.shared().findViewById(R.id.left_month_pager).setVisibility(View.INVISIBLE);
//                } else {
//                    MainActivity.shared().findViewById(R.id.left_month_pager).setVisibility(View.VISIBLE);
//                }
//            }
//        });

    }

    private void registerPageLimits(int position){
        if (position == 0) {
            if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())) {
                this.getView().findViewById(R.id.left_month_pager).setVisibility(View.INVISIBLE);

            }else if(calendarType.equals(YearlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.previous_year_link_vp).setVisibility(View.INVISIBLE);
            }
        } else {
            if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())) {
                this.getView().findViewById(R.id.left_month_pager).setVisibility(View.VISIBLE);
            }
            else if(calendarType.equals(YearlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.previous_year_link_vp).setVisibility(View.VISIBLE);
            }
        }

        if (position == calendarPagerAdapter.getItemCount() - 1) {
            if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.right_month_pager).setVisibility(View.INVISIBLE);
            }
            else if(calendarType.equals(YearlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.next_year_link_vp).setVisibility(View.INVISIBLE);
            }
        } else {
            if(calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.right_month_pager).setVisibility(View.VISIBLE);
            }
            else if(calendarType.equals(YearlyCalendarFragment.class.getSimpleName())){
                this.getView().findViewById(R.id.next_year_link_vp).setVisibility(View.VISIBLE);
            }
        }

        if( calendarType.equals(MonthlyCalendarFragment.class.getSimpleName())){

            leftNavigationView = this.getView().findViewById(R.id.left_month_pager);
            rightNavigationView = this.getView().findViewById(R.id.right_month_pager);

            leftNavigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                View viewroot = view.getRootView();
//                ViewPager2 viewPager2 =
//                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                    int currentPage =  viewPager.getCurrentItem();
                    viewPager.setCurrentItem( currentPage - 1 );
                }
            });

            rightNavigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                View viewroot = view.getRootView();
//                ViewPager2 viewPager2 =
//                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                    int currentPage =  viewPager.getCurrentItem();
                    viewPager.setCurrentItem( currentPage + 1 );
                }
            });
        }else{
            leftNavigationView = this.getView().findViewById(R.id.
                    previous_year_link_vp);
            rightNavigationView = this.getView().findViewById(R.id.next_year_link_vp);

            leftNavigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                View viewroot = view.getRootView();
//                ViewPager2 viewPager2 =
//                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                    int currentPage =  viewPager.getCurrentItem();
                    viewPager.setCurrentItem( currentPage - 1 );
                }
            });

            rightNavigationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                View viewroot = view.getRootView();
//                ViewPager2 viewPager2 =
//                        (ViewPager2) viewroot.findViewById(R.id.calendar_pager);
                    int currentPage =  viewPager.getCurrentItem();
                    viewPager.setCurrentItem( currentPage + 1 );
                }
            });
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public String getCalendarType(){
        return calendarType;
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

    private static class GetAllSessionSummaryDatesAsyncTask extends AsyncTask<Void,
            Void, List<Long>> {
        @Override
        protected List<Long> doInBackground(Void... voids) {
            return SessionCdlDb.getInstance().getSessionDataDAO().getTimestampsFromSessionSummary();
        }
    }
}
