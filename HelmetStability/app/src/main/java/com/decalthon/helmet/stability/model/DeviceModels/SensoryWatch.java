package com.decalthon.helmet.stability.model.devicemodels;

// Pojo class for Sensory watch
public class SensoryWatch {
    public short dev2_sensation_t;
    public short dev2_sensation_h;
    public String dev2_sensation_t_str;
    public String dev2_sensation_h_str;
    public String english_msg;

    public SensoryWatch() {
    }

    public SensoryWatch(short dev2_sensation_t, short dev2_sensation_h, String dev2_sensation_t_str, String dev2_sensation_h_str, String english_msg) {
        this.dev2_sensation_t = dev2_sensation_t;
        this.dev2_sensation_h = dev2_sensation_h;
        this.dev2_sensation_t_str = dev2_sensation_t_str;
        this.dev2_sensation_h_str = dev2_sensation_h_str;
        this.english_msg = english_msg;
    }
}
