package com.zero.shareby.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.zero.shareby.Utils.UserDetails;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatsAdapter extends RecyclerView.Adapter<RecentChatsAdapter.RecentChatViewHolder> {

    private Context context;
    private ArrayList<String> uid;
    private ClickListener listener;
    public interface ClickListener{
        void onItemClick(String uid);
    }

    public RecentChatsAdapter(Context context,ArrayList<String> uid,ClickListener listener) {
        this.uid=uid;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentChatViewHolder(LayoutInflater.from(context).inflate(R.layout.recent_chat_list_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentChatViewHolder holder, int position) {
        String friendId = uid.get(position);
        DatabaseReference userIdReference = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(friendId);
        userIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    UserDetails user = dataSnapshot.getValue(UserDetails.class);
                    if (user!=null){
                        holder.friend_name.setText(user.getName());
                        Glide.with(context).load(user.getPhotoUrl()).into(holder.friend_profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        if (uid!=null)
            return uid.size();
        return 0;
    }

    public class RecentChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CircleImageView friend_profile_image;
        TextView friend_name;
        public RecentChatViewHolder(View itemView) {
            super(itemView);
            friend_name = itemView.findViewById(R.id.friend_name);
            friend_profile_image = itemView.findViewById(R.id.friend_profile_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(uid.get(getAdapterPosition()));
        }
    }
}
