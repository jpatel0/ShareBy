package com.zero.shareby.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
import com.zero.shareby.R;
import com.zero.shareby.models.UserDetails;

public class UserProfile extends AppCompatActivity {
    private static final String TAG="UserProfile";

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView profileImageView;
    ImageView addressApprovedImageView;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile_new);

         toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionBar=getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }

        mAuth=FirebaseAuth.getInstance();
        collapsingToolbarLayout = findViewById(R.id.profile_collapsing_toolbar);
        progressBar= findViewById(R.id.user_profile_progress_bar);
        profileImageView= findViewById(R.id.profile_image);
        fab = findViewById(R.id.floatingActionButton);
        addressApprovedImageView= findViewById(R.id.address_approved_image_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        if(mAuth.getCurrentUser()!=null){
            collapsingToolbarLayout.setTitle(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl()==null) {
                profileImageView.setImageResource(R.drawable.sign);
                progressBar.setVisibility(View.GONE);
            }
            else{
                Log.d(TAG,"PHOTO:"+mAuth.getCurrentUser().getPhotoUrl().toString());
                Glide.with(this)
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

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserProfile.this,UserSettings.class));
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
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(UserProfile.this).clearDiskCache();
            }
        }).start();
    }
}
