package com.decalthon.helmet.stability.workmanager.worker;


import android.content.Context;

import com.decalthon.helmet.stability.utilities.CsvGenerator;
import com.decalthon.helmet.stability.preferences.CsvPreference;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CsvGenerationWorker extends Worker {

    public CsvGenerationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        startGeneration();
        return Result.success();
    }

    private void startGeneration(){
        Context context = getApplicationContext();

        Set<Long> session_ids = CsvPreference.getInstance(getApplicationContext()).getSessionIds();
        //session_ids.clear();//session_ids.add(4l);

        //Executor service instance
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (Long session_id: session_ids) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    CsvGenerator csvGenerator = new CsvGenerator(context);
                    csvGenerator.generateCSV(session_id);
                }
            };
            executor.execute(runnable);
        }
        executor.shutdown();
    }
}

