package com.example.deadzonemobiledetection.app;

import android.content.Context;
import android.os.Handler;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by informix on 19/6/2014.
 */
public class CellSignalInfo extends PhoneStateListener {
    Context myContext;

    private List<CellSignalInfoCallback> listeners = new ArrayList<CellSignalInfoCallback>();

    public CellSignalInfo (Context mContext) {
        super();
        mCallback = (CellSignalInfoCallback) mContext;
        registerCallbackListener(mCallback);
        this.myContext = mContext;
    }

    private void registerCallbackListener(CellSignalInfoCallback listener) {
        listeners.add(listener);
    }


    private CellSignalInfoCallback mCallback;

    public float signalStrengths;

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        TelephonyManager telephonyManager = (TelephonyManager)myContext.getSystemService(Context.TELEPHONY_SERVICE);
        // for example value of first element
        for(CellSignalInfoCallback l: listeners) {
            l.onSignalStrengthsChanged(signalStrength);
        }
        if (signalStrength.isGsm()) {
            // Resource from http://www.tested.com/tech/android/557-how-to-measure-cell-signal-strength-on-android-phones/
            // Formula to get dbm = -113 + (2*ASU)
            signalStrengths = -113 + (2*signalStrength.getGsmSignalStrength());

        } else {
            for(CellSignalInfoCallback l: listeners) {
                l.onSignalStrengthsChanged(signalStrength);
            }
        }
    }

    public static interface CellSignalInfoCallback {
        void onSignalStrengthsChanged(SignalStrength signalStrength);
    }

}
