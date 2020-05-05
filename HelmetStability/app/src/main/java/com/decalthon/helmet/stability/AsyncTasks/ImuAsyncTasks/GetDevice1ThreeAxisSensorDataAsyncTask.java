package com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.model.NineAxisModels.SensorDataEntry;

import java.util.List;

public class GetDevice1ThreeAxisSensorDataAsyncTask extends AsyncTask<Long,
        Void, List<SensorDataEntry>> {
    @Override
    protected List<SensorDataEntry> doInBackground(Long... longs) {
        return SessionCdlDb.getInstance().getSessionDataDAO().getThreeAxisDevice1(longs);
    }
}
