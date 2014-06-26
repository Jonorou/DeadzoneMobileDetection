package com.example.deadzonemobiledetection.app;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.codefestsabah.deadzonemobile.deadzoneMobile.model.SignalMapModel;
import com.codefestsabah.deadzonemobile.deadzoneMobile.DeadzoneMobile;

import java.io.IOException;
import java.util.Date;

/**
 * Created by informix on 22/6/2014.
 */
public class SyncAdapter extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private SignalTrackerDbHelper mDbHelper;
    private SQLiteDatabase db;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mLocation;

    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;

    private SyncLocalModel mSyncLocalModel;

    private static final String LOG_TAG = "SyncAdapter";

//    private DeadzoneMobileSync mDeadzoneMobileSync;


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            long newRowId;
            while (true) {
                synchronized (this) {
                    try {
                        syncServer();
                        // minutes * seconds * miliseconds
                        wait(1 * 60 * 1000);
                    } catch (Exception e) {
                        Log.d("Error", e.getMessage());
                    }
                }
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            // stopSelf(msg.arg1);
        }
    }

    public long storeLocal() {
        if (mSyncLocalModel.dirty) {
            ContentValues values = new ContentValues();
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_NETWORK, mSyncLocalModel.network);
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_OPERATOR, mSyncLocalModel.operator);
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_LAT, mSyncLocalModel.latitude);
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_LNG, mSyncLocalModel.longitude);
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_TIMESTAMP, String.valueOf(mSyncLocalModel.timeStamped));
            values.put(SignalTrackerContract.SignalTracker.COLUMN_NAME_SIGNALSTRENGTH, mSyncLocalModel.signalStrength);

            long newRowId = db.insert(
                    SignalTrackerContract.SignalTracker.TABLE_NAME,
                    SignalTrackerContract.SignalTracker.COLUMN_NAME_NULLABLE,
                    values);
            mSyncLocalModel.dirty = false;
            Log.d("Store Local", "Store Local");
            return newRowId;
        }
        return 0;
    }

    public void syncServer() {
            final AsyncTask<Void, Void, SignalMapModel> addSignalMap = new AsyncTask<Void, Void, SignalMapModel>() {
                @Override
                protected SignalMapModel doInBackground(Void... voids) {
                    String[] projection = {
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_TIMESTAMP,
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_OPERATOR,
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_LAT,
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_LNG,
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_NETWORK,
                            SignalTrackerContract.SignalTracker.COLUMN_NAME_SIGNALSTRENGTH,
                            SignalTrackerContract.SignalTracker._ID
                    };
                    try {
                        Cursor c = db.query(
                                SignalTrackerContract.SignalTracker.TABLE_NAME,
                                projection,
                                null,
                                null,
                                null,
                                null,
                                null
                        );
                        c.moveToFirst();
                        while(!c.isAfterLast()) {
                            try {
                                SignalMapModel contentModel = new SignalMapModel();
                                contentModel.setTimestamp(c.getString(0));
                                contentModel.setOperator(c.getString(1));
                                contentModel.setLatitude(c.getString(2));
                                contentModel.setLongitude(c.getString(3));
                                contentModel.setNetwork(c.getString(4));
                                contentModel.setSignalStrength(c.getString(5));
                                DeadzoneMobile apiServiceHandle = AppConstants.getApiServiceHandle();
                                DeadzoneMobile.SignalMap.Add addSignalMapCommand = apiServiceHandle.signalMap().add(contentModel);
                                addSignalMapCommand.execute();
                            } catch (IOException e) {
                                Log.e(LOG_TAG, "Exception during API call", e);
                            }
                        }
                        Log.d("Sync Server", "Sync Server");
                    } catch (SQLiteException e) {
                        Log.e("Sqlite Error", "Exception during execute query", e);
                    }
                    return null;
                }
            };
            addSignalMap.execute((Void)null);
            // Clean local database
            Log.d("Delete sqlite", "Delete sqlite");
            db.delete(SignalTrackerContract.SignalTracker.TABLE_NAME, null, null);
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mLocationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        mTelephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        mSyncLocalModel = new SyncLocalModel();

        mDbHelper = new SignalTrackerDbHelper(this);
        db = mDbHelper.getWritableDatabase();


        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (mSyncLocalModel.signalStrength != 0) {
                    mSyncLocalModel.dirty = true;
                }
                mSyncLocalModel.latitude = location.getLatitude();
                mSyncLocalModel.longitude=location.getLongitude();
                mSyncLocalModel.timeStamped = new Date();
                storeLocal();
                Log.d("Location Changed", "Location Changed");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                if (mSyncLocalModel.latitude > 0.f && mSyncLocalModel.longitude > 0.f) {
                    mSyncLocalModel.dirty = true;
                }
                if (signalStrength.isGsm()) {
                    mSyncLocalModel.signalStrength = -113 + (2*signalStrength.getGsmSignalStrength());
                } else {
                    mSyncLocalModel.signalStrength = signalStrength.getCdmaDbm();
                }
                if (signalStrength.isGsm()) {
                    mSyncLocalModel.network = "GSM";
                } else {
                    mSyncLocalModel.network = "CDMA";
                }
                mSyncLocalModel.operator = mTelephonyManager.getNetworkOperatorName();
                mSyncLocalModel.timeStamped = new Date();
                Log.d("Signal Changed", "Signal Changed");
                storeLocal();
            }
        };

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        mSyncLocalModel.operator = mTelephonyManager.getNetworkOperatorName();
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                1, 1, mLocationListener);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (mLocation != null) {
            mSyncLocalModel.latitude = mLocation.getLatitude();
            mSyncLocalModel.longitude = mLocation.getLongitude();
        }





        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        //mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

}


