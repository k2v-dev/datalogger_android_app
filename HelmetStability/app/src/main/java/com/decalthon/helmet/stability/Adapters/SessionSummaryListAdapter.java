package com.decalthon.helmet.stability.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.model.generic.TimeFmt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        String dateString = new SimpleDateFormat("MMM dd YYYY HH:MM:SS EEE",
                Locale.getDefault()).format(currentDate);
        cardTitle.setText(dateString);

//        convertView.findViewsWithText(textViewArrayList,card_text, -1);

        String [] textlines = new String[Constants.MAX_SESSION_CARD_LINES];
        textlines[0] =
                 sessionSummary.getName();
        textlines[1] =
                 Constants.ActivityCodeMap.inverse().get(sessionSummary.getActivity_type());

        TimeFmt timeFmt = Common.convertToTimeFmt((long)(sessionSummary.getDuration()*1000));
        String total_duration =
                 String.format(Locale.getDefault(), "%02d:%02d:%02d", timeFmt.hr, timeFmt.min, timeFmt.sec);//+collective_summary_info.get(1).toString();

        textlines[2]  = String.valueOf(total_duration);
        textlines[3] =
                String.valueOf(sessionSummary.getSize());
        textlines[4] =
                 String.valueOf(sessionSummary.getSampling_freq());
        textlines[5] =
                String.valueOf(Constants.typesOfData);
        if(sessionSummary.getNote().isEmpty()){
            textlines[6] = "-";
        }else{
            textlines[6] =
                    String.valueOf(sessionSummary.getNote());
        }



        TextView session_name_tv =
                convertView.findViewById(R.id.session_name_card_tv);
        session_name_tv.setText(Html.fromHtml(textlines[0]));

        TextView activity_type_tv =
                convertView.findViewById(R.id.type_of_activity_tv);
        activity_type_tv.setText(textlines[1]);

        TextView duration_tv = convertView.findViewById(R.id.duration_tv);
        duration_tv.setText(textlines[2]);

        TextView total_data_tv = convertView.findViewById(R.id.total_data_tv);
        total_data_tv.setText(textlines[3]);

        TextView sample_frequency_tv =
                convertView.findViewById(R.id.sampling_rate_tv);
        sample_frequency_tv.setText(textlines[4]);

        TextView types_of_data_tv =
                convertView.findViewById(R.id.types_of_data_tv);
        types_of_data_tv.setText(textlines[5]);

        TextView note_tv =
                convertView.findViewById(R.id.text_note_summary_line_tv);
        note_tv.setText(textlines[6]);

        return convertView;

    }
};


