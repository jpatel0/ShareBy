package com.zero.shareby;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileFragment extends Fragment {

    private static final String TAG="UserProfileFragment";

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView profileImageView;
    Button editProfileButton;
    TextView profileName;
    ImageView addressApprovedImageView;

    public UserProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.show_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        progressBar=view.findViewById(R.id.user_profile_progress_bar);
        profileImageView=view.findViewById(R.id.profile_image);
        editProfileButton=view.findViewById(R.id.edit_profile_button);
        profileName=view.findViewById(R.id.name_text_view);
        addressApprovedImageView=view.findViewById(R.id.address_approved_image_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        if(mAuth.getCurrentUser()!=null){
            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl()==null) {
                profileImageView.setImageResource(R.drawable.sign);
                progressBar.setVisibility(View.GONE);
            }
            else{
                Log.d(TAG,"PHOTO:"+mAuth.getCurrentUser().getPhotoUrl().toString());
                Glide.with(getActivity())
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(profileImageView);
            }

            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),EditProfile.class));
                }
            });

            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                    Log.d(TAG,String.valueOf(userDetails.getLatitude()));
                    if (userDetails.getLongitude()==0){
                        addressApprovedImageView.setImageResource(R.drawable.ic_error);
                    }
                    else
                        addressApprovedImageView.setImageResource(R.drawable.ic_verified);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(getActivity()).clearDiskCache();
            }
        }).start();
    }
}
