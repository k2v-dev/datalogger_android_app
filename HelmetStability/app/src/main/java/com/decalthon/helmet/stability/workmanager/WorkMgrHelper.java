package com.decalthon.helmet.stability.workmanager;

import android.content.Context;
import android.util.Log;

import com.decalthon.helmet.stability.workmanager.worker.CsvGenerationWorker;
import com.decalthon.helmet.stability.workmanager.worker.CsvUploadingWorker;
import com.decalthon.helmet.stability.workmanager.worker.DeleteOldSessionWorker;
import com.decalthon.helmet.stability.workmanager.worker.TestWorker;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class WorkMgrHelper {
    private static long TIME_INTERVAL_IN_MIN =  10;
    private static long TIME_INTERVAL_IN_SEC =  600;
    private final String CSV_UPLOAD_WORKER = "Csv_Uploading_Worker";
    private final String CSV_GENERATE_WORKER = "Csv_Generation_Worker";
    private final String DELETE_SESSION_WORKER = "DELETE_SESSION_WORKER";
    private Context context;

    public WorkMgrHelper(Context context) {
        this.context = context;
    }

    /**
     * Check whether the WorkRequest with Tag is running/pending 0r cancel
     * @param tag
     * @return
     */
    private boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance(context);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

//    if(!isWorkScheduled(TAG_MY_WORK)) { // check if your work is not already scheduled
//        schedulePeriodicallyWork(TAG_MY_WORK); // schedule your work
//    }
    /**
     * Schedule worker periodically if workrequest is already pending, then will not add more schduler
     */
    public void periodicCSVGenerationRequest_test() {
        if(isWorkScheduled(CSV_UPLOAD_WORKER+"test")){
            Log.d("periodicCSVGeneration", CSV_GENERATE_WORKER+"test"+" is already pending or running");
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)// you can add as many constraints as you want
                .build();
        PeriodicWorkRequest.Builder csvGenerateBuilder =
                new PeriodicWorkRequest.Builder(TestWorker.class, 10,
                        TimeUnit.MINUTES);
        csvGenerateBuilder.setInitialDelay(100,TimeUnit.SECONDS);
        csvGenerateBuilder.setConstraints(constraints);
        PeriodicWorkRequest csvGenerateWorker = csvGenerateBuilder.build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueueUniquePeriodicWork(CSV_GENERATE_WORKER+"test", ExistingPeriodicWorkPolicy.KEEP , csvGenerateWorker);
    }


    /**
     * Schedule worker periodically if workrequest is already pending, then will not add more schduler
     */
    public void periodicCSVGenerationRequest() {
        if(isWorkScheduled(CSV_UPLOAD_WORKER)){
            Log.d("periodicCSVGeneration", CSV_GENERATE_WORKER+" is already pending or running");
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
               .setRequiresStorageNotLow(true)// you can add as many constraints as you want
               .build();
        PeriodicWorkRequest.Builder csvGenerateBuilder =
                new PeriodicWorkRequest.Builder(CsvGenerationWorker.class, TIME_INTERVAL_IN_MIN,
                        TimeUnit.MINUTES);
        csvGenerateBuilder.setInitialDelay(600,TimeUnit.SECONDS);
        csvGenerateBuilder.setConstraints(constraints);
        PeriodicWorkRequest csvGenerateWorker = csvGenerateBuilder.build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueueUniquePeriodicWork(CSV_GENERATE_WORKER, ExistingPeriodicWorkPolicy.KEEP , csvGenerateWorker);
    }

    /**
     * One Time Request for uploading pending csv files.
     */
    public void oneTimeCSVUploadingRequest(){
        if(isWorkScheduled(CSV_UPLOAD_WORKER)){
            Log.d("oneTimeCSVUploading", CSV_UPLOAD_WORKER+" is already pending or running");
            return;
        }
        //creating constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // you can add as many constraints as you want
                .build();
// Passing data to worker thread
//        Data data = new Data.Builder()
//                .putString("key", "The task data passed from MainActivity")
//                .build();

        final OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(CsvUploadingWorker.class)
//                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(30, TimeUnit.SECONDS)
                        .build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueueUniqueWork(CSV_UPLOAD_WORKER, ExistingWorkPolicy.REPLACE , workRequest);
    }

    /**
     * One Time Request for uploading pending csv files.
     */
    public void oneTimeCSVGenerationRequest(){
        if(isWorkScheduled(CSV_UPLOAD_WORKER)){
            Log.d("oneTimeCSVGeneration", CSV_GENERATE_WORKER+" is already pending or running");
            return;
        }
        //creating constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)// you can add as many constraints as you want
                .build();

        final OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(CsvGenerationWorker.class)
//                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(TIME_INTERVAL_IN_SEC, TimeUnit.SECONDS)
                        .build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueueUniqueWork(CSV_GENERATE_WORKER, ExistingWorkPolicy.KEEP , workRequest);
    }

    /**
     * One Time Request for uploading pending csv files.
     */
    public void oneTimeDeleteSessionRequest(){
        if(isWorkScheduled(DELETE_SESSION_WORKER)){
            Log.d("oneTimeCSVGeneration", DELETE_SESSION_WORKER+" is already pending or running");
            return;
        }
        //creating constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        final OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(DeleteOldSessionWorker.class)
//                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(60, TimeUnit.SECONDS)
                        .build();
        WorkManager instance = WorkManager.getInstance(context);
        instance.enqueueUniqueWork(DELETE_SESSION_WORKER, ExistingWorkPolicy.KEEP , workRequest);
    }

}
