package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.adapters.GraphPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static com.decalthon.helmet.stability.utilities.Constants.DEV2_NINE_AXES_NAME;
import static com.decalthon.helmet.stability.utilities.Constants.DEV2_THREE_AXES_NAME;
import static com.decalthon.helmet.stability.utilities.Constants.GPS_SPEED_NAME;
import static com.decalthon.helmet.stability.utilities.Constants.DEV1_NINE_AXES_NAME;
import static com.decalthon.helmet.stability.utilities.Constants.DEV1_THREE_AXES_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * This class holds the pager for individual graph pages
 */
public class CustomViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    // TODO: Rename and change types of parameters
    private long session_id;
    private long clicked_ts;
    private long start_ts;

    private OnFragmentInteractionListener mListener;
    private GraphPagerAdapter customGraphPagerAdapter;
    private ViewPager2 viewPager;
    private View actionBarView;

    public CustomViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param session_id Parameter 1.
     * @param click_ts Parameter 2.
     * @param start_ts Parameter 2.
     * @return A new instance of fragment CustomViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomViewFragment newInstance(Long session_id, Long click_ts, Long start_ts) {
        CustomViewFragment fragment = new CustomViewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, session_id);
        args.putLong(ARG_PARAM2, click_ts);
        args.putLong(ARG_PARAM3, start_ts);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Fragment creation
     * @param savedInstanceState Any restore-able data
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            session_id = getArguments().getLong(ARG_PARAM1);
            clicked_ts = getArguments().getLong(ARG_PARAM2);
            start_ts = getArguments().getLong(ARG_PARAM3);
        }
    }


    /**
     *
     * @param inflater The callback argument which inflates a {@link CustomViewFragment}
     * @param container The parent container
     * @param savedInstanceState savedInstanceState
     * @return The view of the fragment, a rectangular area on the screen, usable by
     * a tab layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pager_view,container,false);
    }

    /**
     * Initialize action bar and event handlers
     * @param savedInstanceState Any restore-able data
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View backLink =
                CustomViewFragment.this.getView().findViewById(R.id.back_navigation);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.shared().onBackPressed();
            }
        });
    }

    /**
     * The view pager is used along with the tab layout, to navigate accross different graphs
     * @param view The rectangular under the tab layout which can hold graphs
     * @param savedInstanceState Any restore-able data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //Define the adapter for interfacing to the pager view

        System.out.println("Custom fragment view created");

        customGraphPagerAdapter = new GraphPagerAdapter(getActivity(), session_id, clicked_ts, start_ts);

        //Extract the pager from ID and set its defined adapter

        viewPager = view.findViewById(R.id.pager);

        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(customGraphPagerAdapter);

        /**
         * Matches the tab position or graph with the tab layout
         */
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(getPageTitle(position))
        ).attach();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack("MapFragment",0);

        System.out.println("Tab layout successfully linked with page adapter");
    }

    /**
     * Assigns page title based on position
     * @param position The tab layout index
     * @return Page title as character sequence
     */
    public CharSequence getPageTitle(int position) {
        System.out.println("Inside get page title");
        switch(position){
            case 0:
                return(DEV1_NINE_AXES_NAME);
            case 1:
                return(DEV1_THREE_AXES_NAME);
            case 2:
                return(DEV2_NINE_AXES_NAME);
            case 3:
                return(DEV2_THREE_AXES_NAME);
            case 4:
                return(GPS_SPEED_NAME);
                //====Additional charts below====//
        }
        return "No page";
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
