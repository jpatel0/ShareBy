package com.zero.shareby.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.TransitionManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.Utils.Utilities;
import com.zero.shareby.models.Post;
import com.zero.shareby.models.UserDetails;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingRequestAdapter extends RecyclerView.Adapter {

    private ArrayList<Post> mPostList;
    private Context context;
    private PendingRequestAdapter.ButtonClickListener listener;
    private int mExpandedPosition=-1;
    RecyclerView recyclerView;
    public interface ButtonClickListener {
        void onReplyButtonClick(String otherUserId);
        void onHaveItemButtonClick(Post post);
        void onQueryReplyButtonClick(Post post,String text);
    }

    public PendingRequestAdapter(Context context, ArrayList<Post> posts,ButtonClickListener listener,RecyclerView view){
        this.mPostList = posts;
        this.context=context;
        this.listener = listener;
        recyclerView = view;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case 0:
                v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pending_post_query_item, parent, false);
                return new PendingQueryVH(v);


            case 1:
                v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pending_post_item, parent, false);
                return new PendingPostVH(v);

            default:
                v=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pending_post_item, parent, false);
                return new PendingPostVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final Post post=mPostList.get(position);

        if (post.getType()==0){
            //query

            ((PendingQueryVH) holder).reqUserTextView.setText(post.getName());
            ((PendingQueryVH) holder).titleTextView.setText(post.getTitle());
            if (post.getDesc()!=null){
                ((PendingQueryVH) holder).descriptionTextView.setText(post.getDesc());
            }
            else
                ((PendingQueryVH) holder).descriptionTextView.setHeight(0);
            DatabaseReference reqUserRef= FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(post.getReqUid());
            reqUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DashboardAdapter",dataSnapshot.toString());
                    UserDetails u=dataSnapshot.getValue(UserDetails.class);
                    if (u.getPhotoUrl()!=null){
                        Glide.with(context)
                                .load(Uri.parse(u.getPhotoUrl()))
                                .into(((PendingQueryVH) holder).imageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

            ((PendingQueryVH) holder).timestampTextView.setText(Utilities.calculateTimeDisplay(post.getTimestamp()));



//        handling card collapse


            if (Utilities.getUserUid().equals(post.getReqUid())){
//            replyButton.setBackgroundTintList(getContext().getResources().getColorStateList(android.R.color.darker_gray));
//            replyButton.setTextColor(getContext().getResources().getColor(android.R.color.white));
                ((PendingQueryVH) holder).view.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow));
                ((PendingQueryVH) holder).replyButton.setVisibility(View.GONE);
                ((PendingQueryVH) holder).replyEditText.setVisibility(View.GONE);
            }else {

                ((PendingQueryVH) holder).view.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
                final boolean isExpanded = position==mExpandedPosition;
                ((PendingQueryVH) holder).replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                ((PendingQueryVH) holder).replyEditText.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                ((PendingQueryVH) holder).view.setActivated(isExpanded);
                ((PendingQueryVH) holder).view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                        TransitionManager.beginDelayedTransition(recyclerView);
                        notifyDataSetChanged();
                    }
                });
                ((PendingQueryVH) holder).replyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onQueryReplyButtonClick(post,((PendingQueryVH) holder).replyEditText.getText().toString());
                        mExpandedPosition=-1;
                        ((PendingQueryVH) holder).replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                        ((PendingQueryVH) holder).replyEditText.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                    }
                });

            }




        }else {

            ((PendingPostVH) holder).reqUserTextView.setText(post.getName());
            ((PendingPostVH) holder).titleTextView.setText(post.getTitle());
            if (post.getDesc()!=null){
                ((PendingPostVH) holder).descriptionTextView.setText(post.getDesc());
            }
            else
                ((PendingPostVH) holder).descriptionTextView.setHeight(0);
            DatabaseReference reqUserRef= FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(post.getReqUid());
            reqUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DashboardAdapter",dataSnapshot.toString());
                    UserDetails u=dataSnapshot.getValue(UserDetails.class);
                    if (u.getPhotoUrl()!=null){
                        Glide.with(context)
                                .load(Uri.parse(u.getPhotoUrl()))
                                .into(((PendingPostVH) holder).imageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });

            ((PendingPostVH) holder).timestampTextView.setText(Utilities.calculateTimeDisplay(post.getTimestamp()));



//        handling card collapse


            if (Utilities.getUserUid().equals(post.getReqUid())){
//            replyButton.setBackgroundTintList(getContext().getResources().getColorStateList(android.R.color.darker_gray));
//            replyButton.setTextColor(getContext().getResources().getColor(android.R.color.white));
                ((PendingPostVH) holder).view.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow));
                ((PendingPostVH) holder).replyButton.setVisibility(View.GONE);
                ((PendingPostVH) holder).haveButton.setVisibility(View.GONE);
            }else {

                ((PendingPostVH) holder).view.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
                final boolean isExpanded = position==mExpandedPosition;
                ((PendingPostVH) holder).replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                ((PendingPostVH) holder).haveButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                ((PendingPostVH) holder).view.setActivated(isExpanded);
                ((PendingPostVH) holder).view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
                        TransitionManager.beginDelayedTransition(recyclerView);
                        notifyDataSetChanged();
                    }
                });
                ((PendingPostVH) holder).replyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, post.getReqUid(), Toast.LENGTH_SHORT).show();
                        listener.onReplyButtonClick(post.getReqUid());
                    }
                });

                ((PendingPostVH) holder).haveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onHaveItemButtonClick(post);
                        mExpandedPosition=-1;
                        ((PendingPostVH) holder).replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                        ((PendingPostVH) holder).haveButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                    }
                });
            }
        }

    }




    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mPostList==null?0:mPostList.size();
    }

    public class PendingPostVH extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView reqUserTextView,titleTextView,descriptionTextView,timestampTextView;
        Button haveButton,replyButton;
        CardView view;

        private PendingPostVH(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.pending_card_view);
            reqUserTextView=itemView.findViewById(R.id.requesting_user_name);
            titleTextView=itemView.findViewById(R.id.pending_request_title);
            descriptionTextView=itemView.findViewById(R.id.pending_request_description);
            timestampTextView=itemView.findViewById(R.id.timestamp_pending_post);
            haveButton = itemView.findViewById(R.id.i_have_it);
            imageView=itemView.findViewById(R.id.pending_user_profile_photo);
            replyButton=itemView.findViewById(R.id.pending_reply_button);
        }

    }

    public class PendingQueryVH extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView reqUserTextView,titleTextView,descriptionTextView,timestampTextView;
        FloatingActionButton replyButton;
        EditText replyEditText;
        CardView view;

        private PendingQueryVH(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.pending_card_view);
            reqUserTextView=itemView.findViewById(R.id.requesting_user_name);
            titleTextView=itemView.findViewById(R.id.pending_request_title);
            descriptionTextView=itemView.findViewById(R.id.pending_request_description);
            timestampTextView=itemView.findViewById(R.id.timestamp_pending_post);
            replyEditText = itemView.findViewById(R.id.pending_reply_edit_text);
            imageView=itemView.findViewById(R.id.pending_user_profile_photo);
            replyButton=itemView.findViewById(R.id.pending_reply_button);
        }

    }


}
