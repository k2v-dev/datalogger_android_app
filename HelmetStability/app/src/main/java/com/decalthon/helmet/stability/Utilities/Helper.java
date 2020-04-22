package com.decalthon.helmet.stability.Utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Locale;

public class Helper {


    /**
     * Hide the keyboard
     * @param activity Current activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    // Not working , if you call below in more than one fragment.
    /**
     * check all view whether edittext or not, if click on non-edittext then hide the keyboard
     * @param activity current activity
     * @param view parent/top view
     */
    public static void setupUI(Activity activity, View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Helper.hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                final View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(activity, innerView);
            }
        }
    }

    /**
     * Email validation method
     * @param target email string
     * @return true on valid email address otherwise false
     */
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    /**
     * check the internet's availability
     * @param context
     * @return true if connected to Internet
     *
     */
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return (networkInfo != null && networkInfo.isConnected());
    }

    // InputStream -> File
    public static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            // commons-io
            //IOUtils.copy(inputStream, outputStream);

        }

    }

    public static long compareTimestamps(long timestamp1, long timestamp2){
        return Math.abs((timestamp2 - timestamp1)/(60000));
    }

    //convert two byte to integer
    public static int getIntValue(byte b1, byte b2){
        int val1 = b1 & 0xFF ;
        int val2 = b2 & 0xFF ;
        return val1*256 + val2;
    }

    //convert two byte to short integer
    public static short getShortValue(byte b1, byte b2){
        byte[] temp = {b1, b2};
        return ByteBuffer.wrap(temp).getShort();
    }

    public static void format3(StringBuilder builder, float d) {
        if (d < 0) {
            builder.append('-');
            d = -d;
        }
        if (d * 1e3 + 0.5 > Long.MAX_VALUE) {
            // TODO write a fall back.
            throw new IllegalArgumentException("number too large");
        }
        long scaled = (long) (d * 1e3 + 0.5);
        long factor = 1000;
        int scale = 4;
        long scaled2 = scaled / 10;
        while (factor <= scaled2) {
            factor *= 10;
            scale++;
        }
        while (scale > 0) {
            if (scale == 3)
                builder.append('.');
            long c = scaled / factor % 10;
            factor /= 10;
            builder.append((char) ('0' + c));
            scale--;
        }
    }

    public static void format6(StringBuilder builder, float d) {
        if (d < 0) {
            builder.append('-');
            d = -d;
        }
        if (d * 1e6 + 0.5 > Long.MAX_VALUE) {
            // TODO write a fall back.
            throw new IllegalArgumentException("number too large");
        }
        long scaled = (long) (d * 1e6 + 0.5);
        long factor = 1000000;
        int scale = 7;
        long scaled2 = scaled / 10;
        while (factor <= scaled2) {
            factor *= 10;
            scale++;
        }
        while (scale > 0) {
            if (scale == 6)
                builder.append('.');
            long c = scaled / factor % 10;
            factor /= 10;
            builder.append((char) ('0' + c));
            scale--;
        }
    }

    public static void format6_new(StringBuilder sb, float val){
        if(val < 0){
            sb.append("-");
            val = -val;
        }
        float rounded = (float)Math.floor(1000000 * val + 0.5) / 1000000;
        sb.append(rounded);
    }

    public static void format3_old(StringBuilder sb, float val){
        if(val < 0){
            sb.append("-");
            val = -val;
        }
        float rounded = (float)Math.floor(1000 * val + 0.5) / 1000;
        sb.append(rounded);
    }



    public static void test_format(){
        float val = 1234.234933f;

        StringBuilder sb= new StringBuilder("");
        long t1 = System.currentTimeMillis();
        for(int i =0; i< 100;i++){
            format3_old(sb, val);
        }

        long t2 = System.currentTimeMillis();
        System.out.println("format3="+(t2-t1));
        sb = new StringBuilder("");
        for(int i =0; i< 100;i++){
            format6(sb, val);
        }

        long t22 = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.println("format6="+(t22-t2));
        sb = new StringBuilder("");
        for(int i =0; i< 100;i++){
            sb.append(df.format(val));
        }

        long t3 = System.currentTimeMillis();
        System.out.println("DecimalFormat="+(t3-t22));
        sb = new StringBuilder("");
        for(int i =0; i< 100;i++){
            sb.append(String.format(Locale.getDefault(),"%5.3f", val));
        }
        long t4 = System.currentTimeMillis();
        System.out.println("String="+(t4-t3));
        for(int i =0; i< 100;i++){
            format3(sb, val);
        }

        long t5 = System.currentTimeMillis();
        System.out.println("format3_new="+(t5-t4));
        sb = new StringBuilder("");
        for(int i =0; i< 100;i++){
            format6_new(sb, val);
        }

        long t6 = System.currentTimeMillis();
        System.out.println("format6_new="+(t6-t5));
        sb = new StringBuilder("");
        for(int i =0; i< 100;i++){
            sb.append(val);
        }
        long t7 = System.currentTimeMillis();
        System.out.println("No api="+(t7-t6));
    }
}
