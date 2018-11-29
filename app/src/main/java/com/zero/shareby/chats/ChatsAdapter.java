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

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder> {
    private ArrayList<Chat> mChatList;
    private ChatItemClickListener onItemClickListener;

    public interface ChatItemClickListener{
        void onClick(Chat chatObject);
    }

    public ChatsAdapter(ChatItemClickListener listener,ArrayList<Chat> chats){
        mChatList = chats;
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list_item, parent, false);

        return new ChatsViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        Chat chatObj = mChatList.get(position);
        holder.userName.setText(chatObj.getSentBy());
        holder.userImage.setImageResource(R.drawable.ic_home);
    }

    @Override
    public int getItemCount() {
        if (mChatList==null)
        return 0;
        else return mChatList.size();
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView userImage;
        TextView userName;
        public ChatsViewHolder(View layoutView) {
            super(layoutView);
            userImage = layoutView.findViewById(R.id.chat_list_user_image);
            userName = layoutView.findViewById(R.id.chat_list_user_name);
            layoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(mChatList.get(getAdapterPosition()));
        }
    }
}
