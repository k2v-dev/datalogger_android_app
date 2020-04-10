package com.decalthon.helmet.stability.model.DeviceModels;

/**
 *
 * @author gts
 */
public class DeviceData {
    public String mac_address;
    public byte[] bytes;

    public DeviceData(String mac_address, byte[] bytes) {
        this.mac_address = mac_address;
        this.bytes = bytes;
    }

}
