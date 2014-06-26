package com.example.deadzonemobiledetection.app;

import android.provider.BaseColumns;

/**
 * Created by informix on 22/6/2014.
 */
public class SignalTrackerContract {

    public SignalTrackerContract() {}

    public static abstract class SignalTracker implements BaseColumns {
        public static final String TABLE_NAME = "deadzone_heatmap";
        public static final String COLUMN_NAME_NULLABLE = null;
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LNG = "longitude";
        public static final String COLUMN_NAME_OPERATOR = "operator";
        public static final String COLUMN_NAME_NETWORK = "network";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_SIGNALSTRENGTH = "signalstrength";
    }
}
