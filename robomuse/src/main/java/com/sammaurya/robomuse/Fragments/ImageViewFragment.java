package com.sammaurya.robomuse.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.sammaurya.robomuse.Core.RobotController;
import com.sammaurya.robomuse.MainActivity;
import com.sammaurya.robomuse.R;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;
import org.ros.message.MessageListener;

import sensor_msgs.CompressedImage;

public class ImageViewFragment extends RosFragment {

    private RosImageView<CompressedImage> image;
    private TextView noCameraTextView;
    private RobotController controller;
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

        image.setTopicName(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("edittext_camera_topic", getString(R.string.camera_topic)));

        image.setMessageType(sensor_msgs.CompressedImage._TYPE);
        image.setMessageToBitmapCallable(new BitmapFromCompressedImage());

        noCameraTextView = view.findViewById(R.id.noCameraTextView);

        try {
            controller = ((MainActivity) getActivity()).getRobotController();
        }
        catch(Exception ignore){
        }

        // Create a message listener for getting camera data
        if(controller != null){
            controller.setCameraMessageReceivedListener(new MessageListener<CompressedImage>() {
                @Override
                public void onNewMessage(CompressedImage compressedImage) {
                    if (compressedImage != null) {
                        controller.setCameraMessageReceivedListener(null);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noCameraTextView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }

        if (nodeConfiguration != null)
            nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("android/fragment_image_view"));

        return view;
    }


    @Override
    void shutdown() {
        nodeMainExecutor.shutdownNodeMain(image);
    }

}
