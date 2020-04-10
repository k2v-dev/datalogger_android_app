package com.decalthon.helmet.stability.Utilities;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.decalthon.helmet.stability.R;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.model.DeviceModels.SensorDataOld;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


// FileUtilies use for creating log file, write header and
public class FileUtilities {
    private static final String LOG_DIR = "Log Files";
    private static final String LOG_FILE_PREFIX = "ConnectedHelmet_";
    private static final String GPS_SPEED_PREFIX = "GpsDetail_";
    private static String LOG_FILE_NAME = null;
    public static final int REQUEST_PERMISSIONS_LOG_STORAGE = 1003;

    /**
     * Create log folder and file and write header to file
     *
     * @param context  application context
     *
     */
    public static void writeSensorHeaderToLog(Context context){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        Date now = new Date();
        LOG_FILE_NAME = LOG_FILE_PREFIX + formatter.format(now) + ".txt";//like LOG_FILE_20181502_130316.txt
        try {
            String path = context.getPackageName() + File.separator + LOG_DIR;
            File root = createDirIfNotExists(path);
            if(root==null){
                LOG_FILE_NAME = null;
                return;
            }else {
                // code to make the files visible on the PC using MTP protocol
                if(root.setExecutable(true)&&root.setReadable(true)&&root.setWritable(true)){
                    Log.i("SET FILE PERMISSION ", "Set read , write and execuatable permission for log file");
                }

                MediaScannerConnection.scanFile(context, new String[]{root.toString()}, null, null);
            }
            File gpxFile = new File(root, LOG_FILE_NAME);

            FileWriter writer = new FileWriter(gpxFile, true);
            //writer.append(" LOG FILE  "+"\n\n");
//            String header=String.format("%11s\t%9s\t%6s\t%6s\t%6s\t%7s\t%6s\n\n",
//                    "TIME STAMP","STEP COUNT","X","Y","Z","Heading","Distance");
            String device = String.format("%15s: %s\n",
                    Constants.Dev1_type, Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device1_tv)).mac_address);
            writer.append(device);
            device = String.format("%15s: %s\n",
                    Constants.Dev2_type, Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device2_tv)).mac_address);
            writer.append(device);
            device = String.format("%15s: %s\n",
                    Constants.Dev3_type, Constants.DEVICE_MAPS.get(context.getResources().getString(R.string.device3_tv)).mac_address);
            writer.append(device);
            writer.append(String.format("%4s;%20s;%s\n","###","Datetime", SensorDataOld.getHeader()));
            writer.flush();
            writer.close();
            //Toast.makeText(get, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("WRITE LOG ERROR ", e.getMessage());
        }
    }

    public static void writeGpsSpeedHeaderToLog(Context context){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        Date now = new Date();
        LOG_FILE_NAME = GPS_SPEED_PREFIX + formatter.format(now) + ".txt";
        try{
            String path = context.getPackageName() + File.separator + LOG_DIR;
            File root = createDirIfNotExists(path);
            if(root==null) {
                LOG_FILE_NAME = null;
                return;
            }
            if(root.setExecutable(true)&&root.setReadable(true)&&root.setWritable(true)){
                Log.i("SET FILE PERMISSION ", "Set read , write and execuatable permission for log file");
            }
            File gpsFile = new File(root, LOG_FILE_NAME);

            FileWriter writer = new FileWriter(gpsFile, true);

            writer.append(String.format("%13s; %s","Time",GpsSpeed.getHeader()));

            writer.flush();
            writer.close();
        }
        catch (Exception e){
            Log.e("WRITE LOG ERROR ", e.getMessage());
        }
    }

    /**
     * To get external file path
     *
     * @param relativePath path relative external sdcard path e.g.
     *
     * @return File external file where log file will be created
     */
    public static File createDirIfNotExists(String relativePath) {
        String testFilePath=null;
        File filePath = null;
        List<String> possibleExternalFilePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard", "sdcard2");

        try{
            for (String sdPath : possibleExternalFilePath) {
                File file = new File("/mnt/", sdPath);

                if (file.isDirectory() && file.canWrite()) {
                    testFilePath = file.getAbsolutePath();

                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date());
                    File checkWritable = new File(testFilePath, "test_" + timeStamp);

                    if (checkWritable.mkdirs()) {
                        if(checkWritable.delete()) {
                            Log.e("FILE ERROR ", "Problem deleting test file");
                        }
                    }
                    else {
                        testFilePath = null;
                    }
                }
            }

            if (testFilePath != null) {
                filePath = new File(testFilePath,relativePath);
            }
            else {
                filePath = new File(Environment.getExternalStorageDirectory(),relativePath);
            }
        }catch(Exception ex){
            filePath = null;
        }


        if (filePath!=null && !filePath.exists()) {
            if (!filePath.mkdirs()) {
                Log.e("FILE ERROR ", "Problem creating app folder");
                return null;
            }
            else {
                Log.i("FILE HANDLING", testFilePath + " Created successfully");
            }
        }
        return filePath;
    }

    public static File getProfileFile(Context context){
        File root = null;
        try {
            String path = context.getPackageName() + File.separator + Constants.PHOTO_DIR;
            root = FileUtilities.createDirIfNotExists(path);
            if(root==null){
                return null;
            }else {
                // code to make the files visible on the PC using MTP protocol
                if(root.setExecutable(true)&&root.setReadable(true)&&root.setWritable(true)){
                    Log.i("SET FILE PERMISSION ", "Set read , write and execuatable permission for log file");
                }

                MediaScannerConnection.scanFile(context, new String[]{root.toString()}, null, null);
            }

        }catch (Exception ex){
            return null;
        }

       return new File(root, Constants.PROFILE_PIC);
    }

    public static void writeGpsSpeedDataToLog(Context context, GpsSpeed gpsSpeed) {
        long gpsSpeedFileWriteTime = System.currentTimeMillis();
        System.out.println("Millisecond time-->"+gpsSpeedFileWriteTime);
        double seconds = gpsSpeedFileWriteTime/1000.0;
        long millisecond = (gpsSpeedFileWriteTime%1000)/10;
        System.out.println("seconds"+seconds+"millisecond"+millisecond);
        try
        {
            if(LOG_FILE_NAME!=null && LOG_FILE_NAME.length() > 0){
                String path = File.separator + context.getPackageName() + File.separator  + LOG_DIR + File.separator ;
                File root = createDirIfNotExists(path);
                if (root==null)
                {
                    return;
                }
                File gpsFile = new File(root, LOG_FILE_NAME);
                FileWriter writer = new FileWriter(gpsFile,true);
                String data = String.format("%13.2f;%s\n",seconds,gpsSpeed.toString());
                writer.append(data);
                writer.flush();
                writer.close();
                //Toast.makeText(get, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
            }
        }
        catch(IOException e)
        {
            Log.e("FILE ERROR","Problem File Writing ");
        }
    }
