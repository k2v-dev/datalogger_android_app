package com.decalthon.helmet.stability.AsyncTasks.ImuAsyncTasks;

import android.os.AsyncTask;

import com.decalthon.helmet.stability.DB.Entities.SessionSummary;
import com.decalthon.helmet.stability.DB.SessionCdlDb;

public class GetFirmwareTypeAsyncTask extends AsyncTask<Void,
        Void, Integer> {
    //TODO replace this with the firmware type from the database
    @Override
    protected Integer doInBackground(Void... voids) {
        return 1;
    }
}
