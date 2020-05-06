package com.decalthon.helmet.stability.Utilities;

import com.decalthon.helmet.stability.model.DeviceModels.DeviceDetails;
import com.decalthon.helmet.stability.model.DeviceModels.HelmetData;
import com.decalthon.helmet.stability.model.DeviceModels.SensoryWatch;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {
//==============================================
    //THESE ARE HELMET STABILITY CONSTANTS
//==============================================

    // UserPreferences key string
    public static final String MyPREFERENCES = "com.decalthon.user.values";
    public static final String Name = "nameKey";
    public static final String Phone = "phoneKey";
    public static final String Email = "emailKey";
    public static final String LOGIN_TS = "loginTSKey";
    public static final String DEV_ID = "deviceIdentification";
    public static final String USER_ID = "userIdentifier";
    public static final String TOKEN = "userToken";
    //ProfilePreference key string
    public static final String Weight = "user.weight";
    public static final String Height = "user.height";
    public static final String Gender = "user.gender";
    public static final String DOB = "user.dob";
    public static final String PHOTOS_KEY = "profilePhoto";

    // Fragment's name
    public static final String HOME_FRAGMENT = "DeviceFragment";
    public static final String LOGIN_FRAGMENT = "LoginFragment";
    public static final String REGISTRATION_FRAGMENT = "RegistrationFormFragment";
    public static final int MAX_SESSIONS_COUNT = 7;
    public static final int MAX_SESSION_CARD_LINES = 7;



    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static final int REQUEST_PERMISSIONS_LOG_STORAGE = 1003;


//=============================================================
    //THESE ARE BLUETOOTH LE AND THERMAL COMFORT CONSTANTS
//=============================================================
    public static final short RECONNECTION_TEST = 2 ;
    public static final short TOTAL_SCAN_TIME = 3000 ; // 60 second scan whether all 3 device are connected or not if not reconnect it.
    public static boolean isStart = false;
    public static final int REQUEST_ENABLE_BT = 3;
    public static final int NUM_DEVICE = 3;
    public static final int DEV1_NUM_SENSORS = 8;
    public static final int BATTERY_LEVEL_INTERVAL = 30000;
    public static final String DEVICE_ID = "DeviceID";
    public static Map<String, DeviceDetails> DEVICE_MAPS = new HashMap<>();
    public static Map<String, List<String>> SENSORS_MAPS = new HashMap<>();
    public static Map<Integer, SensoryWatch> sensoryWatchMap = new HashMap<>();
    public static Map<String, String> ADDR_ID_MAPS = new HashMap<>();

    public static final String Dev1_type = "HELMET MAC";
    public static final String Dev2_type = "ButtonBox MAC";
    public static final String Dev3_type = "HEARTBELT MAC";

    public static final String Dev1_name = "HELMET NAME";
    public static final String Dev2_name = "ButtonBox NAME";
    public static final String Dev3_name = "HEARTBELT NAME";

    public static final String SHARED_PREFERENCES = "ThermalComfortPrefs" ;

    // For oblu device only
    //For output data rate of 1000Hz:    0x40, 0x01, 0x00, 0x41
    //For output data rate of 500Hz:     0x40, 0x02, 0x00, 0x42
    //For output data rate of 250Hz:     0x40, 0x03, 0x00, 0x43
    //For output data rate of 125Hz:     0x40, 0x04, 0x00, 0x44
    //For output data rate of 62.5Hz:    0x40, 0x05, 0x00, 0x45
    //For output data rate of 31.25Hz:   0x40, 0x06, 0x00, 0x46
    //For output data rate of 15.625Hz:  0x40, 0x07, 0x00, 0x47
    //For output data rate of 7.8125Hz:  0x40, 0x08, 0x00, 0x48
    //For output data rate of 3.90625Hz: 0x40, 0x09, 0x00, 0x49
    public static final String PREC_IMU_CMD = "0x40 0x09 0x00 0x49";
    public static final String PRO_OFF = "0x32 0x00 0x32";         //stop all processing
    public static final String SYS_OFF = "0x22 0x00 0x22";         //stop all output

    //Send Notification Command
    public static final String SEND_NOTIF_CMD = "0x08 0x00 0x00 0x08";
    public static final String STOP_CMD = "0x09 0x00 0x00 0x09";
    public static final String STOP_ACT_CMD = "0x07 0x00 0x00 0x07";
    public static final String MEM_USAGE_CMD = "0x12 0x00 0x00 0x12";
    public static final String ERASE_ALL = "0x05 0x00 0x00 0x05";

    //01 pl1 pl2 pl3 pl4 pl5 pl6 cs1 cs2
    public static final String SESSION_CMD = "0x01 0x32 0x00 0x00 0x00 0x00 0x01 0x00 0x34";

    public static HelmetData HELMET_DATA = new HelmetData();

    public static final List<String> HEL_SENSORS = new ArrayList<String>();
    public static final List<String> WAT_SENSORS = new ArrayList<String>();
    public static final List<String> BELT_SENSORS = new ArrayList<String>();
    public static String[] monthsThreeLetter ={ "Jan", "Feb", "Mar" , "Apr" , "May", "Jun", "Jul","Aug"
    ,"Sep","Oct","Nov","Dec"};


    public static final String  DevPREFERENCES = "com.decathlon.user.devices";
    public static final String  CsvPREFERENCES = "com.decathlon.user.csv.file";
    public static BiMap<String, Integer> ActivityCodeMap = HashBiMap.create();
    public static final String INDOOR = "Indoor";
    public static final String OUTDOOR = "Outdoor";
    public static final String dateFormatString = "MMM dd YYYY HH:MM:SS EEE";
    public static final int typesOfData = 38;
    public static int INDOOR_REGULAR_TIMESTAMPS_INTERVAL = 50;
//    static {
//        DEVICE_MAPS.put(R.string.device1_tv, new DeviceDetails());
//        DEVICE_MAPS.put("Device 2", new DeviceDetails());
//        DEVICE_MAPS.put("Device 3", new DeviceDetails());
//    }

    static {


        // Helmet sesnsors lists
        HEL_SENSORS.add("HELMET_SENSORS"); //Helmet_Sensors
        // Watch sensors list
        WAT_SENSORS.add("DECA_WATCH"); // Deca_Watch
        WAT_SENSORS.add("SENSORY_WATCH");
        // BELT sensors list
        BELT_SENSORS.add("Ble HRM"); // BLE HRM
        BELT_SENSORS.add("DUAL HR"); // Dual HR

 //       French to English meaning
//        Neutre == Neutral
//        Sec == Dry
//        Chaud == Hot
//        Froid == Cold
//        Très == very
//        Humide == wet

        // Mapping two byte information, which received from device, to french/english words
        sensoryWatchMap.put(0x0000, new SensoryWatch((short)0, (short)0, "Neutre", "Sec", "Neutral dry")); // Neutral dry
        sensoryWatchMap.put(0x0001, new SensoryWatch((short)1, (short)0, "Chaud", "Sec", "Hot dry"));  // Hot dry
        sensoryWatchMap.put(0x0002, new SensoryWatch((short)-1, (short)0, "Froid", "Sec", "Cold dry")); // Cold dry
        sensoryWatchMap.put(0x0010, new SensoryWatch((short)0, (short)0, "Neutre", "Sec", "Neutral dry")); // Neutral dry
        sensoryWatchMap.put(0x0011, new SensoryWatch((short)2, (short)0, "Très chaud", "Sec", "Very hot dry"));// Very hot dry
        sensoryWatchMap.put(0x0012, new SensoryWatch((short)-2, (short)0, "Très froid", "Sec", "Very cold dry")); // Very cold dry
        sensoryWatchMap.put(0x0100, new SensoryWatch((short)0, (short)1, "Neutre", "Humide", "Neutral wet")); // Neutral wet
        sensoryWatchMap.put(0x0101, new SensoryWatch((short)1, (short)1, "Chaud", "Humide", "Hot wet")); // Hot wet
        sensoryWatchMap.put(0x0102, new SensoryWatch((short)-1, (short)1, "Froid", "Humide", "Cold wet")); // Cold wet
        sensoryWatchMap.put(0x0110, new SensoryWatch((short)0, (short)1, "Neutre", "Humide", "Neutral wet")); // Neutral wet
        sensoryWatchMap.put(0x0111, new SensoryWatch((short)2, (short)1, "Très chaud", "Humide", "Very hot wet")); // Very hot wet
        sensoryWatchMap.put(0x0112, new SensoryWatch((short)-2, (short)1, "Très froid", "Humide", "Very cold wet")); // Very cold wet
        sensoryWatchMap.put(0x1000, new SensoryWatch((short)0, (short)0, "Neutre", "Sec", "Neutral dry")); // Neutral dry
        sensoryWatchMap.put(0x1001, new SensoryWatch((short)1, (short)0, "Chaud", "Sec", "Hot dry")); // Hot dry
        sensoryWatchMap.put(0x1002, new SensoryWatch((short)-1, (short)0, "Froid", "Sec", "Cold dry")); // Cold dry
        sensoryWatchMap.put(0x1010, new SensoryWatch((short)0, (short)0, "Neutre", "Sec", "Neutral dry")); // Neutral dry
        sensoryWatchMap.put(0x1011, new SensoryWatch((short)2, (short)0, "Très chaud", "Sec", "Very hot dry")); // Very hot dry
        sensoryWatchMap.put(0x1012, new SensoryWatch((short)-2, (short)0, "Très froid", "Sec", "Very hot dry")); // Very hot dry
        sensoryWatchMap.put(0x1100, new SensoryWatch((short)0, (short)2, "Neutre", "Très humide", "Neutral very wet")); // Neutral very wet
        sensoryWatchMap.put(0x1101, new SensoryWatch((short)1, (short)2, "Chaud", "Très humide", "Hot very wet")); // Hot very wet
        sensoryWatchMap.put(0x1102, new SensoryWatch((short)-1, (short)2, "Froid", "Très humide", "Cold very hot")); // Cold very hot
        sensoryWatchMap.put(0x1110, new SensoryWatch((short)0, (short)2, "Neutre", "Très humide", "Neutral hot wet")); // Neutral hot wet
        sensoryWatchMap.put(0x1111, new SensoryWatch((short)2, (short)2, "Très chaud", "Très humide", "Very hot very wet")); // Very hot very wet
        sensoryWatchMap.put(0x1112, new SensoryWatch((short)-2, (short)2, "Très froid", "Très humide", "Very cold very wet")); // Very cold very wet

    }

//=============================================
    //THESE ARE MAP APP SPECIFIC CONSTANTS
//=============================================
    public static final float ZOOM_PREFS_MAX = 18;
    public static final float ZOOM_STREET = 15;
    public static final float ZOOM_BUILDING = 20;
    public static final float ZOOM_EARTH = 0;
    public static final double POLYLINE_DISTANCE = 350.0;
    public static final int DATA_LIST_SIZE = 60000;
    public static final int TEST_SAMPLE_SIZE = 60000;
    public static final int TAB_LAYOUT_SIZE = 10;
    public static final float POINTS_MAX = DATA_LIST_SIZE/1000;
    public static final float POINTS_PER_SCALE = DATA_LIST_SIZE/10000;
    public static final int GRAPH_COUNT = 5;


    public static final String MAP_NAME = "MAP";
    public static final String DEV1_NINE_AXES_NAME = "9-AXIS DEV1";
    public static final String DEV1_THREE_AXES_NAME = "3-AXIS DEV1";
    public static final String DEV2_NINE_AXES_NAME = "9-AXIS DEV2";
    public static final String DEV2_THREE_AXES_NAME = "3-AXIS DEV2";
    public static final String GPS_SPEED_NAME = "GPS SPEED";
    public static final String STEP_COUNT_NAME = "STEP COUNT";
    public static final String SAMPLE_CHART1_NAME = "SAMPLE CHART 1";
    public static final String SAMPLE_CHART2_NAME = "SAMPLE CHART 2";

    public static final float UPDATER_STATIC = 0.06f/POINTS_PER_SCALE;

    public static final int INTER_MARKER_COUNT = 1;
    public static final float CM_PER_INCH = 2.54f;

    public static final String FRAGMENT_NAME_DEVICE1_9_AXIS = "FRAGMENT_DEVICE_1_9_AXIS";
    public static final String FRAGMENT_NAME_DEVICE1_3_AXIS = "FRAGMENT_DEVICE_1_3_AXIS";
    public static final String FRAGMENT_NAME_DEVICE2_9_AXIS = "FRAGMENT DEVICE_2_9_AXIS";
    public static final String FRAGMENT_NAME_DEVICE2_3_AXIS = "FRAGMENT DEVICE_2_3_AXIS";
    public static final String FRAGMENT_NAME_GPS_SPEED = "FRAGMENT_GPS_SPEED";
    public static final String UPLOAD_API_KEY  = "AIzaSyD47XeJv-_Q31QHekEftRrEmd3Zu8xYrHE";
    public static final String CSV_LOG_DIR = "LOG_FILES";
    public static final String CHART_LOG_DIR = "CHART_FILES";
//    public static final String FRAGMENT_NAME_STEP_COUNT = "FRAGMENT_STEP_COUNT";
//    public static final String FRAGMENT_NAME_CHART1 = "FRAGMENT_SAMPLE_CHART1";
//    public static final String FRAGMENT_NAME_CHART2 = "FRAGMENT_SAMPLE_CHART2";
//    public static final String AFTER_MAP_STATE = "AFTER MAP";

    public static Map<Integer,String> TAB_ID_MAPS = new HashMap<>();
    static{
        TAB_ID_MAPS.put(0,FRAGMENT_NAME_DEVICE1_9_AXIS);
        TAB_ID_MAPS.put(1,FRAGMENT_NAME_DEVICE1_3_AXIS);
        TAB_ID_MAPS.put(2,FRAGMENT_NAME_DEVICE2_9_AXIS);
        TAB_ID_MAPS.put(3,FRAGMENT_NAME_DEVICE2_3_AXIS);
        TAB_ID_MAPS.put(4,FRAGMENT_NAME_GPS_SPEED);
    }
    public static Map<String, Boolean> TAB_MAP = new HashMap<>();
    static {
        TAB_MAP.put(FRAGMENT_NAME_DEVICE1_9_AXIS, false);
        TAB_MAP.put(FRAGMENT_NAME_DEVICE1_3_AXIS, false);
        TAB_MAP.put(FRAGMENT_NAME_DEVICE2_9_AXIS, false);
        TAB_MAP.put(FRAGMENT_NAME_DEVICE2_3_AXIS, false);
        TAB_MAP.put(FRAGMENT_NAME_GPS_SPEED, false);
    }

//    public enum TAB_ID_MAPS {
//        FRAGMENT_NAME_DEVICE1_9_AXIS,
//        FRAGMENT_NAME_DEVICE1_3_AXIS,

//        FRAGMENT_NAME_GPS_SPEED
//    }

    // SCALE FACTOR
    public static final float ACC_9axis_SF = 0.00718092f;
    public static final float GYR_9axis_SF = 0.07f;
    public static final float MAG_9axis_SF = 0.58f;

    public static final float ACC_3axis_SF = 7.6518f;

    // Consumer Thread
    public static final int WAIT_FOR_DATA = 60;

    // Firestore
    public static final String PHOTO_DIR = "photos";
    public static final String PROFILE_PIC = "profile.png";
    public static final String UPLOAD_DIR = "uploads";
    public static final String USER_COLLECTION = "USERS";
    public static final String PROFILE_COLLECTION = "PROFILES";

    public static class UserFields{
        public static final String EMAIL = "email";
        public static final String PHONE_NO = "phone_no";
        public static final String DEVICE_ID = "dev_id";
    }

    public static class ProfileFields{
        public static final String USERNAME = "name";
        public static final String HEIGHT = "ht";
        public static final String WEIGHT = "wt";
        public static final String DOB = "dob";
        public static final String GENDER = "gender";
    }

    //DB
    public static Map<Integer, String> MARKER_MAPS = new HashMap<>();
    static {
        MARKER_MAPS.put(0, "Start");
        MARKER_MAPS.put(1, "Poor");
        MARKER_MAPS.put(2, "Very Poor");
        MARKER_MAPS.put(4, "No Opinion");
        MARKER_MAPS.put(8, "Good");
        MARKER_MAPS.put(16, "Very Good");
        MARKER_MAPS.put(255, "Stop");
    }


//    if BSP_BOARD_BUTTON_1 f_Poor 1
//            if BSP_BOARD_BUTTON_1 f_Very_Poor 2
//            if BSP_BOARD_BUTTON_2 f_No_Opnion 4
//            if BSP_BOARD_BUTTON_3 f_Good 8
//            if BSP_BOARD_BUTTON_3 f_Very_Good 16
}