//


    /**
     * Write step details to log file if exists
     *
     * @param context  application context
     * @param sensorData  step details like stepcounter, X, Y, Z and distance covered
     *
     */
    public static void writeSensorDataToLog(Context context, SensorDataOld sensorData){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH-mm-ss", Locale.US);
        Date now = new Date();
        DecimalFormat df3 = new DecimalFormat("0.00");

        try
        {
            if(LOG_FILE_NAME!=null && LOG_FILE_NAME.length() > 0){
                String path = File.separator + context.getPackageName() + File.separator  + LOG_DIR + File.separator ;
                File root = createDirIfNotExists(path);
                if (root==null)
                {
                    return;
                }
                File gpxFile = new File(root, LOG_FILE_NAME);

                FileWriter writer = new FileWriter(gpxFile,true);
                //writer.append(" LOG FILE  "+"\n\n");"
//                String data = "";
//                String data=String.format("%11s\t%9s\t%6s\t%6s\t%6s\t%6s\t%6s\n", formatter.format(now),stepData.getStepCounter(),
//                        df3.format(stepData.getX()),df3.format(stepData.getY()),df3.format(stepData.getZ()),
//                        df3.format(stepData.getHeading()), df3.format(stepData.getDistance()));

                String data = String.format("%4s;%20s;%s\n", sensorData.connectionStr, formatter.format(now), sensorData.toString());
                writer.append(data);
                writer.flush();
                writer.close();
                //Toast.makeText(get, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
            }
        }
        catch(IOException e)
        {
            Log.e("FILE ERROR","Problem File Writing ");
        }
    }

    public static void writeDataToLog(Context context, GpsSpeed gpsSpeed){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd_HH-mm-ss", Locale.US);
        Date now = new Date();
        DecimalFormat df3 = new DecimalFormat("0.00");
        try
        {
            if(LOG_FILE_NAME!=null && LOG_FILE_NAME.length() > 0){
                String path = File.separator + context.getPackageName() + File.separator  + LOG_DIR + File.separator ;
                File root = createDirIfNotExists(path);
                if (root==null)
                {
                    return;
                }
                File gpxFile = new File(root, LOG_FILE_NAME);

                FileWriter writer = new FileWriter(gpxFile,true);
                //writer.append(" LOG FILE  "+"\n\n");"
//                String data = "";
//                String data=String.format("%11s\t%9s\t%6s\t%6s\t%6s\t%6s\t%6s\n", formatter.format(now),stepData.getStepCounter(),
//                        df3.format(stepData.getX()),df3.format(stepData.getY()),df3.format(stepData.getZ()),
//                        df3.format(stepData.getHeading()), df3.format(stepData.getDistance()));

//                writer.append(data);
                writer.flush();
                writer.close();
                //Toast.makeText(get, "Data has been written to Report File", Toast.LENGTH_SHORT).show();
            }
        }
        catch(IOException e)
        {
            Log.e("FILE ERROR","Problem File Writing ");
        }
    }

    public static void closeFile(){
        LOG_FILE_NAME = null;
    }
}
