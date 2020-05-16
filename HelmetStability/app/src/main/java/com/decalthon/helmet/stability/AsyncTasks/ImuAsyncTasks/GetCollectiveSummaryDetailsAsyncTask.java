package com.decalthon.helmet.stability.asynctasks.imuasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.SessionCdlDb;

import java.util.ArrayList;
import java.util.List;

public class GetCollectiveSummaryDetailsAsyncTask extends AsyncTask<Void,
        Void, List<Float>> {
    @Override
    protected List<Float> doInBackground(Void... voids) {
        List<Float> summary_list = new ArrayList<>();
        try{
            int nSessions =
                    SessionCdlDb.getInstance().getSessionDataDAO().getCollectiveMNumberOfSessions();
            int totalDataSize =
                    SessionCdlDb.getInstance().getSessionDataDAO().getTotalDataInBytes() / 1024;
            Float duration_total =
                    SessionCdlDb.getInstance().getSessionDataDAO().getAllActivitiesTotalTime();
            Integer [] activity_codes =
                    SessionCdlDb.getInstance().getSessionDataDAO().getAllActivityTypes();


            summary_list.add((float)nSessions);
            summary_list.add(duration_total);
            summary_list.add((float) totalDataSize);
            summary_list.add((float)activity_codes.length);
        }catch (Exception ex){

        }
        return  summary_list;
    }

    @Override
    protected void onPostExecute(List<Float> floats) {

    }
}
