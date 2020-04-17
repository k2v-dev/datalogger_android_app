package com.decalthon.helmet.stability.model.DeviceModels.session;

//D1 X1  X2  X3 X4  X5  X6 X7 X8  X9 X10 CS1 CS2 where
//        where D1 is the new packet Header : 0xDD
//        X1 – Session Number
//        X2 – Date (unsigned single byte)
//        X3 – Month (unsigned single byte)
//        X4 – Year (unsigned single byte)
//        X5 - Hour in 24 hour format (unsigned byte)
//        X6 - Minute (unsigned byte)
//        X7- Second (unsigned byte)
//        X8- Milisecond  (unsigned byte) scale factor 10
//        X9- Size in MB (4 bytes float)
//        X10- Activity Type
//        CS1 and CS2 =checksum;

import java.util.Date;

public class SessionHeader {
    int number;
    Date date; // dd-MM-YYYY HH:mm:SS.sss
    float data_size;
    int activity_type;
    short firmwareType;
    short samp_freq;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getData_size() {
        return data_size;
    }

    public void setData_size(float data_size) {
        this.data_size = data_size;
    }

    public int getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(int activity_type) {
        this.activity_type = activity_type;
    }

    public short getFirmwareType() {
        return firmwareType;
    }

    public void setFirmwareType(short firmwareType) {
        this.firmwareType = firmwareType;
    }

    public short getSamp_freq() {
        return samp_freq;
    }

    public void setSamp_freq(short samp_freq) {
        this.samp_freq = samp_freq;
    }
}
