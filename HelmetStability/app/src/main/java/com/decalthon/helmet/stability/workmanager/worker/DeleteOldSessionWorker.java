package com.decalthon.helmet.stability.workmanager.worker;

import android.content.Context;
import android.util.Log;

import com.decalthon.helmet.stability.database.DatabaseHelper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DeleteOldSessionWorker  extends Worker {

    public DeleteOldSessionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        new DatabaseHelper.DeleteOldSessions().execute();
        Log.d(DeleteOldSessionWorker.class.getSimpleName(), "Delete old session is going on.");
        return Result.success();
    }
}
