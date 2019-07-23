package com.sammaurya.robomuse.Fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

public class SimpleFragment extends Fragment {

    /**
     * Shows the Fragment, making it visible.
     */
    public void show(){
        getFragmentManager()
                .beginTransaction()
                .show(this)
                .commit();
    }

    /**
     * Hides the Fragment, making it invisible.
     */
    public void hide(){
        getFragmentManager()
                .beginTransaction()
                .hide(this)
                .commit();
    }

    /**
     * Convenience method to get the current activity as a ControlApp.
     * @return The current activity casted to a ControlApp if it is one and null otherwise
     */
    public Context getControlApp() {
        if (getActivity()!=null)
            return getActivity();
        else
            return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
