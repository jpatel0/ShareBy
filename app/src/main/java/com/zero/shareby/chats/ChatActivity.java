package com.zero.shareby.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class ChatActivity extends AppCompatActivity implements ChatsAdapter.ChatItemClickListener {
    private static final String TAG = "ChatsActivity";
    ArrayList<Chat> chatsData;
    ChatsAdapter chatsAdapter;
    private ChildEventListener mListener=null;
    private DatabaseReference mChatRef;
    private DatabaseReference mGrpRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        RecyclerView chats_list = findViewById(R.id.chats_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chats_list.setHasFixedSize(true);
        chats_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        chats_list.setLayoutManager(layoutManager);

        chatsData =new ArrayList<>();
        chatsAdapter=new ChatsAdapter(this,chatsData);
        chats_list.setAdapter(chatsAdapter);
        mGrpRef = DatabaseReferences.getGroupReference(getApplicationContext());

    }

    @Override
    public void onClick(Chat chat) {
        Log.d(TAG,"click"+chat.getSentBy());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGrpRef == null){
            Toast.makeText(this,"Grp not available",Toast.LENGTH_SHORT).show();
        }else {
            mChatRef = mGrpRef.child("chats");
            chatsAdapter.notifyDataSetChanged();
            Log.d(TAG, mChatRef.toString());
            attachChildListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListener!=null){
            mChatRef.removeEventListener(mListener);
            mListener=null;
        }
    }

    private void attachChildListener(){
        if (mListener==null) {
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        chatsData.add(dataSnapshot.getValue(Chat.class));
                    }
                    chatsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
        }
        mChatRef.addChildEventListener(mListener);

    }

}
