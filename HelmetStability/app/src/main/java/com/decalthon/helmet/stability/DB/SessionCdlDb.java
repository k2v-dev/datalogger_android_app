package com.decalthon.helmet.stability.DB;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

//This annotation is used to create a RoomDatabase with one or more entities.
@Database(entities = {GpsSpeed.class, MarkerData.class, SensorDataEntity.class, ButtonBoxEntity.class
        ,SessionSummary.class},  version = 23, exportSchema = false)
public abstract class SessionCdlDb extends RoomDatabase {
    private static SessionCdlDb sessionCdlDb;
    private static String TAG = "SessionCdlDb";
    public static SessionCdlDb getInstance(Context context) {
        if(sessionCdlDb == null){
            sessionCdlDb = Room.databaseBuilder(context, SessionCdlDb.class,"Session_CDL_DB")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }
        return sessionCdlDb;
    }

    // THe DAO handle to the GPSSpeed and marker entities is declared here
    public abstract GpsSpeedDAO gpsSpeedDAO();
    public abstract MarkerDataDAO getMarkerDataDAO();
    public abstract SessionDataDao getSessionDataDAO();
    public abstract MergedDao getMergedDao();

    private static RoomDatabase.Callback callback= new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            //Toast.makeText(getApplicationContext()," On Create Called ",Toast.LENGTH_LONG).show();
            Log.i(TAG, " on create invoked ");

        }


        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            //  Toast.makeText(getApplicationContext()," On Create Called ",Toast.LENGTH_LONG).show();
            Log.i(TAG, " on open invoked ");

        }

    };
}
