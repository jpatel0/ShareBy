package com.zero.shareby;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditProfile extends AppCompatActivity {
    private static final String TAG=EditProfile.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageView editImageProfile=findViewById(R.id.edit_profile_image);
        EditText editNameText=findViewById(R.id.edit_profile_name);
        final TextView editAddress=findViewById(R.id.edit_address_text_view);
        Button changeLocationButton=findViewById(R.id.change_location_button);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double lat,lng;
                UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                Log.d(TAG,String.valueOf(userDetails.getLatitude()));
                lat=userDetails.getLatitude();
                lng=userDetails.getLongitude();
                if (userDetails.getLongitude()==0){
                }
                else {
                    Geocoder geocoder = new Geocoder(EditProfile.this, Locale.getDefault());
                    List<Address> addressList = null;
                    try {
                        addressList=geocoder.getFromLocation(lat,lng,3);
                        Log.d(TAG,addressList.toString());
                        editAddress.setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,AddressActivity.class));
            }
        });
        if(auth.getCurrentUser()!=null){
            editNameText.setText(auth.getCurrentUser().getDisplayName());
            editImageProfile.setImageResource(R.drawable.sign);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile_menu:
                saveChangesToProfile();
                break;
            case R.id.discard_changes_menu:
                finish();
                break;
            case R.id.homeAsUp:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesToProfile(){

    }

}
