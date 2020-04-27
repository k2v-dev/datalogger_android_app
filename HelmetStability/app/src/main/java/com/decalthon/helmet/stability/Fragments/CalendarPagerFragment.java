package com.decalthon.helmet.stability.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.Adapters.CalendarPagerAdapter;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ViewPager2 viewPager;
    private FragmentStateAdapter calendarPagerAdapter;
    private static Context mContext;

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
    public static CalendarPagerFragment newInstance(String param1, String param2) {
        CalendarPagerFragment fragment = new CalendarPagerFragment();
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

        Date earliestSession = new Date( allSessionTimestamps.get(0) );
        Date latestSession =
                new Date(allSessionTimestamps.get(allSessionTimestamps.size() - 1 ));

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
        final int session_range_month =
            (Math.abs( ( 12 * session_range_year )  - monthDiff));
        viewPager = view.findViewById(R.id.calendar_pager);

        if(mParam1.equals(MonthlyCalendarFragment.class.getSimpleName())){
            calendarPagerAdapter =
                    new CalendarPagerAdapter(this,getString(R.string.months),
                            session_range_month + 1,
                            allSessionTimestamps);
        }else if(mParam1.equals(YearlyCalendarFragment.class.getSimpleName())){
            calendarPagerAdapter =
                    new CalendarPagerAdapter(this,getString(R.string.years),
                            session_range_year + 1,allSessionTimestamps);
        }
        viewPager.setAdapter(calendarPagerAdapter);
        if(mParam2 == null) {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(calendarPagerAdapter.getItemCount(),
                            true);
                }
            }, 20);
        }else{
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(calendarPagerAdapter.getItemCount() - Integer.parseInt(mParam2)
                            , true);
                }
            }, 20);
        }
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

    private static class GetAllSessionSummaryDatesAsyncTask extends AsyncTask<Void,
            Void, List<Long>> {
        @Override
        protected List<Long> doInBackground(Void... voids) {
            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().getTimestampsFromSessionSummary();
        }
    }
}
