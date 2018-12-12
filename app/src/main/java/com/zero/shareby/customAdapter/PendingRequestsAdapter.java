package com.zero.shareby.customAdapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.Utils.Post;
import com.zero.shareby.R;
import com.zero.shareby.Utils.UserDetails;
import com.zero.shareby.Utils.Utilities;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingRequestsAdapter extends ArrayAdapter<Post>{

    public PendingRequestsAdapter(Context context, ArrayList<Post> postArrayList){
        super(context, R.layout.pending_post_item,postArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newView=convertView;
        if (convertView==null){
            newView= LayoutInflater.from(getContext()).inflate(R.layout.pending_post_item,parent,false);
        }

        final Post post=getItem(position);
        TextView reqUserTextView=newView.findViewById(R.id.requesting_user_name);
        TextView titleTextView=newView.findViewById(R.id.pending_request_title);
        TextView descriptionTextView=newView.findViewById(R.id.pending_request_description);
        TextView timestampTextView=newView.findViewById(R.id.timestamp_pending_post);
        final CircleImageView imageView=newView.findViewById(R.id.pending_user_profile_photo);
        Button replyButton=newView.findViewById(R.id.pending_reply_button);

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

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Left to implement",Toast.LENGTH_SHORT).show();
            }
        });

        return newView;
    }
}
