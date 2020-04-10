package com.decalthon.helmet.stability.model;

public class IndoorTimestamp {
    final String message;
    final String date;
    public IndoorTimestamp(String date, String message){
        this.date = date;
        this.message = message;
    }
    public String getDate(){
        return this.date;
    }
    public boolean hasMessage(){
        if(this.message.equals("")){
            return false;
        }else{
            return true;
        }
    }
}
