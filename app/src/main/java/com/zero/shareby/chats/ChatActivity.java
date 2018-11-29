package com.zero.shareby.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.zero.shareby.DatabaseReferences;
import com.zero.shareby.R;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity  {
    private static final String TAG = "ChatsActivity";

    ViewPager viewPager;
    ChatPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        viewPager= findViewById(R.id.chat_view_pager);
        pagerAdapter=new ChatPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tab=findViewById(R.id.chat_tab_layout);
        tab.setupWithViewPager(viewPager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setCurrentItem(0);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }



}
