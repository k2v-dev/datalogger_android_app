package com.decalthon.helmet.stability.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.decalthon.helmet.stability.Fragments.CustomGraphFragment;

import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE1_9_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE1_3_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE2_3_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_DEVICE2_9_AXIS;
import static com.decalthon.helmet.stability.Utilities.Constants.FRAGMENT_NAME_GPS_SPEED;


public class CustomPagerAdapter extends FragmentStateAdapter {

    public CustomPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        switch(position){
//            case 0:
//                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_9_AXIS);
//            case 1:
//                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_3_AXIS);
//            case 2:
//                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_9_AXIS);
//            case 3:
//                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_3_AXIS);
//            case 4:
//                return new CustomGraphFragment(FRAGMENT_NAME_GPS_SPEED);
//        }
        return new CustomGraphFragment();
    }

    @Override
    public int getItemCount() {
        System.out.println("Get count is called");
        return 6;
    }
}
