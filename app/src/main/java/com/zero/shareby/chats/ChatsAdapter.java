package com.zero.shareby.chats;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.UserDetails;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter {
    private ArrayList<Chat> mChatList;
    private Context context;

    public ChatsAdapter(Context context,ArrayList<Chat> chats){
        mChatList = chats;
        this.context=context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case 0:
                v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_message_layout, parent, false);
                return new MyMessageViewHolder(v);

            default:
                v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.their_message_layout, parent, false);
                return new TheirMessageViewHolder(v);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Chat chatObj=mChatList.get(position);
        if (chatObj.isBelongsToCurrentUser()){
            ((MyMessageViewHolder) holder).myMessage.setText(chatObj.getMessage());
        }else {
            DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(chatObj.getSentBy());
            otherUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        UserDetails otherUser = dataSnapshot.getValue(UserDetails.class);
                        if (otherUser.getPhotoUrl()!=null)
                            Glide.with(context).load(Uri.parse(otherUser.getPhotoUrl())).into(((TheirMessageViewHolder)holder).theirAvatar);
                        ((TheirMessageViewHolder) holder).theirName.setText(otherUser.getName());
                        ((TheirMessageViewHolder) holder).theirMessage.setText(chatObj.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mChatList==null)
        return 0;
        else return mChatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatList.get(position).isBelongsToCurrentUser()){
            return 0;
        }else return 1;
    }

    public class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView myMessage;
        public MyMessageViewHolder(View layoutView) {
            super(layoutView);
            myMessage= layoutView.findViewById(R.id.my_message_body);
        }

    }

    public class TheirMessageViewHolder extends RecyclerView.ViewHolder {
        CircleImageView theirAvatar;
        TextView theirMessage,theirName;
        public TheirMessageViewHolder(View layoutView) {
            super(layoutView);
            theirAvatar = layoutView.findViewById(R.id.their_avatar);
            theirName= layoutView.findViewById(R.id.their_name);
            theirMessage= layoutView.findViewById(R.id.their_message_body);
        }

    }
}
