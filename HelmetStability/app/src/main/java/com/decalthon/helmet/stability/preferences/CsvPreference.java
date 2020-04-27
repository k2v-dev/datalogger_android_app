package com.decalthon.helmet.stability.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.decalthon.helmet.stability.Utilities.Constants;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvPreference {
    private static final String TAG = CsvPreference.class.getSimpleName();
    private static CsvPreference single_instance = null;


    private SharedPreferences sharedpreferences;

    /**
     * Private constructor
     * @param context Application context for sharedpreference object
     */
    private CsvPreference(Context context) {
        sharedpreferences = context.getSharedPreferences(Constants.CsvPREFERENCES, Context.MODE_PRIVATE);
    }

    // Allow to create only one object
    public static CsvPreference getInstance(Context context) {
        if (single_instance == null) {
            single_instance = new CsvPreference(context);
        }
        return single_instance;
    }

    public void addSessionId(long session_id){
        Set<Long> sessionSet = getSessionIds();
        sessionSet.add(session_id);
        saveArray(sessionSet);
    }

    public void removeSessionId(long session_id){
        Set<Long> sessionSet = getSessionIds();
        sessionSet.remove(session_id);
        saveArray(sessionSet);
    }

    private void saveArray(Set<Long> setInt){
        Set<String> setStr = new HashSet<>();
        for (Long session_id: setInt
        ) {
            setStr.add(session_id+"");
        }
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putStringSet(TAG, setStr);
        editor.apply();
    }

    public Set<Long> getSessionIds(){

        Set<String> setStr = sharedpreferences.getStringSet(TAG, null);
        if(setStr == null){
            setStr = new HashSet<>();
        }
        Set<Long> setInt = new HashSet<>();
        for (String str: setStr) {
            setInt.add(Long.parseLong(str));
        }
        return setInt;
    }


}
