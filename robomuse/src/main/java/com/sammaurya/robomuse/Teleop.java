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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sammaurya.robomuse.Fragments.ImageViewFragment;
import com.sammaurya.robomuse.Fragments.MapViewFragment;
import com.sammaurya.robomuse.Fragments.VirtualJoystickFragment;

import org.ros.address.InetAddressFactory;
import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


/**
 * An app that can be used to control a remote robot. This app also demonstrates
 * how to use some of views from the rosjava android library.
 *
 * @author munjaldesai@google.com (Munjal Desai)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 */
public class Teleop extends AppCompatRosActivity {



    public Teleop() {
        super("Teleop", "Teleop");
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

//        ImageView Fragment as default fragment
        Fragment imageViewFragment = new ImageViewFragment();
        this.setDefaultFragment(imageViewFragment, R.id.fragmentContainer);

//        Fragment mapViewFragment = new MapViewFragment();
//        this.setDefaultFragment(mapViewFragment,R.id.fragmentContainer);

        //Joystick fragment set to show by default
        this.setDefaultFragment(new VirtualJoystickFragment(), R.id.joystickFragment);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        // visualizationView.init(nodeMainExecutor);
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
                        getMasterUri());
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