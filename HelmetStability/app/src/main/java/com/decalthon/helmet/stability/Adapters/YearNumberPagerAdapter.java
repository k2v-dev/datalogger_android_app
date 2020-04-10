package com.decalthon.helmet.stability.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.decalthon.helmet.stability.Fragments.YearlyCalendarFragment;

public class YearNumberPagerAdapter extends FragmentStateAdapter {
    private int sessionYearGap;
    private int earliestYear;

    public YearNumberPagerAdapter(@NonNull Fragment fragment, int sessionYearGap, int earliestYear) {
        super(fragment);
        this.sessionYearGap = sessionYearGap;
        this.earliestYear = earliestYear;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return YearlyCalendarFragment.newInstance((Integer.valueOf(earliestYear+position)).toString(),null);
    }

    @Override
    public int getItemCount() {
        return sessionYearGap;
    }
}
