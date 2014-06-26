package com.example.deadzonemobiledetection.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.deadzonemobiledetection.app.SignalTrackerContract.SignalTracker;
/**
 * Created by informix on 22/6/2014.
 */
public class SignalTrackerDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "SignalTracker.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SignalTracker.TABLE_NAME + " (" +
                    SignalTracker._ID + " INTEGER PRIMARY KEY," +
                    SignalTracker.COLUMN_NAME_NETWORK + TEXT_TYPE + COMMA_SEP +
                    SignalTracker.COLUMN_NAME_OPERATOR + TEXT_TYPE + COMMA_SEP +
                    SignalTracker.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    SignalTracker.COLUMN_NAME_LNG + REAL_TYPE + COMMA_SEP +
                    SignalTracker.COLUMN_NAME_SIGNALSTRENGTH + TEXT_TYPE + COMMA_SEP +
                    SignalTracker.COLUMN_NAME_TIMESTAMP + TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SignalTracker.TABLE_NAME;

    public SignalTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
