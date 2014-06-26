package com.example.deadzonemobiledetection.app;

import java.util.Date;
/**
 * Created by informix on 22/6/2014.
 */
public class SyncLocalModel {
    public SyncLocalModel(){
        dirty = false;
        longitude = 0.f;
        latitude = 0.f;
        signalStrength = 0.f;
    }

    public boolean dirty;
    public String operator;
    public String network;
    public double latitude;
    public double longitude;
    public float signalStrength;
    public Date timeStamped;

}
