package com.sammaurya.robomuse.Core.Plans;

import android.content.Context;
import android.util.Log;

import com.sammaurya.robomuse.Core.ControlMode;
import com.sammaurya.robomuse.Core.RobotController;
import com.sammaurya.robomuse.Core.Utils;
import com.sammaurya.robomuse.Teleop;

import org.ros.rosjava_geometry.Vector3;

/**
 * Rudimentary waypoint plan for testing. No collision detection, just moves towards the next waypoint.
 *
 * Created by Nathaniel Stone on 3/4/16.
 */
public class SimpleWaypointPlan extends RobotPlan {

    private static final double MINIMUM_DISTANCE = 1.0;
    private final Context context;

    private static final String TAG = "SimpleWaypointPlan";

    private static final double MAX_SPEED = 0.75;

    /**
     * Creates a SimpleWaypointPlan for the specified ControlApp.
     * @param context The ControlApp
     */
    public SimpleWaypointPlan (Context context) {
        this.context = context;
    }

    /**
     * @return The ControlMode for this RobotPlan
     */
    @Override
    public ControlMode getControlMode() {
        return ControlMode.SimpleWaypoint;
    }

    @Override
    protected void start(RobotController controller) throws Exception {

        Log.d(TAG, "Started");

        Vector3 next;
        double dir, dist, spd;

        while (!isInterrupted()) {

            Log.d(TAG, "begin loop");

            // Wait for the next point to become available
            while (((Teleop)context).getDestination() == null)
                waitFor(1000L);

            next = ((Teleop)context).getDestination();
            Log.d(TAG, "Found next point: (" + next.getX() + ", " + next.getY() + ")");

            spd = 0.0;

            do {
                // Increment speed
                spd += MAX_SPEED / 15.0;
                if (spd > MAX_SPEED)
                    spd = MAX_SPEED;

                // Check angle to target
                dir = Utils.pointDirection(RobotController.getX(), RobotController.getY(), next.getX(), next.getY());
                dir = Utils.angleDifference(RobotController.getHeading(), dir);

                controller.publishVelocity(spd * Math.cos(dir), 0.0, spd * Math.sin(dir));

                // Check distance to target
                dist = Utils.distance(RobotController.getX(), RobotController.getY(), next.getX(), next.getY());

            } while (!isInterrupted() && dist > MINIMUM_DISTANCE && next.equals(((Teleop)context).getDestination()));

            // Stop
            final int N = 15;
            for (int i = N - 1; i >= 0 && !isInterrupted(); --i) {

                // Check angle to target
                dir = Utils.pointDirection(RobotController.getX(), RobotController.getY(), next.getX(), next.getY());
                dir = Utils.angleDifference(RobotController.getHeading(), dir) / 2.0;

                // Slow down
                controller.publishVelocity(spd * ((double)i / N) * Math.cos(dir), 0.0, spd * ((double)i / N) * Math.sin(dir));
                waitFor(N);
            }

            // Remove the way point
            if (!isInterrupted() && next.equals(((Teleop)context).getDestination()))
                ((Teleop)context).pollDestination();
        }
    }
}

