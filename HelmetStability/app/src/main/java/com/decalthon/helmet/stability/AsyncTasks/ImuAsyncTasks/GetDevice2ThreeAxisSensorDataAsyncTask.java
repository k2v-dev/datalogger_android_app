package com.decalthon.helmet.stability.asynctasks.imuasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.model.nineaxismodels.QueryParameters;
import com.decalthon.helmet.stability.model.nineaxismodels.SensorDataEntry;

import java.util.List;

public class GetDevice2ThreeAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameters1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getThreeAxisDevice2(queryParameters1.session_id,queryParameters1.tStart, queryParameters1.tEnd);
    }
}
