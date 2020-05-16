package com.decalthon.helmet.stability.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.decalthon.helmet.stability.activities.MainActivity;
import com.decalthon.helmet.stability.adapters.SessionSummaryListAdapter;
import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SevenSessionsSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SevenSessionsSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * This class defines the view for session summary cards
 */
public class SevenSessionsSummaryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static Context mContext;
    private static String TAG = SevenSessionsSummaryFragment.class.getSimpleName();

    public SevenSessionsSummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SevenSessionsSummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SevenSessionsSummaryFragment newInstance(String param1, String param2) {
        SevenSessionsSummaryFragment fragment = new SevenSessionsSummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: called ");
        return inflater.inflate(R.layout.fragment_seven_sessions_summary, container, false);
    }

    /**
     * Updates to action bar and initializing a list view adapter for the session summaries
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView sessionListView = view.findViewById(R.id.session_summary);
        List<SessionSummary> sessionSummaries = new ArrayList<>();
        try {
            Log.d(TAG, "onViewCreated: Proceeding to async task");
            sessionSummaries =
                    new GetLastSevenSessionSummariesAsyncTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(sessionSummaries.size() == 0){
            Common.okAlertMessage(getContext(),"No session yet");
            return;
        }

        sessionListView.setAdapter(new SessionSummaryListAdapter(mContext,
                sessionSummaries));
        List<SessionSummary> finalSessionSummaries = sessionSummaries;
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionSummary sessionSummary = finalSessionSummaries.get(position);
                Fragment mapFragment =
                MapFragment.newInstance(Constants.ActivityCodeMap.inverse().get(sessionSummary.getActivity_type()), sessionSummary.getSession_id(), sessionSummary.getDuration());
                FragmentManager fragmentManager
                        = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(SevenSessionsSummaryFragment.this.getId(),
                        mapFragment,MapFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        CircleImageView backLink = view.findViewById(R.id.back_navigation);
        backLink.setVisibility(View.VISIBLE);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.shared().onBackPressed();
            }
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        mContext = context;
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

    private static class GetLastSevenSessionSummariesAsyncTask extends AsyncTask<Void,Void, List<SessionSummary>> {

        @Override
        protected List<SessionSummary> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Entering checking the query");
            return SessionCdlDb.getInstance().getSessionDataDAO().getLastSevenSessionSummaries();
        }
    }

//    private class GetDaySessionSummariesAsyncTask extends AsyncTask<Void,Void,
//            List<SessionSummary>>{
//        @Override
//        protected List<SessionSummary> doInBackground(Void... voids) {
//            Log.d(TAG, "doInBackground: Entering checking the query");
//            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().getLastSevenSessionSummaries();
//        }
//    }
}


