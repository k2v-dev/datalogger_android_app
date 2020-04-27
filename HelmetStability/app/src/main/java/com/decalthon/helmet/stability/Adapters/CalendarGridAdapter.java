package com.decalthon.helmet.stability.Adapters;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.Fragments.MapFragment;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.CalendarUtils;
import com.decalthon.helmet.stability.Utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CalendarGridAdapter extends RecyclerView.Adapter<CalendarGridAdapter.CalendarViewHolder>{

    private ArrayList<String> mCalendarDataset;
    private Integer []  mEventDataset;
    private static String TAG = CalendarGridAdapter.class.getSimpleName();

    public static class CalendarViewHolder extends RecyclerView.ViewHolder{
        public View cellView;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            cellView = itemView;
        }
    }

    public CalendarGridAdapter(ArrayList<String> calendarDataset,
                               ArrayList<Integer> events) {
        mCalendarDataset = calendarDataset;
        mEventDataset = events.toArray(new Integer[0]);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cellView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_cell,parent,false);
        return new CalendarViewHolder(cellView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        int dotCounter = 0;
        char [] dots = new char[7];
        String dotString = null;
        TextView cellTextView = holder.cellView.findViewById(R.id.calendar_cell_tv);
        if(position < 7) {
            cellTextView.setTextColor(Color.parseColor("#FF000000"));
        }else{
            cellTextView.setTextColor(Color.parseColor("#33000000"));
        }
        cellTextView.setText(mCalendarDataset.get(position));
        if(mEventDataset == null) {
            return;
        }else{
            for(Integer event : mEventDataset){
                if(String.valueOf(event).contentEquals(cellTextView.getText())){
                    cellTextView.setTextColor(Color.BLACK);
                    dotCounter++;
                    if( dotCounter >= 1 ) {
                        dots[dotCounter] = '.';
                    }
                    dotString = new String(dots);
                    cellTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment sessionFragment =
                                    MapFragment.newInstance(event.toString(),null);
                            FragmentTransaction fragmentTransaction =
                                    MainActivity.shared().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.main_activity,
                                    sessionFragment,"Monthly Calendar Fragment");
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                }
            }
            if(dotString == null) {
                ;
            }else{

                final String eventCellContents =
                        cellTextView.getText() + dotString;
                SpannableString dotStringColored =
                        new SpannableString(dotString);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.RED),0
                        ,1,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.BLUE),1
                        ,2,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.RED),2
                        ,3,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.BLUE),3
                        ,4,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.RED),4
                        ,5,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.BLUE),5
                        ,6,0);
                dotStringColored.setSpan(new ForegroundColorSpan(Color.BLUE),5
                        ,6,0);

                final SpannableString cellContents = new SpannableString(
                        eventCellContents);
                cellContents.setSpan(new RelativeSizeSpan(1.0f),
                        cellTextView.getText().toString().length() + 1,
                        dotStringColored.length(),0 );
                cellTextView.setText(cellContents);
            }
        }
//        String cellTextString =
//                mCalendarDataset.get(position) + new String(dots);
//        cellTextView.setText(cellTextString);

//        if(eventCount <= 3){
//            LinearLayout linearLayout =
//                    holder.cellView.findViewById(R.id.circle_row_1);
//            if(eventCount <= 1){
//                linearLayout.findViewById(R.id.one_event_day).setVisibility(View.VISIBLE);
//                linearLayout.findViewById(R.id.two_event_day).setVisibility(View.INVISIBLE);
//                linearLayout.findViewById(R.id.three_event_day).setVisibility(View.INVISIBLE);
//            }
//
//            if(eventCount == 2){
//                linearLayout.findViewById(R.id.two_event_day).setVisibility(View.VISIBLE);
//            }
//        }else if(eventCount <= 7){
//            LinearLayout linearLayout1 =
//                    holder.cellView.findViewById(R.id.circle_row_1);
//            LinearLayout linearLayout2 =
//                    holder.cellView.findViewById(R.id.circle_row_2);
//            linearLayout1.setVisibility(View.VISIBLE);
//            if(eventCount == 7){
//                holder.cellView.findViewById(R.id.circle_row_3).setVisibility(View.VISIBLE);
//            }else {
//                if(eventCount == 6){
//                    linearLayout2.setVisibility(View.VISIBLE);
//                }else if(eventCount == 5){
//                    linearLayout2.findViewById(R.id.four_event_day).setVisibility(View.VISIBLE);
//                }else{
//                    linearLayout2.findViewById(R.id.four_event_day).setVisibility(View.VISIBLE);
//                    linearLayout2.findViewById(R.id.five_event_day).setVisibility(View.VISIBLE);
//                }
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return mCalendarDataset.size();
    }
}
