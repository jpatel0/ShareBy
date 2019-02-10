package com.zero.shareby.chats;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.models.UserDetails;
import com.zero.shareby.Utils.Utilities;
import com.zero.shareby.adapters.RecentChatsAdapter;

import java.util.ArrayList;

public class RecentChats extends Fragment implements RecentChatsAdapter.ClickListener {

    //private static final int RC_CONTACT = 10;
    RecyclerView recentChatList;
    private ArrayList<String> friends_Uids;
    private RecentChatsAdapter chatsAdapter;
    SwipeRefreshLayout refreshLayout;
    public RecentChats() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        friends_Uids = new ArrayList<>();
        return inflater.inflate(R.layout.fragment_recent_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recentChatList = view.findViewById(R.id.recent_chats_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recentChatList.setLayoutManager(layoutManager);
        chatsAdapter = new RecentChatsAdapter(getActivity().getApplicationContext(),friends_Uids,this);
        recentChatList.setAdapter(chatsAdapter);
        refreshLayout = view.findViewById(R.id.recent_chats_refresh);
        FloatingActionButton fab = view.findViewById(R.id.recent_chat_fab_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getContactIntent = new Intent(getContext(),ContactListActivity.class);
                startActivity(getContactIntent);
                Toast.makeText(getContext(),"FAB",Toast.LENGTH_SHORT).show();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friends_Uids.clear();
                getFriendList();
            }
        });
        getFriendList();
    }

    @Override
    public void onItemClick(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserDetails")
                .child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Intent goToChat = new Intent(getActivity(),PeerToPeerChat.class);
                    goToChat.putExtra("userObject",dataSnapshot.getValue(UserDetails.class));
                    startActivity(goToChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFriendList(){
        try {
            refreshLayout.setRefreshing(true);
            DatabaseReference recentChatReference = FirebaseDatabase.getInstance().getReference().child("RecentChats").child(Utilities.getUserUid());
            recentChatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                        for (DataSnapshot friendId:dataSnapshot.getChildren()){
                            friends_Uids.add(friendId.getKey());
                        }
                        chatsAdapter.notifyDataSetChanged();
                    }
                    if (refreshLayout.isRefreshing())
                        refreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
