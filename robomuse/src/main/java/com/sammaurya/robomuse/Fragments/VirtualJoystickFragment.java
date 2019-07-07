package com.sammaurya.robomuse.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.sammaurya.robomuse.R;

import org.ros.android.view.VirtualJoystickView;


public class VirtualJoystickFragment extends RosFragment {

    private VirtualJoystickView virtualJoystickView;


    /**
     * Default Constructor.
     */
    public VirtualJoystickFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_virtual_joystick, container, false);

        //VirtualJoystickView
        virtualJoystickView = view.findViewById(R.id.virtual_joystick);

        if (nodeConfiguration != null)
            nodeMainExecutor
                    .execute(virtualJoystickView, nodeConfiguration.setNodeName("virtual_joystick"));

        return view;
    }


    @Override
    void shutdown() {
        nodeMainExecutor.shutdownNodeMain(virtualJoystickView);
    }

}
