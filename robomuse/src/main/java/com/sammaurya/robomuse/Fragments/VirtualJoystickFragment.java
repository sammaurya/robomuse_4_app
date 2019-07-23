package com.sammaurya.robomuse.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sammaurya.robomuse.Core.ControlMode;
import com.sammaurya.robomuse.R;
import com.sammaurya.robomuse.Views.VirtualJoystickView;


/**
 * Fragment containing the VirtualJoystickView.
 *
 * Created by Michael Brunson on 11/7/15.
 */
public class VirtualJoystickFragment extends Fragment {
    private VirtualJoystickView virtualJoystick;
    private View view;
    private ControlMode controlMode = ControlMode.Joystick;

    /**
     * Default Constructor.
     */
    public VirtualJoystickFragment() {
    }

    /**
     * Create this Fragments View.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the view if needed
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_virtual_joystick_view, container, false);

            // Grab the VirtualJoystickView and set its topic
            virtualJoystick = view.findViewById(R.id.virtual_joystick);
        }

        return view;
    }

    /**
     * Returns the VirtualJoystickView
     * @return The VirtualJoystickView
     */
    public VirtualJoystickView getVirtualJoystickView() {
        return virtualJoystick;
    }

    /**
     * Get the currently active ControlMode.
     * @return The current ControlMode
     */
    public ControlMode getControlMode() {
        return controlMode;
    }

    /**
     * Set the ControlMode for controlling the Joystick.
     * @param controlMode The new ControlMode
     */
    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
        this.invalidate();
    }

    /**
     * Tests whether the Joystick supports accelerometer control.
     * @return True if the Joystick supports accelerometer control, false otherwise
     */
    @SuppressWarnings("unused") // Maybe later...
    public boolean hasAccelerometer() {
        return virtualJoystick.hasAccelerometer();
    }

    /**
     * Invalidate the Fragment, updating the visibility of the Joystick based on the ControlMode.
     *
     */
    public void invalidate() {

        switch (controlMode) {
            case Joystick:
                show();
                break;

            case Tilt:
                show();
                break;

            default:
                hide();
                break;
        }

        virtualJoystick.setControlMode(controlMode);
        virtualJoystick.controlSchemeChanged();
    }

    /**
     * Stops the JoystickFragment.
     */
    public void stop() {
        virtualJoystick.stop();
    }

    /**
     * Shows the JoystickFragment.
     */
    public void show(){
        getFragmentManager()
                .beginTransaction()
                .show(this)
                .commit();
    }

    /**
     * Hides the JoystickFragment.
     */
    public void hide(){
        getFragmentManager()
                .beginTransaction()
                .hide(this)
                .commit();
    }
}
