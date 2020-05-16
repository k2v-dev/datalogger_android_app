package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.decalthon.helmet.stability.adapters.MonthGridAdapter;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Constants;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YearlyCalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YearlyCalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YearlyCalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String currentYear = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int yearCurrent;
    private int earliestYear;

    private static String TAG =
            YearlyCalendarFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private TextView leftButton;
    private TextView rightButton;
    private int mCurrentYear;

    public YearlyCalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YearlyCalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YearlyCalendarFragment newInstance(int param1, int param2) {
        YearlyCalendarFragment fragment = new YearlyCalendarFragment();
        Bundle args = new Bundle();
        args.putInt(currentYear, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            yearCurrent = getArguments().getInt(currentYear);
            earliestYear = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_yearly_calendar, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCurrentYear = yearCurrent;

        Calendar displayedYear = Calendar.getInstance();

        TextView yearTextView = view.findViewById(R.id.year_number_tv);
        int latestYear = displayedYear.get(Calendar.YEAR);
        yearTextView.setText(String.valueOf(mCurrentYear));

        GridView yearView = view.findViewById(R.id.month_only_grid);
        MonthGridAdapter monthGridAdapter =
                new MonthGridAdapter(getContext(), Constants.monthsThreeLetter);
        monthGridAdapter.setmYearPassed(mCurrentYear);
        yearView.setAdapter(monthGridAdapter);

        leftButton = view.findViewById(R.id.previous_year_link_vp);
        rightButton = view.findViewById(R.id.next_year_link_vp);

        if(mCurrentYear == earliestYear){
            leftButton.setVisibility(View.INVISIBLE);
        }

        if(mCurrentYear == latestYear){
            rightButton.setVisibility(View.INVISIBLE);
        }

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentYear--;
                monthGridAdapter.setmYearPassed(mCurrentYear);
                yearTextView.setText(String.valueOf(mCurrentYear));
                rightButton.setVisibility(View.VISIBLE);
                yearView.setAdapter(monthGridAdapter);
                if(mCurrentYear - 1 <= earliestYear){
                    v.setVisibility(View.INVISIBLE);
                }else {
                    v.setVisibility(View.VISIBLE);
                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentYear++;
                monthGridAdapter.setmYearPassed(mCurrentYear);
                yearTextView.setText(String.valueOf(mCurrentYear));
                leftButton.setVisibility(View.VISIBLE);
                yearView.setAdapter(monthGridAdapter);
                if(mCurrentYear + 1 >= latestYear){
                    v.setVisibility(View.INVISIBLE);
                }else{
                    v.setVisibility(View.VISIBLE);
                }
            }
        });


//        yearView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Fragment monthFragment = MonthlyCalendarFragment.newInstance
//                        (Integer.valueOf(position).toString(), yearTextView.getText().toString());
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.year_pager_fragment, monthFragment);
//                fragmentTransaction.commit();
//            }
//        });

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

}
