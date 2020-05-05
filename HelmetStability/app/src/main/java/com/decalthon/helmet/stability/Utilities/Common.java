package com.decalthon.helmet.stability.Utilities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.decalthon.helmet.stability.Activities.MainActivity;
import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.Generic.TimeFmt;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.decalthon.helmet.stability.Utilities.Constants.DEVICE_MAPS;

/*
It has helper methods.
 */

public class Common {

    public static String ACT_TYPE = "";

    // Convert String to bye array e.g. "50 FF" ==> [80, 255]
    public static byte[] convertingTobyteArray(String result) {
        String[] splited = result.split("\\s+");
        byte[] valueByte = new byte[splited.length];
        for (int i = 0; i < splited.length; i++) {
            if (splited[i].length() > 2) {
                String trimmedByte = splited[i].split("x")[1];
                valueByte[i] = (byte) Integer.parseInt(trimmedByte, 16);
            }
        }
        return valueByte;

    }

    public static String convertByteArrToStr(byte[] byteArray, boolean needSpace){
        final StringBuilder stringBuilder = new StringBuilder(byteArray.length);
        String fmt = "%02X";
        if(needSpace){
            fmt = "%02X ";
        }
        for (byte byteChar : byteArray) {
            stringBuilder.append(String.format(fmt, byteChar));
        }
        return stringBuilder.toString();
    }

    // save previous connected devices to local storage
    public static void saveDeviceDetails(Context context){
        SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();

        DeviceDetails deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv));
        editor.putString(Constants.Dev1_name, deviceDetails.name);
        editor.putString(Constants.Dev1_type, deviceDetails.mac_address);

        deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device2_tv));
        editor.putString(Constants.Dev2_name, deviceDetails.name);
        editor.putString(Constants.Dev2_type, deviceDetails.mac_address);

        deviceDetails = Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device3_tv));
        editor.putString(Constants.Dev3_name, deviceDetails.name);
        editor.putString(Constants.Dev3_type, deviceDetails.mac_address);

        editor.commit();
    }


    // load the device details's from local storage and try to reconnect
    public static void load_n_connect_devices_(final Context context){
       Runnable runnable = new Runnable() {
           @Override
           public void run() {
               Common.wait(500);
               SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

               String dev_id_1 = context.getResources().getString(R.string.device1_tv);
               String dev_id_2 = context.getResources().getString(R.string.device2_tv);
               String dev_id_3 = context.getResources().getString(R.string.device3_tv);

               DeviceDetails deviceDetails1 = new DeviceDetails();

               deviceDetails1.mac_address = sharedpreferences.getString(Constants.Dev1_type, null);
               if (deviceDetails1.mac_address != null) {
                   deviceDetails1.name = sharedpreferences.getString(Constants.Dev1_name, context.getResources().getString(R.string.unknown_device));
               }
               Constants.DEVICE_MAPS.put(dev_id_1, deviceDetails1);


               DeviceDetails deviceDetails2 = new DeviceDetails();
               deviceDetails2.mac_address = sharedpreferences.getString(Constants.Dev2_type, null);
               if (deviceDetails2.mac_address != null) {
                   deviceDetails2.name = sharedpreferences.getString(Constants.Dev2_name, context.getResources().getString(R.string.unknown_device));
               }
               Constants.DEVICE_MAPS.put(dev_id_2, deviceDetails2);
               if (!MainActivity.shared().getBleService().createBond(deviceDetails2.mac_address)) {
                   Constants.DEVICE_MAPS.put(dev_id_2, new DeviceDetails());
               }else{
                   Common.wait(300);
                   MainActivity.shared().getBleService().connect(dev_id_2);
               }

               DeviceDetails deviceDetails3 = new DeviceDetails();
               deviceDetails3.mac_address = sharedpreferences.getString(Constants.Dev3_type, null);
               if (deviceDetails3.mac_address != null) {
                   deviceDetails3.name = sharedpreferences.getString(Constants.Dev3_name, context.getResources().getString(R.string.unknown_device));
               }
               Constants.DEVICE_MAPS.put(dev_id_3, deviceDetails3);
//               MapActivity.shared().getBleService().connect(dev_id_3);
//               Common.wait(100);

//               connectAll();
           }
       };

        new Thread(runnable).start();
    }

    public  static double calculateSpeed(Location prevLoc, Location curLoc) {
        double lat1 = prevLoc.getLatitude();
        double lng1 = prevLoc.getLongitude();
        double lat2 = curLoc.getLatitude();
        double lng2 = curLoc.getLongitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distanceInMeters = 6371000 * c;
        double timeDiff = (double)(curLoc.getTime() - prevLoc.getTime());
        System.out.println(curLoc.getTime()+"CURRENT FIX TIME");
        System.out.println(prevLoc.getTime()+"PREVIOUS FIX TIME");
        if (timeDiff < 0.01) {
            return 0.0;
        }
        return distanceInMeters/timeDiff;
    }


    // Connecting to all previous connected device
    public static void connectAll(){
        for(Map.Entry<String, DeviceDetails> entry : Constants.DEVICE_MAPS.entrySet()) {
            if (!entry.getValue().connected) {
                MainActivity.shared().getBleService().connect(entry.getKey());
                Common.wait(500);
            }
        }
    }

    //
    public static void wait(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get last 4 alphanumeric from given mac address
     * @param mac_addr
     * @return String
     */
    public static String getLast4Char(String mac_addr){
        String last4Char = "";

        if (mac_addr.length() > 4)
        {
            last4Char = mac_addr.substring(mac_addr.length() - 5);
            last4Char = last4Char.replace(":", "");
            last4Char = "_"+last4Char;
        }
        return last4Char;
    }

    /**
     * Get timestamp from given age
     * @param age
     * @return long
     */
    public static long getTimestamp(int age){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);
        Date date = calendar.getTime();
        return  date.getTime()/1000;
    }

    /**
     * Show alert dailog for non internet availability
     * @param context
     */
    public static void noInternetAlert(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Alert");
        alert.setMessage(context.getResources().getString(R.string.NO_INTERNET));
        alert.setPositiveButton("OK",null);
        alert.show();

    }

    /**
     * Show alert dialog with ok button only
     * @param context
     * @param message
     */
    public static void okAlertMessage(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Alert");
        alert.setMessage(message);
        alert.setPositiveButton("OK",null);
        alert.show();
    }


    private static ProgressDialog wait_cursor;
    public static void show_wait_bar(Context context, String message){
        dismiss_wait_bar();
        wait_cursor = new ProgressDialog(context);
       // yourProgress.setTitle("Title");
        wait_cursor.setMessage("Wait for a while");
        wait_cursor.getProgress();
        wait_cursor.setCancelable(true);
        wait_cursor.show();
    }

    public static void dismiss_wait_bar(){
        if(wait_cursor != null){
            wait_cursor.dismiss();
            wait_cursor = null;
        }
    }

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static void load_initialization(Context context){
        DEVICE_MAPS.put(context.getResources().getString(R.string.device1_tv), new DeviceDetails());
        DEVICE_MAPS.put(context.getResources().getString(R.string.device2_tv), new DeviceDetails());
        DEVICE_MAPS.put(context.getResources().getString(R.string.device3_tv), new DeviceDetails());

        String[] indoor = context.getResources().getStringArray(R.array.indoor_sports_array);
        int[] indoor_code = context.getResources().getIntArray(R.array.indoor_sports_array_code);
        String[] outdoor = context.getResources().getStringArray(R.array.outdoor_sports_array);
        int[] outdoor_code = context.getResources().getIntArray(R.array.outdoor_sports_array_code);

        for (int i = 0; i < indoor.length; i++) {
            String key = indoor[i]+"_"+Constants.INDOOR;
            Constants.ActivityCodeMap.put(key, indoor_code[i]);
        }

        for (int i = 0; i < outdoor.length; i++) {
            String key = outdoor[i]+"_"+Constants.OUTDOOR;
            Constants.ActivityCodeMap.put(key, outdoor_code[i]);
        }

//        SENSORS_MAPS.put(getResources().getString(R.string.device1_tv), HEL_SENSORS);
//        SENSORS_MAPS.put(getResources().getString(R.string.device2_tv), WAT_SENSORS);
//        SENSORS_MAPS.put(getResources().getString(R.string.device3_tv), BELT_SENSORS);
    }

