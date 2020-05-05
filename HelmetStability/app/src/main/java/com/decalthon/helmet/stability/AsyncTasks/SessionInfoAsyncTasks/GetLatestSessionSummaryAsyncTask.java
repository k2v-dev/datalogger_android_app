package com.decalthon.helmet.stability.AsyncTasks.SessionInfoAsyncTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;

public class GetLatestSessionSummaryAsyncTask extends AsyncTask<Void,
        Void, SessionSummary> {

    @Override
    protected SessionSummary doInBackground(Void... voids) {
        return SessionCdlDb.getInstance().getSessionDataDAO().getLatestSessionSummary();
    }

}
