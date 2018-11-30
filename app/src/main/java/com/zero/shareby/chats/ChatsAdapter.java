package com.zero.shareby.chats;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zero.shareby.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter {
    private ArrayList<Chat> mChatList;

    public ChatsAdapter(ArrayList<Chat> chats){
        mChatList = chats;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chatObj=mChatList.get(position);
        if (chatObj.isBelongsToCurrentUser()){
            ((MyMessageViewHolder) holder).myMessage.setText(chatObj.getMessage());
        }else {
            ((TheirMessageViewHolder) holder).theirName.setText(chatObj.getSentBy());
            ((TheirMessageViewHolder) holder).theirMessage.setText(chatObj.getMessage());
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
        View theirAvatar;
        TextView theirMessage,theirName;
        public TheirMessageViewHolder(View layoutView) {
            super(layoutView);
            theirAvatar = layoutView.findViewById(R.id.their_avatar);
            theirName= layoutView.findViewById(R.id.their_name);
            theirMessage= layoutView.findViewById(R.id.their_message_body);
        }

    }
}
