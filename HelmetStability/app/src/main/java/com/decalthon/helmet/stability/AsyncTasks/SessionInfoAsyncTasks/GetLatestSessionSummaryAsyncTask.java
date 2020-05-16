package com.decalthon.helmet.stability.asynctasks.sessioninfoasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.database.SessionCdlDb;

public class GetLatestSessionSummaryAsyncTask extends AsyncTask<Void,
        Void, SessionSummary> {

    @Override
    protected SessionSummary doInBackground(Void... voids) {
        return SessionCdlDb.getInstance().getSessionDataDAO().getLatestSessionSummary();
    }

}
