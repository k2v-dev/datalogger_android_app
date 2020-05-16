package com.decalthon.helmet.stability.asynctasks.imuasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.SessionCdlDb;
import com.decalthon.helmet.stability.model.nineaxismodels.QueryParameters;
import com.decalthon.helmet.stability.model.nineaxismodels.SensorDataEntry;

import java.util.List;

public class GetDevice1ThreeAxisSensorDataAsyncTask extends AsyncTask<QueryParameters,
        Void, List<SensorDataEntry>> {
    @Override
    protected List<SensorDataEntry> doInBackground(QueryParameters... queryParameters) {
        QueryParameters queryParameters1 = queryParameters[0];
        return SessionCdlDb.getInstance().getSessionDataDAO().getThreeAxisDevice1(queryParameters1.session_id);
    }
}
