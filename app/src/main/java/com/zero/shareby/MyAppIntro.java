package com.zero.shareby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MyAppIntro extends AppIntro2 {
    private static final String APP_INTRO_KEY="app_intro_check_key";
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferences sharedPreferences;


        pref= PreferenceManager.getDefaultSharedPreferences(this);

        if(!pref.getBoolean(APP_INTRO_KEY,true)){
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }

        addSlide(AppIntro2Fragment.newInstance("Welcome","This is Share NearBy App",R.drawable.society, Color.parseColor("#E91E63")));
        addSlide(AppIntro2Fragment.newInstance("Borrow","Post request to all neighbors instantly",R.drawable.borrow1, Color.parseColor("#9C27B0")));
        addSlide(AppIntro2Fragment.newInstance("Chat within app","Personal chats for privacy",R.drawable.chat_image, Color.parseColor("#2196F3")));
        addSlide(AppIntro2Fragment.newInstance("Go out","Interact to know your neighbours",R.drawable.meet_people, Color.parseColor("#2196F3")));

        //setColorTransitionsEnabled(true);
        setSlideOverAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        gotoLoginActivity();
    }



    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        gotoLoginActivity();
    }

    private void gotoLoginActivity(){
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean(APP_INTRO_KEY,false);
        editor.apply();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }


}
