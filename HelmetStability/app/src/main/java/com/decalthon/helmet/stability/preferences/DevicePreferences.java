package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.decalthon.helmet.stability.Utilities.Constants;


public class DevicePreferences {
    private static DevicePreferences single_instance = null;


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
}
