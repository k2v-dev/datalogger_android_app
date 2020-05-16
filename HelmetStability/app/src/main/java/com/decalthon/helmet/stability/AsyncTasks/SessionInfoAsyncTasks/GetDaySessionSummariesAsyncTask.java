package com.decalthon.helmet.stability.asynctasks.sessioninfoasynctasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.database.SessionCdlDb;

import java.util.Calendar;
import java.util.List;

public class GetDaySessionSummariesAsyncTask extends AsyncTask<Calendar,
        Void, List<SessionSummary>> {

    @Override
    protected List<SessionSummary> doInBackground(Calendar... dates) {
        Calendar day = dates[0];
        Calendar startOfDay = Calendar.getInstance();
        startOfDay.set(day.get(Calendar.YEAR),
                day.get(Calendar.MONTH),
                day.get(Calendar.DATE),
                0,0,0);
        Calendar endOfDay = Calendar.getInstance();

        endOfDay.set(day.get(Calendar.YEAR),
                day.get(Calendar.MONTH),
                day.get(Calendar.DATE),
                23,59,59);

        return SessionCdlDb.getInstance().getSessionDataDAO().getDailySessionSummary
                (startOfDay.getTimeInMillis(),endOfDay.getTimeInMillis());
    }
}