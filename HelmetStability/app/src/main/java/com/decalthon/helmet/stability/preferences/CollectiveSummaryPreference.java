package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.firestore.entities.impl.CollectiveSummaryImpl;
import com.decalthon.helmet.stability.utilities.Constants;
import com.decalthon.helmet.stability.webservice.requests.CollectiveSummaryReq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectiveSummaryPreference {
    private static final String TAG = CollectiveSummaryPreference.class.getSimpleName();
    private SharedPreferences sharedpreferences;
    private Context mContext;
    /**
     * Private constructor
     * @param context Application context for sharedpreference object
     */
    public CollectiveSummaryPreference(Context context) {
        mContext = context;
        sharedpreferences = context.getSharedPreferences(Constants.CollSumPREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * Get total number of duration
     * @return gender
     */
    public long getNumSessions(){
        return sharedpreferences.getLong(Constants.TOTAL_SESSIONS , 0);
    }

    /**
     * Increment total number of session
     */
    public void incrementSessions(){
        try{
            Long num_sessions = getNumSessions() + 1;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_SESSIONS, num_sessions);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Save total number of session
     */
    public void setTotSession(Long num_sessions){
        try{
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_SESSIONS, num_sessions);
            editor.apply();
        }catch (Exception ex){

        }
    }


    /**
     * Get total durations in seconds
     * @return gender
     */
    public long getDurations(){
        return sharedpreferences.getLong(Constants.TOTAL_DURATION , 0);
    }

    /**
     * Add time(sec) to total duration
     * @param time
     */
    public void addDuration(long time){
        try{
            Long total_duration = getDurations() + time;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_DURATION, total_duration);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Set time(sec) to total duration
     * @param time
     */
    public void setTotDuration(long time){
        try{
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_DURATION, time);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Get total durations in seconds
     * @return gender
     */
    public long getTotalSize(){
        return sharedpreferences.getLong(Constants.TOTAL_SIZE , 0);
    }

    /**
     * Add time(sec) to total duration
     * @param total_bytes
     */
    public void addSize(long total_bytes){
        try{
            Long total_size = getTotalSize() + total_bytes;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_SIZE, total_size);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Add time(sec) to total duration
     * @param total_size
     */
    public void setTotSize(long total_size){
        try{

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putLong(Constants.TOTAL_SIZE, total_size);
            editor.apply();
        }catch (Exception ex){

        }
    }

    /**
     * Get total durations in seconds
     * @return gender
     */
    public Set<Long> getActCodes(){
        Set<String> act_code_strs =  sharedpreferences.getStringSet(Constants.ACT_TYPES , new HashSet<String>());

        Set<Long> act_codes = new HashSet<>();
        try{
            for (String code: act_code_strs) {
                act_codes.add(Long.parseLong(code));
            }
        }catch (Exception ex){
            Log.d(TAG, "error="+ex.getMessage());
        }

        return  act_codes;
    }

    /**
     * Add activity code to total duration
     * @param act_code
     */
    public void addActCode(long act_code){
        try{
            Set<Long> actCodes = getActCodes();
            actCodes.add(act_code);
            Set<String> act_code_strs = new HashSet<>();
            try{
                for (Long code: actCodes) {
                    act_code_strs.add(code.toString());
                }
            }catch (Exception ex){
                Log.d(TAG, "error="+ex.getMessage());
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet(Constants.ACT_TYPES, act_code_strs);
            editor.apply();
        }catch (Exception ex){
            Log.d(TAG, "error="+ex.getMessage());
        }
    }


    /**
     * Save activities code
     * @param act_code_ls
     */
    public void setActCodes(List<Long> act_code_ls){
        try{
            Set<String> act_code_strs = new HashSet<>();
            try{
                for (Long code: act_code_ls) {
                    act_code_strs.add(code.toString());
                }
            }catch (Exception ex){
                Log.d(TAG, "error="+ex.getMessage());
            }
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet(Constants.ACT_TYPES, act_code_strs);
            editor.apply();
        }catch (Exception ex){
            Log.d(TAG, "error="+ex.getMessage());
        }
    }

    public List<Float>  getCollSum(){
        List<Float> collective_summary_info = new ArrayList<>(4);
        collective_summary_info.add((float)getNumSessions());
        collective_summary_info.add( (float)getDurations());
        collective_summary_info.add((float)getTotalSize());
        collective_summary_info.add((float)getActCodes().size());
        return collective_summary_info;
    }

    public void updateAndUpload(SessionSummary sessionSummary){
        incrementSessions();
        addDuration((long)sessionSummary.getDuration());
        addActCode(sessionSummary.getActivity_type());
        addSize(sessionSummary.getSize());

        CollectiveSummaryReq collectiveSummaryReq = new CollectiveSummaryReq();
        collectiveSummaryReq.total_duration =  getDurations();
        collectiveSummaryReq.total_size =  getTotalSize();
        collectiveSummaryReq.total_sessions =  getNumSessions();
        collectiveSummaryReq.activity_types = new ArrayList<>(getActCodes());
        String userid = UserPreferences.getInstance(mContext).getUserID();
        new CollectiveSummaryImpl(mContext).updateUserData(userid, collectiveSummaryReq);
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
