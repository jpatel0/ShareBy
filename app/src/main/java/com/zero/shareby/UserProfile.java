package com.zero.shareby;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class UserProfile extends AppCompatActivity {

    private static final String TAG=UserProfile.class.getSimpleName();
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView profileImageView;
    Button editProfileButton;
    TextView profileName;
    ImageView addressApprovedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.user_profile_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        profileImageView=findViewById(R.id.profile_image);
        editProfileButton=findViewById(R.id.edit_profile_button);
        profileName=findViewById(R.id.name_text_view);
        addressApprovedImageView=findViewById(R.id.address_approved_image_view);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mAuth.getCurrentUser()!=null){
            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            if (mAuth.getCurrentUser().getPhotoUrl()==null) {
                profileImageView.setImageResource(R.drawable.sign);
                progressBar.setVisibility(View.GONE);
            }
            else{
                Log.d(TAG,"PHOTO:"+mAuth.getCurrentUser().getPhotoUrl().toString());
                Glide.with(UserProfile.this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .into(profileImageView);
                progressBar.setVisibility(View.GONE);
            }

            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserProfile.this,EditProfile.class));
                }
            });

            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                    Log.d(TAG,String.valueOf(userDetails.getLatitude()));
                    if (userDetails.getLongitude()==0){
                        addressApprovedImageView.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    }
                    else
                        addressApprovedImageView.setImageResource(R.drawable.approve_icon);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.homeAsUp){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(UserProfile.this).clearDiskCache();
            }
        }).start();
    }
}
