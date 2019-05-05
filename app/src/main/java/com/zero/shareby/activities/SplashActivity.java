package com.zero.shareby.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zero.shareby.R;

import static com.zero.shareby.activities.MyAppIntro.APP_INTRO_KEY;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!pref.getBoolean(APP_INTRO_KEY,true)){
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    finish();
                }
                startActivity(new Intent(SplashActivity.this,MyAppIntro.class));
                finish();
            }
        },400);
    }
}
