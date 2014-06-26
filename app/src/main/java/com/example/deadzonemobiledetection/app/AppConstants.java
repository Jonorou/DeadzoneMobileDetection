package com.example.deadzonemobiledetection.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.codefestsabah.deadzonemobile.deadzoneMobile.model.SignalMapModel;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import com.codefestsabah.deadzonemobile.deadzoneMobile.DeadzoneMobile;

import javax.annotation.Nullable;

/**
 * Created by informix on 25/6/2014.
 */
public class AppConstants {

    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();


    /**
     * Retrieve a Helloworld api service handle to access the API.
     */
    public static DeadzoneMobile getApiServiceHandle() {
        // Use a builder to help formulate the API request.
        DeadzoneMobile.Builder deadzoneSyncBuild = new DeadzoneMobile.Builder(AppConstants.HTTP_TRANSPORT,
                AppConstants.JSON_FACTORY, null);

        return deadzoneSyncBuild.build();
    }

}
