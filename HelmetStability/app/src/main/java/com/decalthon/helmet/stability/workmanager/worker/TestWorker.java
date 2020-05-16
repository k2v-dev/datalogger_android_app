package com.decalthon.helmet.stability.workmanager.worker;

import android.content.Context;
import android.util.Log;

import com.decalthon.helmet.stability.utilities.Common;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TestWorker extends Worker {
    String TAG = TestWorker.class.getSimpleName();
    public TestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Common.wait(2000);
        Log.d(TAG, "Test worker's waiting is completed: date="+new Date());
        return  Result.success();
    }
}
