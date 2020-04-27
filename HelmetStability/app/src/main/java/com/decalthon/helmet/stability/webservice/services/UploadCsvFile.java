package com.decalthon.helmet.stability.webservice.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.decalthon.helmet.stability.DB.Entities.CsvFileStatus;
import com.decalthon.helmet.stability.DB.SessionCdlDb;
import com.decalthon.helmet.stability.Utilities.Constants;
import com.decalthon.helmet.stability.Utilities.FileUtilities;
import com.decalthon.helmet.stability.model.InternetCheck;
import com.decalthon.helmet.stability.preferences.ProfilePreferences;
import com.decalthon.helmet.stability.preferences.UserPreferences;
import com.decalthon.helmet.stability.webservice.responses.ErrorCodes;
import com.decalthon.helmet.stability.webservice.responses.ErrorMessages;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UploadCsvFile {
    private static final String TAG = UploadCsvFile.class.getSimpleName();
//    private final String API_KEY = "AIzaSyD47XeJv-_Q31QHekEftRrEmd3Zu8xYrHE";
    protected static final String APPN_JSON = "application/json" ;
    protected static final String Content_Type = "Content-Type";
    protected static final String Accept = "Accept";
    protected static final String Authorization = "Authorization";
    public final String  PUT = "PUT";
    public final String  GET = "GET";
    private Context context;
    private SessionCdlDb sessionCdlDb;
    private String PATH = "";
//    List<File> upload_files;
//    private static int NUM_FILE = 0;
    private static UploadCsvFile singleton = null;

    private UploadCsvFile(Context context) {
        //this.context = context;
        sessionCdlDb = SessionCdlDb.getInstance(context);
        PATH = context.getPackageName() + File.separator + Constants.CSV_LOG_DIR;
    }

    public static UploadCsvFile getInstance(Context context){
        if(singleton == null){
            singleton = new UploadCsvFile(context);
        }
        return singleton;
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                startUploadingFiles();
            }
        }
        ).start();
    }


    public void startUploadingFiles(){
//        if(NUM_FILE > 0){
//            Log.d(TAG, "Already running: remaining files="+NUM_FILE);
//            return;
//        }
//        NUM_FILE = 0;
        List<CsvFileStatus> csvFileStatus_ls = sessionCdlDb.getCsvDao().getCsvFiles();
       // upload_files = new ArrayList<>();
        if(csvFileStatus_ls.size() == 0){
            Log.d(TAG, "No CSV File to upload");
            return;
        }
        File root_dir = FileUtilities.createDirIfNotExists(PATH);
        if (root_dir==null) {
            return;
        }

        //Executor service instance
        ExecutorService executor = Executors.newFixedThreadPool(1);
        for (CsvFileStatus csvFileStatus: csvFileStatus_ls) {
            String filename = csvFileStatus.filename;
            File file = new File(root_dir, filename);
            if(file.exists()){
//                upload_files.add(file);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        new InternetCheck(isInternet -> {
                            if(isInternet){
//                                File file = upload_files.remove(0);
                                getStorageUrl(file);
                            }
//                            else{
//                                NUM_FILE--;
//                            }
                        });
                    }
                };
                executor.execute(runnable);
//                NUM_FILE++;
            }
        }
        Log.d(TAG, " Before executor shutdown");
        executor.shutdown();
        Log.d(TAG, " After executor shutdown");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new InternetCheck(isInternet -> {
//                    if(isInternet){
//                        File file = upload_files.remove(0);
//                        getStorageUrl(file);
//                    }
//                });
//            }
//        }).start();
    }

    public void getStorageUrl(File file ){
        try{
            String filename = file.getName();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://presign-esp-63cgadl5uq-ew.a.run.app/signed_url?api_key="+ Constants.UPLOAD_API_KEY +"&object_name="+filename)
                    .method(GET, null)
                    .build();
//            Response response = client.newCall(request).execute();
            if(request != null) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if(responseBody != null){
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Get storage url for file("+file.getName()+")");
                                    String url_str = responseBody.string();
                                    putCsvFile(url_str, file);
                                }
//                                else{
//                                    NUM_FILE--;
//                                }
                            }
//                            else{
//                                NUM_FILE--;
//                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
//                        NUM_FILE--;
                    }
                });
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void putCsvFile(String url_str, File file){
        try{
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(75, TimeUnit.SECONDS)
                    .build();
            MediaType mediaType = MediaType.parse("text/csv");
            RequestBody body = RequestBody.create( file, mediaType);
            Request request = new Request.Builder()
                    .url(url_str)
                    .method(PUT, body)
                    .addHeader(Content_Type, "text/csv")
                    .build();
//            Response response = okHttpClient.newCall(request).execute();

            okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if(responseBody != null && response.isSuccessful()){
                            Log.d(TAG, "Uploaded file("+file.getName()+") is successfully done");
                            sessionCdlDb.getCsvDao().deleteFile(file.getName());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
//                    NUM_FILE--;
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
//                    NUM_FILE--;
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
