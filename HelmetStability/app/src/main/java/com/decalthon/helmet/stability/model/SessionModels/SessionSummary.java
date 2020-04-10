package com.decalthon.helmet.stability.model.SessionModels;

public class SessionSummary {
    String sessionName;
    String activityType;
    String recordedData;
    long dataQuantity;

    public SessionSummary(String sessionName,String activityType
            ,String recordedData, long dataQuantity){
        this.sessionName = sessionName;
        this.activityType = activityType;
        this.recordedData = recordedData;
        this.dataQuantity = dataQuantity;
    }

    public String getSessionName(){
        return this.sessionName;
    }

    public String getActivityType(){
        return this.activityType;
    }

    public String getRecordedData(){
        return this.recordedData;
    }

    public long getDataQuantity(){
        return this.dataQuantity;
    }
}
