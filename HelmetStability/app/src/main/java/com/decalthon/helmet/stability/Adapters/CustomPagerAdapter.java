package com.decalthon.helmet.stability.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.decalthon.helmet.stability.Fragments.CustomGraphFragment;

import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_CHART1;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_CHART2;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_GPS_SPEED;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_NINE_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_STEP_COUNT;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_THREE_AXIS;


public class CustomPagerAdapter extends FragmentStateAdapter {

    public CustomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                System.out.println("Able to fetch an empty item @ position" + position);
                return new CustomGraphFragment(FRAGMENT_NAME_NINE_AXIS);
            case 1:
                System.out.println("Able to fetch an empty item @ position" + position);
                return new CustomGraphFragment(FRAGMENT_NAME_THREE_AXIS);
            case 2:
                System.out.println("Able to fetch an empty item @ position" + position);
                return new CustomGraphFragment(FRAGMENT_NAME_GPS_SPEED);
            case 3:
                return new CustomGraphFragment(FRAGMENT_NAME_STEP_COUNT);
            case 4:
                return new CustomGraphFragment(FRAGMENT_NAME_CHART1);
            case 5:
                return new CustomGraphFragment(FRAGMENT_NAME_CHART2);
        }
        return new CustomGraphFragment();
    }

    @Override
    public int getItemCount() {
        System.out.println("Get count is called");
        return 6;
    }
}
