package com.decalthon.helmet.stability.asynctasks.imuasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.model.nineaxismodels.QueryParameters;
import com.decalthon.helmet.stability.model.nineaxismodels.SensorDataEntry;

import java.util.List;

public class GetDevice2MagnetoNineAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    @Override
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameter1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getDevice2MagnetometerData(queryParameter1.session_id,queryParameter1.tStart, queryParameter1.tEnd);
    }
}
