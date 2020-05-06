package com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.model.NineAxisModels.QueryParameters;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;

import java.util.List;

public class GetDevice2GyroNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameter1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getDevice2GyroscopeData(queryParameter1.session_id,queryParameter1.tStart, queryParameter1.tEnd);
    }
}
