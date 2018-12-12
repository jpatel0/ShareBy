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
import android.widget.TextView;

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

public class DashboardAdapter extends ArrayAdapter<Post> {

    public DashboardAdapter(@NonNull Context context, ArrayList<Post> list) {
        super(context, R.layout.shared_item_layout,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(getContext());
        View newView=convertView;
        if (convertView==null){
            newView=layoutInflater.inflate(R.layout.shared_item_layout,parent,false);
        }
        final Post post=getItem(position);
        final TextView titleTextView=newView.findViewById(R.id.offering_user);
        final TextView descriptionTextView=newView.findViewById(R.id.offered_to_para);
        TextView timestampTextView=newView.findViewById(R.id.timestamp_dashboard);
        final CircleImageView imageView=newView.findViewById(R.id.offering_user_profile);

        if (post.getPriority()>0) {

            DatabaseReference sharedUserRef= FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(post.getSharedUid());
            sharedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("DashboardAdapter",dataSnapshot.toString());
                    UserDetails u=dataSnapshot.getValue(UserDetails.class);
                    if (u.getPhotoUrl()!=null){
                        Glide.with(getContext())
                                .load(Uri.parse(u.getPhotoUrl()))
                                .into(imageView);
                    }
                    titleTextView.setText(u.getName());
                    String sentence = "shared a/an " + post.getTitle() + " to " + post.getName();
                    descriptionTextView.setText(sentence);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
        else {
            titleTextView.setText(R.string.new_user_applause);
            String sentence = post.getName() + " has joined your Neighborhood";
            descriptionTextView.setText(sentence);
            DatabaseReference sharedUserRef= FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(post.getReqUid()).child("photoUrl");
            sharedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String photoUrl=dataSnapshot.getValue(String.class);
                    if (photoUrl!=null){
                        Glide.with(getContext())
                                .load(Uri.parse(photoUrl))
                                .into(imageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }


        timestampTextView.setText(Utilities.calculateTimeDisplay(post.getTimestamp()));
        return newView;
    }
}
