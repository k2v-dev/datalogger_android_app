package com.decalthon.helmet.stability.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.Adapters.SessionSummaryListAdapter;
import com.decalthon.helmet.stability.AsyncTasks.SessionInfoAsyncTasks.GetDaySessionSummariesAsyncTask;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailySessionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailySessionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = DailySessionsFragment.class.getSimpleName();
    private Context mContext;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DailySessionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailySessionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailySessionsFragment newInstance(String param1, String param2) {
        DailySessionsFragment fragment = new DailySessionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        return inflater.inflate(R.layout.fragment_daily_sessions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        ListView sessionListView = view.findViewById(R.id.daily_session_summary);
        List<SessionSummary> dailySessionSummaries = new ArrayList<>();

        try {
            Log.d(TAG, "onViewCreated: Proceeding to async task");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH,Integer.parseInt(mParam2));
            calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(mParam1));
            Log.d(TAG,
                    "onViewCreated: checking date" + calendar.getTime().toString());
            dailySessionSummaries = new GetDaySessionSummariesAsyncTask().execute(calendar).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        final List<SessionSummary> dailySessionSummaries_cp = dailySessionSummaries;
        sessionListView.setAdapter(new SessionSummaryListAdapter(mContext,
                dailySessionSummaries));
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionSummary sessionSummary = dailySessionSummaries_cp.get(position);
                Fragment mapFragment =
                        MapFragment.newInstance(Constants.ActivityCodeMap.inverse().get(sessionSummary.getActivity_type()), (long)4, sessionSummary.getDuration());
                FragmentManager fragmentManager
                        = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(DailySessionsFragment.this.getId(),
                        mapFragment,DailySessionsFragment.class.getSimpleName());
                fragmentTransaction.addToBackStack(DailySessionsFragment.class.getSimpleName());
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
