package com.sammaurya.robomuse.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.sammaurya.robomuse.R;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;

import sensor_msgs.CompressedImage;

public class ImageViewFragment extends RosFragment {

    private RosImageView<CompressedImage> image;
    /**
     * Default Constructor.
     */
    public ImageViewFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_image_view, container, false);

        //ImageView
        image = view.findViewById(R.id.image);
        image.setTopicName("/camera/rgb/image_color/compressed");
        image.setMessageType(sensor_msgs.CompressedImage._TYPE);
        image.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        if (nodeConfiguration != null)
            nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("android/fragment_image_view"));

        return view;
    }


    @Override
    void shutdown() {
        nodeMainExecutor.shutdownNodeMain(image);
    }
}
