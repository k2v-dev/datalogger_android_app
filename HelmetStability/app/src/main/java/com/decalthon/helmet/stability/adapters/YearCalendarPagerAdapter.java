package com.decalthon.helmet.stability.adapters;

import com.decalthon.helmet.stability.fragments.YearCalendarPagerFragment;
import com.decalthon.helmet.stability.fragments.YearlyCalendarFragment;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class YearCalendarPagerAdapter extends FragmentStateAdapter {
    private int yearRange;
    private Boolean [] enabledMonths;
    private int [] enabled_positions = new int[12];
    private int mYear;



    public YearCalendarPagerAdapter(@NonNull Fragment fragment, int yearRange, Boolean[] enabledMonths) {
        super(fragment);
        this.yearRange = yearRange;
        this.enabledMonths = enabledMonths;
//
        Arrays.fill(enabled_positions, -1);
        setEnabledMonths(enabledMonths);
    }

    public void setEnabledMonths(Boolean[] enabledMonths) {
        this.enabledMonths = enabledMonths;
        Arrays.fill(enabled_positions, -1);
        for(int enabled_i = 0; enabled_i < enabledMonths.length; enabled_i++){
            if(enabledMonths[enabled_i]){
                enabled_positions[enabled_i] = enabled_i;
            }
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return null;
    }

    @Override
    public int getItemCount() {
        return yearRange;
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }
}
