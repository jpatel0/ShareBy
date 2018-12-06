package com.zero.shareby.customAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.RecentChatViewHolder> {

    private Context context;
    private ArrayList<String> uid;

    public RecentChatsAdapter(Context context,ArrayList<String> uid) {
        this.uid=uid;
        this.context = context;
    }

    @NonNull
    @Override
    public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecentChatViewHolder extends RecyclerView.ViewHolder{
        public RecentChatViewHolder(View itemView) {
            super(itemView);
        }
    }
}
