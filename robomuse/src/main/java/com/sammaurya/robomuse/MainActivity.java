package com.sammaurya.robomuse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    public void masterChooser(View v){
        Intent masterActivity = new Intent(this,MasterChooser.class);
        startActivity(masterActivity);
    }

}
