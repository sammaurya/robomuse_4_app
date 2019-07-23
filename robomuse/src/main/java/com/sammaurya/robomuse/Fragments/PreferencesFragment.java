package com.sammaurya.robomuse.Fragments;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.sammaurya.robomuse.R;


/**
 * Fragment containing the Preferences screen.
 *
 * Created by Michael Brunson on 11/7/15.
 */
public class PreferencesFragment extends PreferenceFragmentCompat {

    // Log tag String
    private static final String TAG = "PreferencesFragment";

    /**
     * Default Constructor.
     */
    public PreferencesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            return;

        // Add the preferences
        addPreferencesFromResource(R.xml.prefs);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    /**
     * Called when the user has finished editing/viewing the current Preferences.
     * Updates the topic names on the current RobotInfo to be in sync with the Preferences.
     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//
//        MainActivity.ROBOT_INFO.load(prefs);
//
//        // Let the ControlApp know that the Preferences have been changed so it can save them
//        if (getActivity() instanceof MainActivity)
//            ((MainActivity)getActivity()).onPreferencesChanged(prefs);
//        else
//            Log.w(TAG, "Could not notify ControlApp!");
//
//        super.onStop();
//    }
}

