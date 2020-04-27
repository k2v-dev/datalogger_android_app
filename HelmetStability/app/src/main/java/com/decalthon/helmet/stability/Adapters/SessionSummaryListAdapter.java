package com.decalthon.helmet.stability.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.Utilities.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SessionListAdapter is used to make the listView interface compatible with
 * session data.
 *
 * The session data is organized into UI cards using the interface provided by
 * adapter.
 */
public class SessionSummaryListAdapter extends BaseAdapter {

    private static final String TAG = SessionSummaryListAdapter.class.getSimpleName();
    private List<SessionSummary> sessionSummaries;
    private Context mContext;

    public SessionSummaryListAdapter(Context context, List<SessionSummary> sessionSummaries){
        this.sessionSummaries = sessionSummaries;
        this.mContext = context;
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

        if(convertView == null){
            convertView =
                    LayoutInflater.from(mContext).inflate(R.layout.session_summary_card_layout,
                            parent,false);
        }
        SessionSummary sessionSummary = (SessionSummary) getItem(position);
        //TODO set a common list view adapter for all session summary
        // occurrences. SO that dimensions do not have to be restricted
        TextView cardTitle = convertView.findViewById(R.id.card_title);
        Date currentDate =
                new Date(sessionSummary.getDate());

        cardTitle.setText(currentDate.toString());

//        convertView.findViewsWithText(textViewArrayList,card_text, -1);

        String [] textlines = new String[Constants.MAX_SESSION_CARD_LINES];
        textlines[0] = "Session Name : " + sessionSummary.getName();
        textlines[1] =
                "Activity Type : " + Constants.ActivityCodeMap.inverse().get(52);
        textlines[2]  = "Duration : " + sessionSummary.getDuration();
        textlines[3] =
                "Total data (KB) : " + sessionSummary.getTotal_data()/1024;

        TextView session_name_tv =
                convertView.findViewById(R.id.session_name_tv);
        session_name_tv.setText(textlines[0]);

        TextView activity_type_tv =
                convertView.findViewById(R.id.activity_type_tv);
        activity_type_tv.setText(textlines[1]);

        TextView duration_tv = convertView.findViewById(R.id.duration_tv);
        duration_tv.setText(textlines[2]);

        TextView total_data_tv = convertView.findViewById(R.id.total_data_tv);
        total_data_tv.setText(textlines[3]);

        return convertView;

    }
}


