package com.decalthon.helmet.stability.workmanager.worker;

import android.content.Context;

import com.decalthon.helmet.stability.MainApplication;
import com.decalthon.helmet.stability.webservice.services.UploadCsvFile;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CsvUploadingWorker  extends Worker {

    public CsvUploadingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
       UploadCsvFile.getInstance(MainApplication.getAppContext()).startUploadingFiles();
        return Result.success();
    }
}
