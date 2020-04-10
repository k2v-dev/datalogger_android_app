package com.decalthon.helmet.stability.webservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class Service {
    // String
    protected static final String APPN_JSON = "application/json" ;
    protected static final String Content_Type = "Content-Type";
    protected static final String Accept = "Accept";
    protected static final String Authorization = "Authorization";

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.get("application/json; charset=utf-8");
//    public final String  BASE_URL = "http://34.89.132.245:8080/DKT-CDL-WS/cdl-api";
    public final String  BASE_URL = "http://192.168.1.10:8080/DKT-CDL-WS/cdl-api";
    public final String  POST = "POST";
    public final String  GET = "GET";
//    public OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .connectTimeout(10, TimeUnit.SECONDS)
                                        .writeTimeout(10, TimeUnit.SECONDS)
                                        .readTimeout(30, TimeUnit.SECONDS)
                                        .build();
    public Gson gson = new GsonBuilder().create();

    public Service() {

    }
}
