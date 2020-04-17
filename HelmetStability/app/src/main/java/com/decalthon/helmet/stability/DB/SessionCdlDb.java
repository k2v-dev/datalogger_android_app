package com.decalthon.helmet.stability.DB;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.decalthon.helmet.stability.DB.Entities.ButtonBoxEntity;
import com.decalthon.helmet.stability.DB.Entities.GpsSpeed;
import com.decalthon.helmet.stability.DB.Entities.MarkerData;
import com.decalthon.helmet.stability.DB.Entities.SensorDataEntity;
import com.decalthon.helmet.stability.DB.Entities.SessionSummary;

//This annotation is used to create a RoomDatabase with one or more entities.
@Database(entities = {GpsSpeed.class, MarkerData.class, SensorDataEntity.class, ButtonBoxEntity.class
        ,SessionSummary.class},  version = 29, exportSchema = false)
public abstract class SessionCdlDb extends RoomDatabase {
    private static SessionCdlDb sessionCdlDb;
    private static String TAG = "SessionCdlDb";
    public static SessionCdlDb getInstance(Context context) {
        if(sessionCdlDb == null){
            sessionCdlDb = Room.databaseBuilder(context, SessionCdlDb.class,"Session_CDL_DB")
                    //.fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28)
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

    static final Migration MIGRATION_24_25 = new Migration(24, 25) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE marker_data "
                    + " ADD COLUMN session_id INTEGER NOT NULL DEFAULT '0'");
        }
    };
    static final Migration MIGRATION_25_26 = new Migration(25, 26) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE marker_data "
                    + " ADD COLUMN lat REAL NOT NULL DEFAULT '0.0'");
            database.execSQL("ALTER TABLE marker_data "
                    + " ADD COLUMN lng REAL NOT NULL DEFAULT '0.0'");
        }
    };

    static final Migration MIGRATION_26_27 = new Migration(26, 27) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE SessionSummary "
                    + " ADD COLUMN activity_type INTEGER NOT NULL DEFAULT '0'");
            database.execSQL("ALTER TABLE SessionSummary "
                    + " ADD COLUMN note TEXT ");
            database.execSQL("ALTER TABLE gps_speed "
                    + " ADD COLUMN altitude REAL NOT NULL DEFAULT '0.0'");
        }
    };

    static final Migration MIGRATION_27_28 = new Migration(27, 28) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE SensorDataEntity "
                    + " ADD COLUMN gps_tag_type INTEGER NOT NULL DEFAULT '0'");
            database.execSQL("ALTER TABLE SensorDataEntity "
                    + " ADD COLUMN gps_tagger INTEGER NOT NULL DEFAULT '0'");
        }
    };



}
