package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.decalthon.helmet.stability.utilities.Constants;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * Save user's profile information to local memory
 */
public class ProfilePreferences {

    private static ProfilePreferences single_instance = null;

    private SharedPreferences sharedpreferences;
    private Context context;

    /**
     * Private constructor
     * @param context Application context for sharepreference object
     */
    private ProfilePreferences(Context context) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    // Allow to create only one object
    public static ProfilePreferences getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new ProfilePreferences(context);
        }
        return single_instance;
    }

    /**
     * Save user's height information
     * @param height in cm
     */
    public void saveHeight(float height){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(Constants.Height, height);
        editor.apply();
    }

    /**
     * Get user's height if saved
     * @return height
     */
    public float getHeight(){
        return sharedpreferences.getFloat(Constants.Height, 0.0f);
    }

    /**
     * Save user's weight information
     * @param weight in kg
     */
    public void saveWeight(float weight){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(Constants.Weight, weight);
        editor.apply();
    }

    /**
     * Get user's weight
     * @return weight
     */
    public float getWeight(){
        return sharedpreferences.getFloat(Constants.Weight, 0.0f);
    }

    /**
     * Save user's gender info
     * @param gender Male or Female or Other
     */
    public void saveGender(String gender){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Constants.Gender, String.valueOf(gender).toUpperCase());
        editor.apply();
    }

    /**
     * Get user's gender info
     * @return gender
     */
    public char getGender(){
        String gender = sharedpreferences.getString(Constants.Gender , "M");
        return gender.charAt(0);
    }

    /**
     * Save user's dob
     * @param timestamp
     */
    public void saveDob(long timestamp){
        try{
//            Date date =  Constants.dateFormat.parse(dob);
//            long timestamp = date.getTime(); // get marker_timestamp in millisecond

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.DOB, timestamp);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Get user's dob if saved
     * @return user's dob
     */
    public Date getDob() {
        long ts = sharedpreferences.getLong(Constants.DOB, new Date().getTime());
        Timestamp timestamp = new Timestamp(ts*1000);
        Date date=new Date(timestamp.getTime());
        return date;
    }

    /**
     * Get user's dob if saved
     * @return user's dob
     */
    public Integer getAge() {
        long ts = sharedpreferences.getLong(Constants.DOB,  new Date().getTime());

       // Timestamp timestamp = new Timestamp(ts*1000);
        Date date=new Date(ts*(1000L));

        Calendar dob = Calendar.getInstance();
        dob.setTime(date);
        Calendar today = Calendar.getInstance();


        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        return ageInt;
    }

    public boolean isEmpty(){
        ProfilePreferences profilePreferences = ProfilePreferences.getInstance(context);
        float ht = profilePreferences.getHeight();
        float wt = profilePreferences.getWeight();
        int age = profilePreferences.getAge();
        if ( ht < 59f || wt < 29f || age < 5) {
            return true;
        }
        return false;
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
