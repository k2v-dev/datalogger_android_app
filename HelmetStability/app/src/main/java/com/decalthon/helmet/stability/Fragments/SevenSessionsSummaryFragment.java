package com.decalthon.helmet.stability.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;

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
            sessionSummaries = new GetLastSevenSessionSummariesAsyncTask().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        ActionBar mActionBar = ( (MainActivity) getActivity() ).getSupportActionBar();
        View actionBarView = (LinearLayout) mActionBar.getCustomView();

        actionBarView.findViewById(R.id.gps_session_start_btn).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.profile_link).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.ble_device_connectivity).setVisibility(View.GONE);
        actionBarView.findViewById(R.id.logout_link).setVisibility(View.INVISIBLE);

        TextView titleText = actionBarView.findViewById(R.id.title_text);
        titleText.setText(R.string.seven_sessions_summary);
        titleText.setVisibility(View.VISIBLE);

        CircleImageView backLink = actionBarView.findViewById(R.id.back_link);
        backLink.setVisibility(View.VISIBLE);
        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.shared().onBackPressed();
            }
        });


        SessionListAdapter sessionListAdapter = new SessionListAdapter(sessionSummaries);
        sessionListView.setAdapter(sessionListAdapter);

        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment mapFragment = MapFragment.newInstance("SESSION"+position , "Display");
                FragmentManager fragmentManager
                        = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.session_summary_fragment,mapFragment,"Seven Session Summary");
                fragmentTransaction.commit();
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

    /**
     * SessionListAdapter is used to make the listView interface compatible with
     * session data.
     *
     * The session data is organized into UI cards using the interface provided by
     * adapter.
     */
    class SessionListAdapter extends BaseAdapter{

        List<SessionSummary> sessionSummaries;

        SessionListAdapter(List<SessionSummary> sessionSummaries)   {
            this.sessionSummaries = sessionSummaries;
        }
        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */


        @Override
        public int getCount() {
            return sessionSummaries.size();
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
            return  sessionSummaries.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
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

            System.out.println("Get view count " + position);

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.session_summary_view,parent,false);
            }
            SessionSummary sessionSummary = (SessionSummary) getItem(position);
            System.out.println("session summary"+sessionSummary.toString()+ " postion"+position);

            return convertView;
        }
    }

    private static class GetLastSevenSessionSummariesAsyncTask extends AsyncTask<Void,Void, List<SessionSummary>> {

        @Override
        protected List<SessionSummary> doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Entering checking the query");
            return SessionCdlDb.getInstance(mContext).getSessionDataDAO().getLastSevenSessionSummaries();
        }
    }
}


