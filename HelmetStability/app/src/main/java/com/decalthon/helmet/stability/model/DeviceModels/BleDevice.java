package com.decalthon.helmet.stability.model.devicemodels;

/**
 * Created by gtsilicon
 * // Use for passing BLE device's details to home page for store  in memory which will user later for reconnecting
 */
public class BleDevice {
    private final String address;
    private final String device_id;

    public BleDevice(String device_id, String address) {
        this.address = address;
        this.device_id = device_id;
    }

    public String getAddress() {
        return address;
    }
    public String getDevice_id() {
        return device_id;
    }
}
