package com.decalthon.helmet.stability.AsyncTasks.SessionInfoAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.SessionCdlDb;

import java.util.ArrayList;
import java.util.List;

public class GetCollectiveSummaryDetailsAsyncTask extends AsyncTask<Void,
        Void, List<Float>> {
    @Override
    protected List<Float> doInBackground(Void... voids) {
        int nSessions =
                SessionCdlDb.getInstance().getSessionDataDAO().getCollectiveMNumberOfSessions();
        int totalDataSize =
                SessionCdlDb.getInstance().getSessionDataDAO().getTotalDataInBytes() / 1024;
        Float duration_total =
                SessionCdlDb.getInstance().getSessionDataDAO().getAllActivitiesTotalTime();
        Integer [] activity_codes =
                SessionCdlDb.getInstance().getSessionDataDAO().getAllActivityTypes();
        List<Float> summary_list = new ArrayList<>();
        summary_list.add((float) nSessions);
        summary_list.add(duration_total);
        summary_list.add((float) totalDataSize);
        summary_list.add((float )activity_codes.length);
        return  summary_list;
    }
}