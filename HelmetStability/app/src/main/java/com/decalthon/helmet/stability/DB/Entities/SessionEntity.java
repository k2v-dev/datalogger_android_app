package com.decalthon.helmet.stability.DB.Entities;


import com.decalthon.helmet.stability.DB.typeconverter.DateConverter;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

//This class is reached when a new session table  has been generated
@Entity(tableName = "session_summaries")
public class SessionEntity {
    @PrimaryKey( autoGenerate = true )
    @ColumnInfo(name = "Id")
    public int session_id;

    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "session_number")
    public int session_number;
    @ColumnInfo(name = "num_pages")
    public int num_pages;
    @ColumnInfo(name = "total_data")
    public int total_data;
    @ColumnInfo(name = "total_pkts")
    public int total_pkts;

    @ColumnInfo(name = "session_date")
    @TypeConverters({DateConverter.class})
    public Date date; // date formatter: dd-MM-YYYY HH:mm.sss

    @ColumnInfo(name = "duration")
    public float duration;

    @ColumnInfo(name = "num_read_pkt")
    public int num_read_pkt;

}
