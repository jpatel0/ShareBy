package com.zero.shareby.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.models.Post;
import com.zero.shareby.R;
import com.zero.shareby.models.UserDetails;
import com.zero.shareby.utils.Utilities;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingRequestsAdapter extends ArrayAdapter<Post>{

    private ButtonClickListener listener;
    private int mExpandedPosition=-1;
    public interface ButtonClickListener {
        void onReplyButtonClick(String otherUserId);
        void onHaveItemButtonClick(Post post);
    }

    public PendingRequestsAdapter(Context context, ArrayList<Post> postArrayList,ButtonClickListener listener){
        super(context, R.layout.pending_post_item,postArrayList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView=convertView;
        if (convertView==null){
            newView= LayoutInflater.from(getContext()).inflate(R.layout.pending_post_item,parent,false);
        }

        final Post post=getItem(position);
        TextView reqUserTextView=newView.findViewById(R.id.requesting_user_name);
        TextView titleTextView=newView.findViewById(R.id.pending_request_title);
        TextView descriptionTextView=newView.findViewById(R.id.pending_request_description);
        TextView timestampTextView=newView.findViewById(R.id.timestamp_pending_post);
        final Button haveButton = newView.findViewById(R.id.i_have_it);
        final CircleImageView imageView=newView.findViewById(R.id.pending_user_profile_photo);
        final Button replyButton=newView.findViewById(R.id.pending_reply_button);

        reqUserTextView.setText(post.getName());
        titleTextView.setText(post.getTitle());
        if (post.getDesc()!=null){
            descriptionTextView.setText(post.getDesc());
        }
        else
            descriptionTextView.setHeight(0);
        DatabaseReference reqUserRef= FirebaseDatabase.getInstance().getReference()
                .child("UserDetails").child(post.getReqUid());
        reqUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("DashboardAdapter",dataSnapshot.toString());
                UserDetails u=dataSnapshot.getValue(UserDetails.class);
                if (u.getPhotoUrl()!=null){
                    Glide.with(getContext())
                            .load(Uri.parse(u.getPhotoUrl()))
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        timestampTextView.setText(Utilities.calculateTimeDisplay(post.getTimestamp()));

        if (Utilities.getUserUid().equals(post.getReqUid())){
            replyButton.setBackgroundTintList(getContext().getResources().getColorStateList(android.R.color.darker_gray));
            replyButton.setTextColor(getContext().getResources().getColor(android.R.color.white));
            replyButton.setVisibility(View.GONE);
        }

//        handling card collapse
        final ListView postListView = parent.findViewById(R.id.pending_requests_listview);
        final boolean isExpanded = position==mExpandedPosition;
        replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        haveButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        newView.setActivated(isExpanded);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(postListView);
                notifyDataSetChanged();
            }
        });

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),post.getReqUid(),Toast.LENGTH_SHORT).show();
                listener.onReplyButtonClick(post.getReqUid());
            }
        });

        haveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHaveItemButtonClick(post);
                mExpandedPosition=-1;
                replyButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
                haveButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
            }
        });

        return newView;
    }
}
