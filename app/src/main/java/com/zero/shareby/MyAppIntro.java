package com.zero.shareby;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;


public class MyAppIntro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
