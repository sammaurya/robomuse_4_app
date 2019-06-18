package com.sammaurya.robomuse;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

public class VideoPlay extends AppCompatActivity {

    private static final Uri videoURI =
            Uri.parse("android.resource://com.sammaurya.robomuse/raw/dock_robomuse");
    private VideoView mVideoView;
    private boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mVideoView = findViewById(R.id.vvVideo);
        mVideoView.setVideoURI(videoURI);
        isInit = true;
    }

    public void playVideo(View v){
        if(!isInit){
            mVideoView.setVideoURI(videoURI);
            isInit = true;
        }
        mVideoView.start();
    }
    public void pauseVideo(View v){
        if(!mVideoView.isPlaying()){
            return;
        }
        mVideoView.pause();
    }
    public void stopVideo(View v){
        mVideoView.suspend();
        isInit = false;
    }
    @Override
    protected void onPause(){
        stopVideo(null);
        super.onPause();
    }

}
