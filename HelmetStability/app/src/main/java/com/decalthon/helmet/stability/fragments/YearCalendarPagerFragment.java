package com.decalthon.helmet.stability.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.adapters.YearCalendarPagerAdapter;
import com.decalthon.helmet.stability.utilities.CalendarUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YearCalendarPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearCalendarPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String clickedYear;
    private String yearRange;

    private int mYear;

    private Boolean [] enabledMonths = new Boolean[12];

    private YearCalendarPagerAdapter yearCalendarPagerAdapter;

    public YearCalendarPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YearCalendarPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YearCalendarPagerFragment newInstance(String param1, String param2) {
        YearCalendarPagerFragment fragment = new YearCalendarPagerFragment();
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
            clickedYear = getArguments().getString(ARG_PARAM1);
            yearRange = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_year_calendar_pager, container, false);
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

        TextView year_number_tv = view.findViewById(R.id.year_view_tv);
        year_number_tv.setText(clickedYear);

        Button leftNav = view.findViewById(R.id.left_year_pager);
        Button rightNav = view.findViewById(R.id.right_year_pager);

        Arrays.fill(enabledMonths, false);

        int yearCount = Integer.parseInt(yearRange) + 1;
        yearCalendarPagerAdapter = new YearCalendarPagerAdapter( this,yearCount , enabledMonths);

        int currentYear = Integer.parseInt(clickedYear);
        updateAdapter(currentYear);


        int [] yearNames = new int[yearCount];
        int year_i = 0;
        int latest_year = Calendar.getInstance().get(Calendar.YEAR);
        while(year_i < yearCount){
            yearNames[yearCount - year_i - 1] = latest_year - year_i;
            year_i++;
        }

        int position = yearCount - (latest_year - currentYear)  - 1;

        ViewPager2 viewPager = view.findViewById(R.id.year_calendar_pager);
        viewPager.setAdapter(yearCalendarPagerAdapter);

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(position,
                        true);
//                updateAdapter(currentYear);
//                yearCalendarPagerAdapter.notifyDataSetChanged();
            }
        }, 20);

        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if(currentItem >= 0) {
                    viewPager.setCurrentItem(currentItem - 1);
                }
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if(currentItem <= viewPager.getAdapter().getItemCount()){
                    viewPager.setCurrentItem(currentItem + 1);
                }
            }
        });

        viewPager.setUserInputEnabled(false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                registerPageLimits(position);
                year_number_tv.setText(String.valueOf(yearNames[position]));
                updateAdapter(yearNames[position]);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("VIEWPAGER", "onPageScrolled: Page scrolled " +position);
                registerPageLimits(position);
            }

        });
    }

    private void updateAdapter(int currentYear){
        Arrays.fill(enabledMonths,false);
        for(Map.Entry<Date, Integer> entry : CalendarUtils.dateMap.entrySet()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entry.getKey());
            if(calendar.get(Calendar.YEAR) == currentYear ){
                enabledMonths[entry.getValue() - 1] = true;
            }
        }
        yearCalendarPagerAdapter.setEnabledMonths(enabledMonths);
        yearCalendarPagerAdapter.setmYear(currentYear);
    }

    private void registerPageLimits(int position){
        if (position == 0) {
            this.getView().findViewById(R.id.left_year_pager).setVisibility(View.INVISIBLE);
        } else {
            this.getView().findViewById(R.id.left_year_pager).setVisibility(View.VISIBLE);
        }

        if (position == yearCalendarPagerAdapter.getItemCount() - 1) {
            this.getView().findViewById(R.id.right_year_pager).setVisibility(View.INVISIBLE);
        } else {
            this.getView().findViewById(R.id.right_year_pager).setVisibility(View.VISIBLE);
        }
    }
}
