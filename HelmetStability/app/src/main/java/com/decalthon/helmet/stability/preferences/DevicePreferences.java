package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StatFs;
import android.util.Log;

import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.utilities.FileUtilities;

import java.io.File;


public class DevicePreferences {
    private static DevicePreferences single_instance = null;
    private final String LST_TIME = "LAST_TIME_CHK";

    private SharedPreferences sharedpreferences;

    /**
     * Private constructor
     * @param context Application context for sharedpreference object
     */
    private DevicePreferences(Context context) {
        sharedpreferences = context.getSharedPreferences(Constants.DevPREFERENCES, Context.MODE_PRIVATE);
    }

    // Allow to create only one object
    public static DevicePreferences getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new DevicePreferences(context);
        }

        return single_instance;
    }

    /**
     * Save user's name
     * @param name user's name
     */
    public void saveName(String device_id, String name){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(device_id, name);
        editor.apply();
    }

    /**
     * Get user's name
     */
    public String getName(String device_id){
        return sharedpreferences.getString(device_id, "");
    }

    /**
     * Save user's mac_address
     * @param mac_address user's mac_address
     */
    public void saveAddr(String device_id, String mac_address){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(device_id+".addr", mac_address);
        editor.apply();
    }

    /**
     * Get user's name
     */
    public String getAddr(String device_id){
        return sharedpreferences.getString(device_id+".addr", "");
    }

    /**
     * Clear the value of all devices
     */
    public void clear(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void lowStorageAlert(Context context){
        try{
            long timestamp_ms = sharedpreferences.getLong(LST_TIME, 0);
            long cur_timestamp = System.currentTimeMillis();
//            if((cur_timestamp - timestamp_ms) < 24*3600*1000){
            if((cur_timestamp - timestamp_ms) < 600*1000){
                System.out.println("Cur ts="+cur_timestamp+", prev ts="+timestamp_ms);
                return;
            }

//            Context context = MainApplication.getAppContext();
            String path = context.getPackageName() + File.separator ;
            File file = FileUtilities.createDirIfNotExists(path);
            long mb_avaiable = (new StatFs(file.getAbsolutePath()).getAvailableBytes())/(1024*1024);
            if(mb_avaiable < 500){
                Common.okAlertMessage(context, context.getString(R.string.low_storage_alert));
            }
            System.out.println("Memory available:"+mb_avaiable);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(LST_TIME, cur_timestamp);
            editor.apply();
        }catch (Exception ex){
            Log.d("Common","lowStorageAlert:"+ex.getMessage());
        }
    }
}
