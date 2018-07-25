package com.zero.shareby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    public static final String APP_INTRO_KEY="app_intro_check_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        Intent intro=new Intent();
        if(!pref.getBoolean(APP_INTRO_KEY,false)){
            intro.setClass(MainActivity.this, MyAppIntro.class);
            startActivity(intro);
            SharedPreferences.Editor editor=pref.edit();
            editor.putBoolean(APP_INTRO_KEY,true);
            editor.apply();
        }
        else {
            startActivity(new Intent(this, LoginActivity.class));
        }


    }



}
