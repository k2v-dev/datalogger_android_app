package com.decalthon.helmet.stability.model.devicemodels;


// Use for passing connection state info between broadcast receiver to Home page via EventBus api
public class BLEConnectionState {
    public boolean connectionstate;
    public String device_id;

    public BLEConnectionState(String device_id, boolean connectionstate) {
        this.connectionstate = connectionstate;
        this.device_id = device_id;
    }
}
