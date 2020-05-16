package com.decalthon.helmet.stability.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"session_number","timestamp"}, unique = true)})
public class SessionSummary {
//    1st byte: b1  - Output Packet Header 0xAC
//    2nd byte b2 – Session number pl1
//    3rd & 4th byte b3-b4   – Number of pages in unsigned short for session pl1
//    5th byte - Packets in last page of the session
//    6th bytes – Date of the session (DD)
//    7th byte- Month of the session (MM)
//    8th byte – Year of the session (YY)
//    9th byte – Hour in (HH)
//    10th byte – Minutes in (MM)
//    11th byte – Seconds in (SS)
//    12th byte – Millisecond in (ms)
//    13th byte – Activity type Identifier
//    13th bytes and 14th: Checksum in unsigned short
    private String name = "";

    @PrimaryKey(autoGenerate = true)
    private int session_id;



    private int session_number ;
    @ColumnInfo(name ="timestamp")
    private long date;
    private float duration = 0.0f;
    private int activity_type = 0;
    private String note="";
    private long note_timestamp;
    private short firmware_type = -1;
    private float firmware_ver = 0.0f ;
    private short sampling_freq = 0;


    // Device1
    private int num_pages;
    private int total_data = 0;
    private int total_pkts = 0;
//    private int num_read_pkt;
    @ColumnInfo(defaultValue = "false")
    private boolean isComplete;

    //ButtonBox
    private int bb_num_pages;
    private int bb_total_data = 0;
    private int bb_total_pkts = 0;
//    private int bb_num_read_pkt;
    @ColumnInfo(defaultValue = "false")
    private boolean bb_isComplete;

    public int getSession_id() {
        return session_id;
    }

    public void setSession_id(int session_id) {
        this.session_id = session_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getSession_number() {
        return session_number;
    }

    public void setSession_number(int session_number) {
        this.session_number = session_number;
    }

    public int getTotal_data() {
        return total_data;
    }

    public void setTotal_data(int total_data) {
        this.total_data = total_data;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getTotal_pkts() {
        return total_pkts;
    }

    public void setTotal_pkts(int total_pkts) {
        this.total_pkts = total_pkts;
    }

//    public int getNum_read_pkt() {
//        return num_read_pkt;
//    }
//
//    public void setNum_read_pkt(int num_read_pkt) {
//        this.num_read_pkt = num_read_pkt;
//    }

    public int getNum_pages() {
        return num_pages;
    }

    public void setNum_pages(int num_pages) {
        this.num_pages = num_pages;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getBb_num_pages() {
        return bb_num_pages;
    }

    public void setBb_num_pages(int bb_num_pages) {
        this.bb_num_pages = bb_num_pages;
    }

    public int getBb_total_data() {
        return bb_total_data;
    }

    public void setBb_total_data(int bb_total_data) {
        this.bb_total_data = bb_total_data;
    }

    public int getBb_total_pkts() {
        return bb_total_pkts;
    }

    public void setBb_total_pkts(int bb_total_pkts) {
        this.bb_total_pkts = bb_total_pkts;
    }

//    public int getBb_num_read_pkt() {
//        return bb_num_read_pkt;
//    }
//
//    public void setBb_num_read_pkt(int bb_num_read_pkt) {
//        this.bb_num_read_pkt = bb_num_read_pkt;
//    }

    public boolean isBb_isComplete() {
        return bb_isComplete;
    }

    public void setBb_isComplete(boolean bb_isComplete) {
        this.bb_isComplete = bb_isComplete;
    }

    public int getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(int activity_type) {
        this.activity_type = activity_type;
    }

    public String getNote() {
        if(note == null){
            return " ";
        }
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getNote_timestamp() {
        return note_timestamp;
    }

    public void setNote_timestamp(long note_timestamp) {
        this.note_timestamp = note_timestamp;
    }

    public short getFirmware_type() {
        return firmware_type;
    }

    public void setFirmware_type(short firmware_type) {
        this.firmware_type = firmware_type;
    }

    public short getSampling_freq() {
        return sampling_freq;
    }

    public void setSampling_freq(short sampling_freq) {
        this.sampling_freq = sampling_freq;
    }

    public float getFirmware_ver() {
        return firmware_ver;
    }

    public void setFirmware_ver(float firmware_ver) {
        this.firmware_ver = firmware_ver;
    }

    public int getSize(){
        int total_bytes = this.total_data + this.bb_total_data;
        return (total_bytes/1024);
    }
}
