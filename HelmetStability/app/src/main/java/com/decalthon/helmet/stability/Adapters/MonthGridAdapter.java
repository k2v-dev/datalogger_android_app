package com.decalthon.helmet.stability.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.Fragments.CalendarPagerFragment;
import com.decalthon.helmet.stability.Fragments.MonthlyCalendarFragment;
import com.decalthon.helmet.stability.Fragments.YearlyCalendarFragment;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.CalendarUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MonthGridAdapter extends BaseAdapter {

    private Context mContext;
    private static String TAG = MonthGridAdapter.class.getSimpleName();

    private final String months[];

    public MonthGridAdapter(Context context, String [] months){
        this.mContext = context;
        this.months = months;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return 12;
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View parentView = (View) parent.getParent();
        TextView leftCaret = parentView.findViewById(R.id.previous_year_link_vp);
        TextView rightCaret = parentView.findViewById(R.id.next_year_link_vp);

        leftCaret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewPager2 parentPager = parentView.getRootView().findViewById(R.id.calendar_pager);
                int currentPosition = parentPager.getCurrentItem();
                parentPager.setCurrentItem(currentPosition - 1);
            }
        });

        rightCaret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager2 parentPager = parentView.getRootView().findViewById(R.id.calendar_pager);
                int currentPosition = parentPager.getCurrentItem();
                parentPager.setCurrentItem(currentPosition + 1);
            }
        });

        TextView monthTextView = new TextView(mContext);

        monthTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        monthTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        monthTextView.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        monthTextView.setPadding(64,80,64,80);
        monthTextView.setText(months[position]);
        monthTextView.setTextColor(Color.parseColor("#33000000"));
        for(Map.Entry<Date,Integer> entry: CalendarUtils.dateMap.entrySet()){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(entry.getKey());
            TextView currentYear =
                    parent.getRootView().findViewById(R.id.year_number_tv);
            if(String.valueOf(calendar.get(Calendar.YEAR)).equals(currentYear.getText().toString())){
                if( ( position + 1) == CalendarUtils.dateMap.get(entry.getKey())){
//                    monthTextView.setBackgroundColor(Color.YELLOW);
                    monthTextView.setTextColor(Color.BLACK);
                    monthTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment monthPager =
                                    CalendarPagerFragment.newInstance(MonthlyCalendarFragment.class.getSimpleName(),String.valueOf(position));
                            FragmentTransaction fragmentTransaction =
                                    MainActivity.shared().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.add(R.id.main_activity,
                                    monthPager, YearlyCalendarFragment.class.getSimpleName());
                            fragmentTransaction.addToBackStack(MonthGridAdapter.class.getSimpleName());
                            fragmentTransaction.commit();
                        }
                    });
                }
            }
        }
        return monthTextView;
    }
}

