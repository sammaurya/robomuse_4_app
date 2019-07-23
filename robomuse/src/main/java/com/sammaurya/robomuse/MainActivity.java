package com.sammaurya.robomuse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.sammaurya.robomuse.Core.ControlMode;
import com.sammaurya.robomuse.Core.RobotController;
import com.sammaurya.robomuse.Core.RobotInfo;
import com.sammaurya.robomuse.Core.WarningSystem;
import com.sammaurya.robomuse.Fragments.HUDFragment;
import com.sammaurya.robomuse.Fragments.VirtualJoystickFragment;

import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;


public class MainActivity extends AppCompatRosActivity implements NavigationView.OnNavigationItemSelectedListener  {

    /** Notification ticker for the App */
    public static final String NOTIFICATION_TICKER = "ROS Control";
    /** Notification title for the App */
    public static final String NOTIFICATION_TITLE = "ROS Control";

    /** The RobotInfo of the connected Robot */
    public static RobotInfo ROBOT_INFO;

    // The RobotController for managing the connection to the Robot
    private static RobotController controller;
    // Fragment for the Joystick
    private VirtualJoystickFragment joystickFragment;
    // Fragment for the HUD
    private HUDFragment hudFragment;

    // The WarningSystem used for detecting imminent collisions
    private WarningSystem warningSystem;

    // NodeMainExecutor encapsulating the Robot's connection
    private NodeMainExecutor nodeMainExecutor;
    // The NodeConfiguration for the connection
    private NodeConfiguration nodeConfiguration;


    // Log tag String
    private static final String TAG = "ControlApp";

    // The saved instance state
    private Bundle savedInstanceState;

    /**
     * Default Constructor.
     */
   static  {
        if(ROBOT_INFO == null){
            //TODO alert user to select a robot from RobotChooser Activity
//            Toast.makeText(this, "Robot is not Selected", Toast.LENGTH_LONG).show();
            ROBOT_INFO = new RobotInfo();
            ROBOT_INFO.setMasterUri("http://192.168.43.143:11311");


        }else if(ROBOT_INFO != null){
            //ROBOT_INFO.save(editor);
        }
    }
    public static MainActivity instance;
    public static MainActivity getInstance(){
       if (instance == null){
           instance = new MainActivity();
       }
       return instance;

    }
    public MainActivity() {
        super(NOTIFICATION_TICKER, NOTIFICATION_TITLE, ROBOT_INFO.getUri());

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);




        // Create the RobotController
        controller = new RobotController(this);

        // Hud fragment
        hudFragment = (HUDFragment) getSupportFragmentManager().findFragmentById(R.id.hud_fragment);
        // Find the Joystick fragment
        joystickFragment = (VirtualJoystickFragment) getSupportFragmentManager().findFragmentById(R.id.joystick_fragment);

        this.savedInstanceState = savedInstanceState;

        if (savedInstanceState != null) {
            // Load the controller
            controller.load(savedInstanceState);
        }

    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();

            this.nodeMainExecutor = nodeMainExecutor;
            this.nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //controller.setTopicName(PreferenceManager.getDefaultSharedPreferences(this).getString("edittext_joystick_topic", getString(R.string.joy_topic)));
            controller.initialize(nodeMainExecutor, nodeConfiguration);

            // Add the HUDFragment to the RobotController's odometry listener
            controller.addOdometryListener(hudFragment);


        } catch (Exception e) {
            // Socket problem
            Log.e(TAG, "socket error trying to get networking information from the master uri", e);
        }
    }

    /**
     * @return The RobotController
     */
    public RobotController getRobotController() {
        return controller;
    }

    /**
     * @return the Robot's current ControlMode
     */
    public ControlMode getControlMode() {
        return joystickFragment.getControlMode();
    }

    /**
     * @return The HUDFragment
     */
    public HUDFragment getHUDFragment() {
        return hudFragment;
    }

    /**
     * @return The WarningSystem
     */
    public WarningSystem getWarningSystem() {
        return warningSystem;
    }

    /**
     * Called when a collision is imminent from the WarningSystem.
     */
    public void collisionWarning() {
//        Log.d(TAG, "Collision Warning!");

        hudFragment.warn();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void guideMe(View v){
//        Intent guideActivity = new Intent(this, PlaylistActivity.class);
//        startActivity(guideActivity);
    }
    public void teleopMe(View v){
        Intent controlActivity = new Intent(this,Teleop.class);
        startActivity(controlActivity);
    }
    public void sleepMe(View v){

    }
    public void voiceMe(View v){

    }
    public void videoMe(View v) {
        Intent videoActivity = new Intent(this, VideoPlay.class);
        startActivity(videoActivity);
    }
    public void liveMe(View v){
        Intent liveActivity = new Intent(this,LiveFeed.class);
        startActivity(liveActivity);

    }
    public void contactMe(View v){
        Intent contactActivity = new Intent(this, Contact.class);
        startActivity(contactActivity);

    }
    public void aboutMe(View v) {
        Intent aboutActivity = new Intent(this, About.class);
        startActivity(aboutActivity);
    }
    public void robotChooser(View v){
        Intent robotActivity = new Intent(this, RobotChooser.class);
        startActivity(robotActivity);
    }


}
