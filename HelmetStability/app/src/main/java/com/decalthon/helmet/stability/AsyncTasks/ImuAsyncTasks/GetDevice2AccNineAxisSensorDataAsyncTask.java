package com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.model.NineAxisModels.QueryParameters;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;

import java.util.List;

public class GetDevice2AccNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    @Override
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameter1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getDevice2AccelerometerData(queryParameter1.session_id,queryParameter1.tStart, queryParameter1.tEnd);
    }
}
