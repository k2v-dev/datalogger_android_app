package com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.model.NineAxisModels.QueryParameters;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;

import java.util.List;

public class GetDevice1ThreeAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    @Override
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameters1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getThreeAxisDevice1(queryParameters1.session_id);
    }
}
