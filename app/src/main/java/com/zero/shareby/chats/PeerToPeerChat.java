package com.zero.shareby.chats;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.Utils.UserDetails;
import com.zero.shareby.Utils.Utilities;
import com.zero.shareby.adapters.ChatsAdapter;

import java.util.ArrayList;

public class PeerToPeerChat extends AppCompatActivity {


    private static final String TAG = PeerToPeerChat.class.getSimpleName();
    ArrayList<Chat> chatsData;
    ChatsAdapter chatsAdapter;
    private UserDetails friend;
    private ChildEventListener mListener=null;
    private DatabaseReference mChatRef;
    RecyclerView chats_list;
    EditText editMessage;
    private boolean isChatAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_group_chat);

        friend = (UserDetails) getIntent().getSerializableExtra("userObject");

        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(friend.getName());
        }

        checkIfFriendIsAdded();

        chats_list = findViewById(R.id.chats_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        chats_list.setHasFixedSize(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        chats_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        chats_list.setLayoutManager(layoutManager);

        chatsData =new ArrayList<>();
        chatsAdapter=new ChatsAdapter(this,chatsData);
        chats_list.setAdapter(chatsAdapter);

        final ImageButton sendButton = findViewById(R.id.group_chat_send_message_button);

        editMessage = findViewById(R.id.group_chat_edit_text);
        sendButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        sendButton.setEnabled(false);
        chats_list.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    scrollToBottom();
                }
            }
        });
        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>0) {
                    sendButton.setEnabled(true);
                    sendButton.setBackgroundTintList(getResources().getColorStateList(R.color.sendButton));
                }
                else {
                    sendButton.setEnabled(false);
                    sendButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                editMessage.setText("");
                scrollToBottom();
            }
        });
    }


    private void sendMessage(){
        Chat createChatObj = new Chat(Utilities.getUserUid(),friend.getUid(),editMessage.getText().toString(),System.currentTimeMillis());
        mChatRef.push().setValue(createChatObj).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG,"message Sent");
            }
        });
    }

    private void attachChildListener(){
        if (mListener==null) {
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Chat getChatObject = dataSnapshot.getValue(Chat.class);
                        isChatAvailable = true;
                        if (chatsData.size()==0){
                            Chat initialDivider = new Chat();
                            initialDivider.setDivider(true);
                            initialDivider.setTimestamp(getChatObject.getTimestamp());
                            chatsData.add(initialDivider);
                        }else if (Utilities.compareTimestamps(chatsData.get(chatsData.size()-1).getTimestamp(),getChatObject.getTimestamp())){
                            Chat initialDivider = new Chat();
                            initialDivider.setDivider(true);
                            initialDivider.setTimestamp(getChatObject.getTimestamp());
                            chatsData.add(initialDivider);
                        }

                        if (getChatObject.getSentBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            getChatObject.setBelongsToCurrentUser(true);
                        else
                            getChatObject.setBelongsToCurrentUser(false);
                        chatsData.add(getChatObject);
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


    private void checkIfFriendIsAdded(){
        final DatabaseReference recentChatRef = FirebaseDatabase.getInstance().getReference()
                .child("RecentChats").child(Utilities.getUserUid());
        recentChatRef.child(friend.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists() && !isChatAvailable){
                    recentChatRef.child(friend.getUid()).setValue(friend.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mChatRef = FirebaseDatabase.getInstance().getReference().child("RecentChats")
                .child(Utilities.compareUid(Utilities.getUserUid(),friend.getUid()));
        attachChildListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener!=null){
            chatsData.clear();
            mChatRef.removeEventListener(mListener);
            mListener=null;
        }
    }

    private void scrollToBottom(){
        chats_list.postDelayed(new Runnable() {
            @Override
            public void run() {
                chats_list.scrollToPosition(chatsAdapter.getItemCount()-1);
            }
        },300);
    }
}