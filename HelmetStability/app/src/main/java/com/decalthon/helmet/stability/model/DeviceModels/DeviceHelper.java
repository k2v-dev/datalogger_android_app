package com.decalthon.helmet.stability.model.DeviceModels;


import com.decalthon.helmet.stability.model.DeviceModels.session.SessionHeader;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;


import java.util.Map;
import java.util.TreeMap;

public class DeviceHelper {
    // Device1 Helper
    public static Map<Integer, SessionSummary> SESSION_SUMMARIES = new TreeMap<>();
    //public static Map<Integer, SessionHeader> SESSION_HDRS = new TreeMap<>();
    public static SessionHeader REC_SESSION_HDR;

    //ButtonBox Helper
    public static Map<Integer, SessionSummary> SESSION_SUMMARIES_BB = new TreeMap<>();
    //public static Map<Integer, SessionHeader> SESSION_HDRS_BB = new TreeMap<>();
    public static SessionHeader REC_SESSION_HDR_BB;
}
