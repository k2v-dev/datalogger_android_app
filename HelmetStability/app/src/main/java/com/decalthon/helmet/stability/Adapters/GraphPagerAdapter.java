package com.decalthon.helmet.stability.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.decalthon.helmet.stability.fragments.CustomGraphFragment;
import com.decalthon.helmet.stability.utilities.Constants;

import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE1_9_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE1_3_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE2_3_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_DEVICE2_9_AXIS;
import static com.decalthon.helmet.stability.utilities.Constants.FRAGMENT_NAME_GPS_SPEED;


public class GraphPagerAdapter extends FragmentStateAdapter {
    private final int NUM_TAB = 5;
    CustomGraphFragment[] customGraphFragments ;
    private long clicked_ts;
    private long start_ts;
    private long session_id;
    public GraphPagerAdapter(@NonNull FragmentActivity fragmentActivity, Long session_id, Long clicked_ts, Long start_ts ) {
        super(fragmentActivity);
        this.session_id = session_id;
        this.clicked_ts = clicked_ts;
        this.start_ts = start_ts;
        customGraphFragments = new CustomGraphFragment[NUM_TAB];
        for (int i = 0; i < 5; i++) {
            customGraphFragments[i] = null;
        }
    }

    @NonNull
    @Override
//    public Fragment createFragment(int position) {
//        if(customGraphFragments[position] == null && position < NUM_TAB){
//            switch(position){
//                case 0:
//                    customGraphFragments[position] = new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_9_AXIS, session_id, clicked_ts, start_ts);
//                case 1:
//                    customGraphFragments[position] = new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_3_AXIS, session_id, clicked_ts, start_ts);
//                case 2:
//                    customGraphFragments[position] =  new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_9_AXIS, session_id, clicked_ts, start_ts);
//                case 3:
//                    customGraphFragments[position] =  new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_3_AXIS, session_id, clicked_ts, start_ts);
//                case 4:
//                    customGraphFragments[position] =  new CustomGraphFragment(FRAGMENT_NAME_GPS_SPEED, session_id, clicked_ts, start_ts);
//            }
//        }else if(position >= NUM_TAB){
//            return new CustomGraphFragment();
//        }
//
//        return customGraphFragments[position];
//    }
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_9_AXIS, session_id, clicked_ts, start_ts);
            case 1:
                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE1_3_AXIS, session_id, clicked_ts, start_ts);
            case 2:
                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_9_AXIS, session_id, clicked_ts, start_ts);
            case 3:
                return new CustomGraphFragment(FRAGMENT_NAME_DEVICE2_3_AXIS, session_id, clicked_ts, start_ts);
            case 4:
                return new CustomGraphFragment(FRAGMENT_NAME_GPS_SPEED, session_id, clicked_ts, start_ts);
        }
        return new CustomGraphFragment();
    }

    @Override
    public int getItemCount() {
        System.out.println("Get count is called");
        return Constants.GRAPH_COUNT;
    }
}
