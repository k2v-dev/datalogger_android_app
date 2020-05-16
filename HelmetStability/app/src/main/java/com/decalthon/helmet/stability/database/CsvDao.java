package com.decalthon.helmet.stability.database;

import com.decalthon.helmet.stability.database.entities.CsvFileStatus;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CsvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertReplace(CsvFileStatus csvFileStatus);

    @Query("select * from CsvFileStatus where is_uploaded = 0")
    public List<CsvFileStatus> getCsvFiles();

    @Delete
    public void deleteFile(CsvFileStatus csvFileStatus);

    @Query("delete from CsvFileStatus where filename LIKE :filename")
    public void deleteFile(String filename);
}
