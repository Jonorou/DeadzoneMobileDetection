package com.example.deadzonemobiledetection.app;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SyncActivity extends Fragment {

    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;

    private SignalTrackerDbHelper mDbHelper;
    private SQLiteDatabase db;

    public SyncActivity() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_sync, container, false);
        String[] projection = {
                SignalTrackerContract.SignalTracker.COLUMN_NAME_TIMESTAMP,
                SignalTrackerContract.SignalTracker.COLUMN_NAME_OPERATOR
        };

        mDbHelper = new SignalTrackerDbHelper(getActivity());
        db = mDbHelper.getReadableDatabase();

        Cursor c = db.query(
                SignalTrackerContract.SignalTracker.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ListView list = (ListView) rootView.findViewById(R.id.listView);

        ArrayList<String> stringArr = new ArrayList<String>();
        c.moveToFirst();
        while(!c.isAfterLast()) {
            stringArr.add(c.getString(0) + " " + c.getString(1));
            c.moveToNext();
        }
        String[] values = new String[stringArr.size()];
        stringArr.toArray(values);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        list.setAdapter(adapter);

        return rootView;
    }


}
