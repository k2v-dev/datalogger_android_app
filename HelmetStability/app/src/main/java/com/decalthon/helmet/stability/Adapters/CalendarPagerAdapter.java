package com.decalthon.helmet.stability.Adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.decalthon.helmet.stability.Fragments.MonthlyCalendarFragment;
import com.decalthon.helmet.stability.Fragments.YearlyCalendarFragment;
import com.decalthon.helmet.stability.Utilities.CalendarUtils;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class CalendarPagerAdapter extends FragmentStateAdapter {
    private int session_range;
    private static String TAG = CalendarPagerAdapter.class.getSimpleName();
    private static String frag;


    public CalendarPagerAdapter(@NonNull Fragment fragment, String fragString, int session_range, List<Long> allSessionTimestamps) {
        super(fragment);
        frag = fragString;
        this.session_range = session_range;
        for(Long timestamp : allSessionTimestamps){
            Date date = (new Date(timestamp));
            String monthPosition = new SimpleDateFormat("MM",
                    Locale.getDefault()).format(date);
            CalendarUtils.dateMap.put(date,Integer.valueOf(monthPosition));
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(frag.startsWith("month")){
            return MonthlyCalendarFragment.newInstance(String.valueOf(session_range - position - 1),null);
        }
        if(frag.startsWith("year")){
            int year = Calendar.getInstance().get(Calendar.YEAR);
            return YearlyCalendarFragment.newInstance(String.valueOf(year),
                    null);
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ session_range);
        return session_range;
    }
}