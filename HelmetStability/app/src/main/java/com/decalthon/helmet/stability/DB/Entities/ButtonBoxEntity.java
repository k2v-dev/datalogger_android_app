package com.decalthon.helmet.stability.DB.Entities;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = SessionSummary.class, parentColumns = "session_id",childColumns = "session_id", onDelete = CASCADE))
public class ButtonBoxEntity {
    public long packet_number;

    public long session_id;

    @Ignore
    public Date date;

    @PrimaryKey
    public long dateMillis;

    public float ax_3axis;

    public float ay_3axis;

    public float az_3axis;

    public short button_type;

}
