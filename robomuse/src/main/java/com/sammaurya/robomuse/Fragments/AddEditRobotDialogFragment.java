package com.sammaurya.robomuse.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.sammaurya.robomuse.Core.RobotInfo;
import com.sammaurya.robomuse.R;


public class AddEditRobotDialogFragment extends DialogFragment {

    /**
     * Bundle key for position
     */

    public static final String POSITION_KEY = "POSITION_KEY";

    //Temporary RobotInfo used to filled in the AddEditRobotDialogFragment
    private RobotInfo mRobot = new RobotInfo();

    //Use this instance of the interface to deliver action events
    private DialogListener mListener;

    // EditTexts for editing the RobotInfo
    private EditText mRobotNameView;
    private EditText mMasterUriView;
    private View mAdvancedOptionsView;
    private EditText mJoystickTopicView;
    private EditText mLaserScanTopicView;
    private EditText mCameraTopicView;
    private EditText mNavSatTopicView;
    private EditText mOdometryTopicView;
    private EditText mPoseTopicView;
    private CheckBox mReverseLaserScanCheckBox;
    private CheckBox mInvertXAxisCheckBox;
    private CheckBox mInvertYAxisCheckBox;
    private CheckBox mInvertAngularVelocityCheckBox;

    //Position of the RobotInfo in the list of RobotInfos
    private int mPosition = -1;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);

        if(args != null){
            mPosition =args.getInt(POSITION_KEY, -1);
            mRobot.load(args);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        //Verify that the host activity implements the callback interface
        try {
            //Instantiate the DialogListener so we can send events to the host
            mListener = (DialogListener)context;
        }catch (ClassCastException e){
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()  + " must implement DialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View v = inflater.inflate(R.layout.dialog_add_robot,null);

        mRobotNameView = v.findViewById(R.id.robot_name_edit_text_view);
        mMasterUriView = v.findViewById(R.id.master_uri_edit_view);

        CheckBox mAdvancedOptionsCheckbox = v.findViewById(R.id.advanced_options_checkbox_view);
        mAdvancedOptionsView = v.findViewById(R.id.advanced_options_view);
        mJoystickTopicView = v.findViewById(R.id.joystick_topic_edit_text);
        mLaserScanTopicView = v.findViewById(R.id.laser_scan_edit_view);
        mCameraTopicView = v.findViewById(R.id.camera_topic_edit_view);
        mNavSatTopicView = v.findViewById(R.id.navsat_topic_edit_view);
        mOdometryTopicView = v.findViewById(R.id.odometry_topic_edit_view);
        mPoseTopicView = v.findViewById(R.id.pose_topic_edit_view);
        mReverseLaserScanCheckBox = v.findViewById(R.id.reverse_laser_scan_check_box);
        mInvertXAxisCheckBox = v.findViewById(R.id.invert_x_axis_check_box);
        mInvertYAxisCheckBox = v.findViewById(R.id.invert_y_axis_check_box);
        mInvertAngularVelocityCheckBox = v.findViewById(R.id.invert_angular_velocity_check_box);

        mRobotNameView.setText(mRobot.getName());
        mMasterUriView.setText(mRobot.getMasterUri());

        mAdvancedOptionsCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    mAdvancedOptionsView.setVisibility(View.VISIBLE);
                } else {
                    mAdvancedOptionsView.setVisibility(View.GONE);
                }
            }
        });

        mJoystickTopicView.setText(mRobot.getJoystickTopic());
        mLaserScanTopicView.setText(mRobot.getLaserTopic());
        mCameraTopicView.setText(mRobot.getCameraTopic());
        mNavSatTopicView.setText(mRobot.getNavSatTopic());
        mOdometryTopicView.setText(mRobot.getOdometryTopic());
        mPoseTopicView.setText(mRobot.getPoseTopic());
        mReverseLaserScanCheckBox.setChecked(mRobot.isReverseLaserScan());
        mInvertXAxisCheckBox.setChecked(mRobot.isInvertX());
        mInvertYAxisCheckBox.setChecked(mRobot.isInvertY());
        mInvertAngularVelocityCheckBox.setChecked(mRobot.isInvertAngularVelocity());


        builder.setTitle(R.string.add_edit_robot)
                .setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = mRobotNameView.getText().toString().trim();
                        String masterUri = mMasterUriView.getText().toString().trim();
                        String joystickTopic = mJoystickTopicView.getText().toString().trim();
                        String laserScanTopic = mLaserScanTopicView.getText().toString().trim();
                        String cameraTopic = mCameraTopicView.getText().toString().trim();
                        String navsatTopic = mNavSatTopicView.getText().toString().trim();
                        String odometryTopic = mOdometryTopicView.getText().toString().trim();
                        String poseTopic = mPoseTopicView.getText().toString().trim();
                        boolean reverseLaserScan = mReverseLaserScanCheckBox.isChecked();
                        boolean invertX = mInvertXAxisCheckBox.isChecked();
                        boolean invertY = mInvertYAxisCheckBox.isChecked();
                        boolean invertAngVel = mInvertAngularVelocityCheckBox.isChecked();

                        if (masterUri.equals("")) {
                            Toast.makeText(getActivity(), "Master URI required", Toast.LENGTH_SHORT).show();

                        } else if (joystickTopic.equals("") || laserScanTopic.equals("") || cameraTopic.equals("")
                                || navsatTopic.equals("") || odometryTopic.equals("") || poseTopic.equals("")) {
                            Toast.makeText(getActivity(), "All topic names are required", Toast.LENGTH_SHORT).show();

                        } else if (!name.equals("")) {
                            mListener.onAddEditDialogPositiveClick(new RobotInfo(mRobot.getId(), name,
                                    masterUri, joystickTopic, laserScanTopic, cameraTopic, navsatTopic,
                                    odometryTopic, poseTopic, reverseLaserScan, invertX, invertY, invertAngVel), mPosition);
                            Toast.makeText(getActivity(), "Added robot to the list", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Robot name required", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onAddEditDialogNegativeClick(AddEditRobotDialogFragment.this);
                dialog.cancel();
            }
        });

        return builder.create();

    }

    public interface DialogListener {

        void onAddEditDialogPositiveClick(RobotInfo info, int position);

        void onAddEditDialogNegativeClick(DialogFragment dialog);
    }

}
