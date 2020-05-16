package com.decalthon.helmet.stability.model.devicemodels.session;

import android.content.Context;

import com.decalthon.helmet.stability.database.entities.SessionSummary;
import com.decalthon.helmet.stability.utilities.Common;
import com.decalthon.helmet.stability.model.devicemodels.DeviceHelper;
import com.decalthon.helmet.stability.preferences.UserPreferences;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.decalthon.helmet.stability.utilities.FileUtilities.createDirIfNotExists;

public class SessionFile {
    private static final String LOG_DIR = "HEX_FILES";

    private Context context;
    private  String dateFilePattern = "dd-MM-yyyy_HH_mm";
    private  SimpleDateFormat dateFileFormat = new SimpleDateFormat(dateFilePattern);

    private static SessionFile sessionFile = null;
    private static Map<Integer, String> BIN_FILES = new HashMap<>();

    /**
     * Private constructor
     */
    private SessionFile(Context context) {
        this.context = context;
    }

    // Allow to create only one object
    public static SessionFile getInstance(Context context) {
        if (sessionFile == null) {
            sessionFile = new SessionFile(context);
        }
        return sessionFile;
    }

    /**
     * Write hexdata to file
     * @param session_num
     * @param rcvd_data byte array
     */
    public void writeHexData(int session_num, byte[] rcvd_data){
        try{
            String filename = "";
            // Check whether SessionFile is already created or not
            if(BIN_FILES.containsKey(session_num)){
                filename = BIN_FILES.get(session_num);
            }else{//if file doesnot exists, create new one
                //file name will be base user phone number and session date
                filename = UserPreferences.getInstance(this.context).getPhone() ;
                SessionSummary sessionSummary = DeviceHelper.SESSION_SUMMARIES.get(session_num);
                if(filename.isEmpty()){
                    filename += dateFileFormat.format(sessionSummary.getDate())+".hex";
                }else{
                    filename += "_"+dateFileFormat.format(sessionSummary.getDate())+".hex";
                }

                BIN_FILES.put(session_num, filename);
            }
            // Create directory for app
            String path = File.separator + context.getPackageName() + File.separator  + LOG_DIR + File.separator ;
            File root = createDirIfNotExists(path);
            if (root==null)
            {
                return;
            }

            File hexFile = new File(root, filename);
            FileWriter writer = new FileWriter(hexFile,true);
            // Write hex data to file without space between two byte and will write on new line
            String hexString = Common.convertByteArrToStr(rcvd_data, false);

            writer.append(hexString);
            writer.flush();
            writer.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
