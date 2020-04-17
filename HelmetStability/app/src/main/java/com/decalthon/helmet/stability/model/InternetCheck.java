package com.decalthon.helmet.stability.model;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetCheck extends AsyncTask<Void,Void,Boolean> {

    private Consumer mConsumer;
    public  interface Consumer { void accept(Boolean internet); }

    public  InternetCheck(Consumer consumer) { mConsumer = consumer; execute(); }
    /**
     * Check internet availability
     * Google DNS (e.g. 8.8.8.8) is the largest public DNS in the world.
     * As of 2013 it served 130 billion requests a day. Let 's just say, your app would probably not be the talk of the day.
     */
    @Override protected Boolean doInBackground(Void... voids) { try {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
        sock.close();
        return true;
    } catch (IOException e) { return false; } }

    @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
}