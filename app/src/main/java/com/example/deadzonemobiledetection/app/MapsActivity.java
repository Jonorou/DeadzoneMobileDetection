package com.example.deadzonemobiledetection.app;

import android.content.Entity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codefestsabah.deadzonemobile.deadzoneMobile.DeadzoneMobile;
import com.codefestsabah.deadzonemobile.deadzoneMobile.model.SignalMapModelCollection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import com.codefestsabah.deadzonemobile.deadzoneMobile.model.SignalMapModel;
import com.codefestsabah.deadzonemobile.deadzoneMobile.DeadzoneMobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends Fragment {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private SignalTrackerDbHelper mDbHelper;
    private SQLiteDatabase db;

    private LatLng mCurrentLocation;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    private static final String LOG_TAG = "SyncAdapter";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        mDbHelper = new SignalTrackerDbHelper(getActivity());
        db = mDbHelper.getReadableDatabase();
        setUpMapIfNeeded();

        AsyncTask<Void, Void, SignalMapModelCollection> getSignalMap = new AsyncTask<Void, Void, SignalMapModelCollection>() {
            @Override
            protected SignalMapModelCollection doInBackground(Void... voids) {
                try {
                    SignalMapModelCollection contentModel = new SignalMapModelCollection();
                    DeadzoneMobile apiServiceHandle = AppConstants.getApiServiceHandle();
                    DeadzoneMobile.SignalMap.Get getSignalCommand = apiServiceHandle.signalMap().get();
                    contentModel = getSignalCommand.execute();
                    return contentModel;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception during API call", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(SignalMapModelCollection signalMap) {
                if (signalMap != null){
                    highlightMap(signalMap);
                } else {
                    Log.d(LOG_TAG, "signal was not present");
                }
            }
        };

        getSignalMap.execute();

//        ** Testing Purpose
//        List<SignalMapModel> testList = new ArrayList<SignalMapModel>();
//        for(int i = 0; i<=10; i++) {
//            SignalMapModel model = new SignalMapModel();
//            model.setLongitude(Double.toString(116.12 - i));
//            model.setLatitude(Double.toString(5.97778 - i));
//            model.setSignalStrength(Float.toString(-67));
//            testList.add(model);
//        }
//
//        SignalMapModelCollection list = new SignalMapModelCollection();
//        list.setItems(testList);
//        highlightMap(list);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.deadzoneMap)).getMap();
            mMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                String[] projection = {
                        SignalTrackerContract.SignalTracker.COLUMN_NAME_NETWORK,
                        SignalTrackerContract.SignalTracker.COLUMN_NAME_OPERATOR
                };

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
                    Log.d("Network", c.getString(0));
                    c.moveToNext();
                }

                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // Get user current position
                        mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("Im here"));
                        // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(), arg0.getLongitude()), 10));
                    }
                });

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.9479447, 116.0904637), 0f));
                //setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        Location myLocation = mMap.getMyLocation();
        double dLatitude = myLocation.getLatitude();
        double dLongitude = myLocation.getLongitude();
        mMap.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude)).title("My Location"));
    }

    private void highlightMap(SignalMapModelCollection list) {
        List<List<LatLng>> listLatLng = new ArrayList<List<LatLng>>();
        List<LatLng> listLatLngGreen = new ArrayList<LatLng>();
        List<LatLng> listLatLngBlue = new ArrayList<LatLng>();
        List<LatLng> listLatLngRed = new ArrayList<LatLng>();
        double lat, lng;
        float signal;
        for(SignalMapModel model: list.getItems()) {
            signal = Float.parseFloat(model.getSignalStrength());
            lat = Double.parseDouble(model.getLatitude());
            lng = Double.parseDouble(model.getLongitude());
            if (signal >= -20) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Deadzone"));
            }
            if (signal <= -21 && signal >= -30) {
                listLatLngGreen.add(new LatLng(lat, lng));
            }
            if (signal <= -31 && signal >= -65) {
                listLatLngBlue.add(new LatLng(lat, lng));
            }
            if (signal <= -66 && signal >= -100) {
                listLatLngRed.add(new LatLng(lat, lng));
            }
        }
        listLatLng.add(listLatLngGreen);
        listLatLng.add(listLatLngBlue);
        listLatLng.add(listLatLngRed);
        int[] colors;
        int i = 0;
        for (List<LatLng> l : listLatLng) {
            if (l.size() > 0) {
                switch (i) {
                    case 0:
                        colors = new int[]{
                                Color.rgb(102, 225, 0), // green
                                Color.rgb(0, 255, 0) // blue

                        };
                        break;
                    case 1:
                        colors = new int[]{
                                Color.rgb(102, 225, 0), // green
                                Color.rgb(0, 0, 255) // green

                        };
                        break;
                    case 2:
                        colors = new int[]{
                                Color.rgb(102, 225, 0), // green
                                Color.rgb(225, 0, 0) // red

                        };
                        break;
                    default:
                        colors = new int[]{ };
                        break;
                }

                float[] startPoints = {
                        0.8f, 1f
                };

                Gradient gradient = new Gradient(colors, startPoints);

                mProvider = new HeatmapTileProvider.Builder()
                        .data(l)
                        .gradient(gradient)
                        .build();
                mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            }
            i++;
        }
    }
}
