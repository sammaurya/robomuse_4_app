package com.sammaurya.robomuse.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.common.collect.Lists;
import com.sammaurya.robomuse.R;

import org.ros.android.view.visualization.VisualizationView;
import org.ros.android.view.visualization.layer.CameraControlLayer;
import org.ros.android.view.visualization.layer.LaserScanLayer;
import org.ros.android.view.visualization.layer.Layer;
import org.ros.android.view.visualization.layer.OccupancyGridLayer;
import org.ros.android.view.visualization.layer.PathLayer;
import org.ros.android.view.visualization.layer.PosePublisherLayer;
import org.ros.android.view.visualization.layer.PoseSubscriberLayer;
import org.ros.android.view.visualization.layer.RobotLayer;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MapViewFragment extends RosFragment {

    private VisualizationView visualizationView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        //MapView
        visualizationView = view.findViewById(R.id.visualization);
        visualizationView.getCamera().jumpToFrame("map");
        visualizationView.onCreate(Lists.<Layer>newArrayList(new CameraControlLayer(),
                new OccupancyGridLayer("map"), new PathLayer("move_base/NavfnROS/plan"), new PathLayer(
                        "move_base_dynamic/NavfnROS/plan"), new LaserScanLayer("base_scan"),
                new PoseSubscriberLayer("simple_waypoints_server/goal_pose"), new PosePublisherLayer(
                        "simple_waypoints_server/goal_pose"), new RobotLayer("base_footprint")));

        if (nodeConfiguration != null)
            nodeMainExecutor.execute(visualizationView, nodeConfiguration.setNodeName("android/fragment_image_view"));

        return view;
    }


    @Override
    public void initialize(NodeMainExecutor mainExecutor, NodeConfiguration nodeConfiguration) {
        super.initialize(mainExecutor, nodeConfiguration);
    }

    @Override
    void shutdown() {
        nodeMainExecutor.shutdownNodeMain(visualizationView);
    }
}
