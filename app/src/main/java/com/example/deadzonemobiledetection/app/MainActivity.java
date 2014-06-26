package com.example.deadzonemobiledetection.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, CellSignalInfo.CellSignalInfoCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    TextView currentStrength;
    TextView txtOperator;
    TelephonyManager telephonyManagerManager;
    CellSignalInfo phoneStateListener;

    private CellSignalInfo mCellSignalInfo;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SignalGauge mSignalGauge;





    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        currentStrength = (TextView) findViewById(R.id.txtCellDbm);
        txtOperator = (TextView) findViewById(R.id.txtOperator);

        phoneStateListener = new CellSignalInfo(this);

        telephonyManagerManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManagerManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mSignalGauge = (SignalGauge) findViewById(R.id.signalGauge);

        Intent intent = new Intent(this, SyncAdapter.class);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        telephonyManagerManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onPause() {
        super.onPause();
        telephonyManagerManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

//    public void setSignalGaugeNeedle(float needleTarget) {
//        mSignalGauge.setObserver(new SignalGauge.signalGaugeObserver() {
//            @Override
//            public void callback() {
//
//            }
//        });
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment newFragment;
        Fragment oldFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:
                newFragment = new MapsActivity();
                oldFragment = fragmentManager.findFragmentById(R.id.deadzoneMap);
                if(oldFragment != null) {
                    Log.d("Exist", "It's exist");
                    fragmentManager.beginTransaction()
                            .remove(oldFragment)
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new MapsActivity())
                        .addToBackStack(null)
                        .commit();

                break;
            case 2:
                newFragment = new SyncActivity();
                oldFragment = fragmentManager.findFragmentById(R.id.activity_sync);

                if(oldFragment != null) {
                    Log.d("Exist", "It's exist");
                    fragmentManager.beginTransaction()
                            .remove(oldFragment)
                            .commit();
                }
                fragmentManager.beginTransaction()
                        .replace(R.id.container, newFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    //TODO onSignalStrengthsChanged update Gauge Needle Target
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        float signal;
        if (signalStrength.isGsm()) {
            signal = -113 + (2*signalStrength.getGsmSignalStrength());

        }else {
            signal = signalStrength.getCdmaDbm();
        }
        currentStrength.setText(String.valueOf(signal) + "dbm");
        txtOperator.setText(telephonyManagerManager.getNetworkOperatorName());
        mSignalGauge.setTargetValue(Math.abs(signal));
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();

            Fragment mapFragment = new MapsActivity();
            mapFragment.getFragmentManager();

            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