//    public static void lowStorageAlert(Context context){
//        try{
//            String path = context.getPackageName() + File.separator ;
//            File file = FileUtilities.createDirIfNotExists(path);
//            long mb_avaiable = (new StatFs(file.getAbsolutePath()).getAvailableBytes())/(1024*1024);
//            if(mb_avaiable < 500){
//                okAlertMessage(context, context.getString(R.string.low_storage_alert));
//            }
//            System.out.println("Memory available:"+mb_avaiable);
//        }catch (Exception ex){
//            Log.d("Common","lowStorageAlert:"+ex.getMessage());
//        }
//    }

    /**
     * Convert duration in millsec to time format e.g. hr, min, sec and milli sec
     * @param duration in milli seconds
     * @return
     */
    public static TimeFmt convertToTimeFmt(long duration){
        long hours = TimeUnit.MILLISECONDS.toHours(duration) ;
        long minute = TimeUnit.MILLISECONDS.toMinutes(duration) - (TimeUnit.MILLISECONDS.toHours(duration)* 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(duration) - (TimeUnit.MILLISECONDS.toMinutes(duration) *60);
        long ms = TimeUnit.MILLISECONDS.toMillis(duration) - (TimeUnit.MILLISECONDS.toSeconds(duration) *1000);
        TimeFmt timeFmt = new TimeFmt();
        timeFmt.hr = (int)hours;timeFmt.min = (int)minute;timeFmt.sec = (int)second;timeFmt.milsec = (int)ms;
        return timeFmt;
    }
}


