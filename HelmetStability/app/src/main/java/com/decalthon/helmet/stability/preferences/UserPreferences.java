package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.decalthon.helmet.stability.Utilities.Constants;

import java.util.Date;

/**
 * This is singleton class
 * It's used for save the user perferences data in app's local memory
 */
public class UserPreferences {
    private static UserPreferences single_instance = null;


    private SharedPreferences sharedpreferences;
    private Context context;

    /**
     * Private constructor
     * @param context Application context for sharepreference object
     */
    private UserPreferences(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    // Allow to create only one object
    public static UserPreferences getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new UserPreferences(context);
        }

        return single_instance;
    }

    /**
     * Save user's name
     * @param name user's name
     */
    public void saveName(String name){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.Name, name);
        editor.apply();
    }

    /**
     * Get user's name
     */
    public String getName(){
        return sharedpreferences.getString(Constants.Name, "");
    }

    /**
     * Save user's registered phone number
     * @param phone registered mobile number
     */
    public void savePhoneNo(String phone){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.Phone, phone);
        editor.apply();
    }

    /**
     * Get user's registered mobile number
     * @return String phone number
     */
    public String getPhone(){
        return sharedpreferences.getString(Constants.Phone, "");
    }

    /**
     * Save user's email address
     * @param Email Store user's email address
     */
    public void saveEmail(String Email){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.Name, Email);
        editor.apply();
    }

    /**
     * Get user's email address
     * @return string
     */
    public String getEmail(){
        return sharedpreferences.getString(Constants.Email, "");
    }

    /**
     * Save the timestamps at which user login or registered
     */
    public void saveLoginTimestamp(){
        long currentTimestamp = (new Date().getTime())/1000;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(Constants.LOGIN_TS, currentTimestamp);
        editor.apply();
    }

    /**
     * Get the marker_timestamp when user did login or register
     * @return long
     */
    public long getLoginTimestamp(){
        return sharedpreferences.getLong(Constants.LOGIN_TS, 0);
    }

    /**
     * Get Device ID
     */
    public String getDeviceID(){
         String device_id = sharedpreferences.getString(Constants.DEV_ID, "");
         if (device_id.isEmpty()) {
             device_id = Settings.Secure.getString(context.getContentResolver(),
                     Settings.Secure.ANDROID_ID);
        }
         return device_id;
    }

    public void saveUserID(String id) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.USER_ID, id);
        editor.apply();
    }

    public String getUserID(){
        return sharedpreferences.getString(Constants.USER_ID, "");
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.TOKEN, token);
        editor.apply();
    }

    public String getToken(){
        return sharedpreferences.getString(Constants.TOKEN, "");
    }

    /**
     * Save the registered profile photo
     */
    public void saveProfilePhoto(final String filePath){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.PHOTOS_KEY,filePath);
        editor.apply();
    }
    /**
     * Get the file path as string
     * @return String
     */
    public String getProfilePhoto(){
        return sharedpreferences.getString(Constants.PHOTOS_KEY,"default");
    }

//    public String getAddr(String device_id){
//        return sharedpreferences.getString(device_id+".addr", "");
//    }
    /**
     * Clear the value of all devices
     */
    public void clear(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
