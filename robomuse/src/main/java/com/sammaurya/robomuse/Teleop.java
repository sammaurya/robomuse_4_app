/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sammaurya.robomuse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.sammaurya.robomuse.Core.ControlMode;
import com.sammaurya.robomuse.Core.IWaypointProvider;
import com.sammaurya.robomuse.Core.Plans.RobotPlan;
import com.sammaurya.robomuse.Core.RobotController;
import com.sammaurya.robomuse.Core.RobotStorage;
import com.sammaurya.robomuse.Core.Utils;
import com.sammaurya.robomuse.Core.WarningSystem;
import com.sammaurya.robomuse.Fragments.HUDFragment;
import com.sammaurya.robomuse.Fragments.ImageViewFragment;
import com.sammaurya.robomuse.Fragments.MapViewFragment;
import com.sammaurya.robomuse.Fragments.VirtualJoystickFragment;

import org.ros.rosjava_geometry.Vector3;

import java.util.LinkedList;
import java.util.List;

import static com.sammaurya.robomuse.MainActivity.ROBOT_INFO;


/**
 * An app that can be used to control a remote robot. This app also demonstrates
 * how to use some of views from the rosjava android library.
 *
 * @author munjaldesai@google.com (Munjal Desai)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class Teleop extends AppCompatActivity implements IWaypointProvider, AdapterView.OnItemSelectedListener {


//    // NodeMainExecutor encapsulating the Robot's connection
//    private NodeMainExecutor nodeMainExecutor;
//    // The NodeConfiguration for the connection
//    private NodeConfiguration nodeConfiguration;

//    private MainActivity context;

//    // The RobotController for managing the connection to the Robot
    private RobotController controller;
    // The WarningSystem used for detecting imminent collisions
    private WarningSystem warningSystem;

    private Fragment imageViewFragment;
    private Fragment mapViewFragment;
    private VirtualJoystickFragment joystickFragment;
    private HUDFragment hudFragment;

    // List of waypoints
    private final LinkedList<Vector3> waypoints;
    // Specifies how close waypoints need to be to be considered touching
    private static final double MINIMUM_WAYPOINT_DISTANCE = 1.0;

//    // Laser scan map // static so that it doesn't need to be saved/loaded every time the screen rotates
//    private static LaserScanMap laserScanMap;

    // Log tag String
    private static final String TAG = "Teleop";

    // Bundle keys
    private static final String WAYPOINT_BUNDLE_ID = "com.sammaurya.robomuse.waypoints";
    private static final String SELECTED_VIEW_NUMBER_BUNDLE_ID = "com.sammaurya.robomuse.drawerIndex";
    private static final String CONTROL_MODE_BUNDLE_ID = "com.sammaurya.robomuse.JoystickFragment.controlMode";

    // The saved instance state
    private Bundle savedInstanceState;
    private Spinner actionMenuSpinner;

    // Stuff for managing the current fragment
    private Fragment fragment = null;
    FragmentManager fragmentManager;
    int fragmentsCreatedCounter = 0;

    public Teleop(){
        waypoints = new LinkedList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleop);

//        // Get a support ActionBar corresponding to this toolbar
//        ActionBar ab = getSupportActionBar();
//        // Enable the Up button
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setTitle("Teleop");

        // Set default preference values
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);

        if (ROBOT_INFO != null) {
            ROBOT_INFO.save(editor);

//            editor.putString(getString(R.string.prefs_joystick_topic_edittext_key), ROBOT_INFO.getJoystickTopic());
//            editor.putString(getString(R.string.prefs_laserscan_topic_edittext_key), ROBOT_INFO.getLaserTopic());
//            editor.putString(getString(R.string.prefs_camera_topic_edittext_key), ROBOT_INFO.getCameraTopic());
//            editor.putString(getString(R.string.prefs_navsat_topic_edittext_key), ROBOT_INFO.getNavSatTopic());
//            editor.putString(getString(R.string.prefs_odometry_topic_edittext_key), ROBOT_INFO.getOdometryTopic());
//            editor.putString(getString(R.string.prefs_pose_topic_edittext_key), ROBOT_INFO.getPoseTopic());
        }

//        editor.putBoolean(getString(R.string.prefs_warning_checkbox_key), true);
//        editor.putBoolean(getString(R.string.prefs_warning_safemode_key), true);
//        editor.putBoolean(getString(R.string.prefs_warning_beep_key), true);

        editor.apply();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            // Set custom Action Bar view
            LayoutInflater inflater = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.actionbar_dropdown_menu, null);

            actionMenuSpinner = v.findViewById(R.id.spinner_control_mode);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.motion_plans, android.R.layout.simple_spinner_item);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            actionMenuSpinner.setAdapter(adapter);
            actionMenuSpinner.setOnItemSelectedListener(this);

            actionBar.setCustomView(v);
            actionBar.setDisplayShowCustomEnabled(true);
        }

//      ImageView Fragment as default fragment
        imageViewFragment = new ImageViewFragment();
        this.setDefaultFragment(imageViewFragment, R.id.fragmentContainer);

        mapViewFragment = new MapViewFragment();
        joystickFragment = new VirtualJoystickFragment();
        hudFragment = new HUDFragment();

        controller = MainActivity.getInstance().getRobotController();

        this.savedInstanceState = savedInstanceState;

        if (savedInstanceState != null) {
            //noinspection unchecked
            List<Vector3> list = (List<Vector3>) savedInstanceState.getSerializable(WAYPOINT_BUNDLE_ID);

            if (list != null) {
                waypoints.clear();
                waypoints.addAll(list);
                waypointsChanged();
            }

            setControlMode(ControlMode.values()[savedInstanceState.getInt(CONTROL_MODE_BUNDLE_ID)]);
          //  drawerIndex = savedInstanceState.getInt(SELECTED_VIEW_NUMBER_BUNDLE_ID);

//            // Load the controller
//            controller.load(savedInstanceState);
        }

        // Set the correct spinner item
        if (actionMenuSpinner != null)
        {
            actionMenuSpinner.setSelection(getControlMode().ordinal());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the Clear Waypoints button
        waypointsChanged();
    }

    @Override
    protected void onStop() {
        RobotStorage.update(this, ROBOT_INFO);

        Log.d(TAG, "onStop()");

        if (joystickFragment != null)
            joystickFragment.stop();

        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");

        if (joystickFragment != null)
            joystickFragment.stop();
    }

    /**
     * @return the Robot's current ControlMode
     */
    public ControlMode getControlMode() {
        return joystickFragment.getControlMode();
    }

    /**
     * Sets the ControlMode for controlling the Robot.
     *
     * @param controlMode The new ControlMode
     */
    public void setControlMode(ControlMode controlMode) {

        if (joystickFragment.getControlMode() == controlMode)
            return;

        // Lock the orientation for tilt controls
        lockOrientation(controlMode == ControlMode.Tilt);

        // Notify the Joystick on the new ControlMode
        joystickFragment.setControlMode(controlMode);
        hudFragment.toggleEmergencyStopUI(true);

        // If the ControlMode has an associated RobotPlan, run the plan
        RobotPlan robotPlan = ControlMode.getRobotPlan(this, controlMode);
        if (robotPlan != null) {
            controller.runPlan(robotPlan);
        } else {
            controller.stop();
        }

        invalidateOptionsMenu();

        if (controlMode == ControlMode.SimpleWaypoint || controlMode == ControlMode.Waypoint) {
            Toast.makeText(this, "Tap twice to place or delete a waypoint. " +
                    "Tap and hold a waypoint to move it.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sets the destination point.
     *
     * @param location The point
     */
    public void setDestination(Vector3 location) {
        synchronized (waypoints) {
            waypoints.addFirst(location);
        }
        waypointsChanged();
    }

    /**
     * Adds a waypoint.
     *
     * @param point The point
     */
    public void addWaypoint(Vector3 point) {
        synchronized (waypoints) {
            waypoints.addLast(point);
        }
        waypointsChanged();
    }

    /**
     * Attempts to find a Waypoint at the specified position.
     *
     * @param point The position
     * @return The index of the Waypoint in the Waypoint list or -1 if no Waypoint was found at the point
     */
    public int findWaypointAt(Vector3 point, float scale) {
        // First find the nearest point
        double minDist = Double.MAX_VALUE, dist;
        Vector3 pt, near = null;
        int idx = -1;

        for (int i = 0; i < waypoints.size(); ++i) {

            pt = waypoints.get(i);
            dist = Utils.distanceSquared(point.getX(), point.getY(), pt.getX(), pt.getY());

            if (dist < minDist) {
                minDist = dist;
                near = pt;
                idx = i;
            }
        }

        if (near == null || minDist * scale >= MINIMUM_WAYPOINT_DISTANCE)
            idx = -1;

        return idx;
    }

    /**
     * Same as above but will remove a nearby way point if one is close instead of adding the new point.
     *
     * @param point The point
     * @param scale The camera scale
     */
    public void addWaypointWithCheck(Vector3 point, float scale) {

        // First find the nearest point
        double minDist = Double.MAX_VALUE, dist;
        Vector3 near = null;

        synchronized (waypoints) {
            for (Vector3 pt : waypoints) {
                dist = Utils.distanceSquared(point.getX(), point.getY(), pt.getX(), pt.getY());

                if (dist < minDist) {
                    minDist = dist;
                    near = pt;
                }
            }
        }

        if (near != null && minDist * scale < MINIMUM_WAYPOINT_DISTANCE) {

            final Vector3 remove = near;

            AlertDialog.Builder alert = new AlertDialog.Builder(Teleop.this);
            alert.setTitle("Delete Waypoint");
            alert.setMessage("Are you sure you wish to delete this way point?");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    synchronized (waypoints) {
                        waypoints.remove(remove);
                    }
                    waypointsChanged();

                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });

            alert.show();

        } else {

            minDist = Double.MAX_VALUE;
            int j = -1;

            // See if the waypoint is on an existing line segment and if so insert it
            for (int i = 0; i < waypoints.size() - 1; ++i) {
                dist = Utils.distanceToLine(point.getX(), point.getY(), waypoints.get(i), waypoints.get(i + 1));

                if (dist < minDist) {
                    minDist = dist;
                    j = i;
                }
            }

            // Insert the waypoint if it is between two other waypoints
            if (minDist * scale < MINIMUM_WAYPOINT_DISTANCE) {
                synchronized (waypoints) {
                    waypoints.add(j + 1, point);
                }
                waypointsChanged();
            } else {
                addWaypoint(point);
            }
        }
    }

    /**
     * @return The next waypoint in line
     */
    @Override
    public Vector3 getDestination() {
        return waypoints.peekFirst();
    }

    /**
     * @return The next waypoint in line and removes it
     */
    public Vector3 pollDestination() {

        Vector3 r;

        synchronized (waypoints) {
            r = waypoints.pollFirst();
        }
        waypointsChanged();

        return r;
    }

    /**
     * @return The list of waypoints.
     */
    public LinkedList<Vector3> getWaypoints() {
        return waypoints;
    }

    /**
     * Clears all waypoints.
     */
    public void clearWaypoints() {
        synchronized (waypoints) {
            waypoints.clear();
        }
        waypointsChanged();
    }

    /*
     * Called when the waypoints have been edited.
     */
    private void waypointsChanged() {
        // Enable/Disable the clear waypoints button
        final View view;

        if (fragment != null && fragment.getView() != null
                && (view = fragment.getView().findViewById(R.id.clear_waypoints_button)) != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.setEnabled(!waypoints.isEmpty());
                }
            });
        }
    }


    /**
     * Enables/disables the action bar menu.
     * @param enabled Whether to enable or disable the menu
     */
    public void setActionMenuEnabled(boolean enabled)
    {
//        actionMenuEnabled = enabled;
//        invalidateOptionsMenu();
        if (actionMenuSpinner != null)
            actionMenuSpinner.setEnabled(enabled);
    }

    /**
     * Locks/unlocks the screen orientation.
     * Adapted from an answer on StackOverflow by jp36
     *
     * @param lock Whether to lock the orientation
     */
    public void lockOrientation(boolean lock) {

        if (lock) {
            Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            int rotation = display.getRotation();
            int tempOrientation = getResources().getConfiguration().orientation;
            int orientation = 0;

            switch (tempOrientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
                        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    else
                        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Configuration.ORIENTATION_PORTRAIT:
                    if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    else
                        orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
            }

            //noinspection ResourceType
            setRequestedOrientation(orientation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    /**
     * Callback for when a Spinner item is selected from the ActionBar.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setControlMode(ControlMode.values()[position]);
    }

    /**
     * Callback for when a Spinner item is selected from the ActionBar.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Call to stop the Robot.
     *
     * @param cancelMotionPlan Whether to cancel the current motion plan
     * @return True if a resumable RobotPlan was stopped
     */
    public boolean stopRobot(boolean cancelMotionPlan) {
        Log.d(TAG, "Stopping Robot");
        joystickFragment.stop();
        return controller.stop(cancelMotionPlan);
    }

    public RobotController getRobotController(){
        return controller;
    }

















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_teleop, menu);

        MenuItem item = menu.findItem(R.id.visualizationSwitch);
        item.setActionView(R.layout.switch_item);
        Switch visualSwitch = item.getActionView().findViewById(R.id.visualSwitch);

        visualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "MapView", Toast.LENGTH_SHORT).show();
                    replaceFragment(new MapViewFragment(), R.id.fragmentContainer);
                } else {
                    Toast.makeText(getApplication(), "ImageView", Toast.LENGTH_SHORT).show();
                    replaceFragment(new ImageViewFragment(), R.id.fragmentContainer);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.virtual_joystick_snap:
                if(!item.isChecked()){
                    item.setChecked(true);
                }else{
                    item.setChecked(false);
                }
                return true;
            case R.id.virtual_joystick:
                FrameLayout joystick = findViewById(R.id.joystickFragment);
                if(!item.isChecked()){
                    item.setChecked(true);
                    joystick.setVisibility(View.INVISIBLE);
                    Toast.makeText(this,"Joystick Hidden", Toast.LENGTH_SHORT).show();
                }else{
                    item.setChecked(false);
                    joystick.setVisibility(View.VISIBLE);
                    Toast.makeText(this,"Joystick Visible", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.monoImage:
                if(!item.isChecked()){
                    item.setChecked(true);
                    Toast.makeText(this,"Mono checked", Toast.LENGTH_SHORT).show();

                }else{
                    item.setChecked(false);
                    Toast.makeText(this,"Mono unchecked", Toast.LENGTH_SHORT).show();

                }
                return true;
            case R.id.compressedImage:
                if(!item.isChecked()){
                    item.setChecked(true);
                }else{
                    item.setChecked(false);
                }
                return true;
            case R.id.colorImage:
                if(!item.isChecked()){
                    item.setChecked(true);
                }else{
                    item.setChecked(false);
                }
                return true;
            case R.id.laserScan:
                if(!item.isChecked()){
                    item.setChecked(true);
                }else{
                    item.setChecked(false);
                }
                return true;
            case R.id.cameraControl:
                if(!item.isChecked()){
                    item.setChecked(true);
                }else{
                    item.setChecked(false);
                }
                return true;
            case R.id.robotBase:
                if(!item.isChecked()){
                    item.setChecked(true);
                    Toast.makeText(this,"RobotBase checked", Toast.LENGTH_SHORT).show();

                }else{
                    item.setChecked(false);
                    Toast.makeText(this,"RobotBase unchecked", Toast.LENGTH_SHORT).show();

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // This method is used to set the default fragment that will be shown.
    private void setDefaultFragment(Fragment defaultFragment, int fragmentContainer)
    {
        this.replaceFragment(defaultFragment, fragmentContainer);
    }

    // Replace current Fragment with the destination Fragment.
    public void replaceFragment(Fragment destFragment, int fragmmentContainer)
    {
        // First get FragmentManager object.
        FragmentManager fragmentManager = this.getSupportFragmentManager();

        // Begin Fragment transaction.
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the layout holder with the required Fragment object.
        fragmentTransaction.replace(fragmmentContainer, destFragment);

        // Commit the Fragment replace action.
        fragmentTransaction.commit();

    }
}