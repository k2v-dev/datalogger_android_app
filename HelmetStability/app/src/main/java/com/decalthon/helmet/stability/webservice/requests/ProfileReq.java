package com.decalthon.helmet.stability.webservice.requests;

public class ProfileReq {
    public String name;  // name
    public double ht; //cm
    public double wt; //kg
    public String gender = "M"	; //M or F or O
    public long dob;   //dob

    public ProfileReq() {
        ht = 0;
        wt = 0;
        dob = 0;
        gender = "M";
    }
}
