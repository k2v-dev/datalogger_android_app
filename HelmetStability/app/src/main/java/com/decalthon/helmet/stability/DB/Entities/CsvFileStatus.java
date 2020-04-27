package com.decalthon.helmet.stability.DB.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CsvFileStatus {
    @PrimaryKey
    @NonNull
    public String filename;

    @ColumnInfo(defaultValue = "0")
    public boolean is_uploaded;

    public CsvFileStatus(){
    }

    @Ignore
    public CsvFileStatus(String filename){
        this.filename = filename.trim();
        this.is_uploaded = false;
    }

}
