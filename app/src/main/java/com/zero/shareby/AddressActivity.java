package com.zero.shareby;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG=AddressActivity.class.getSimpleName();
    public static final int RC_PICKER=1353;
    private boolean isPermissionEnabled=false;
    GoogleApiClient mGoogleApiClient;
    private TextView addressLine;
    DatabaseReference databaseReference;
    ChildEventListener mChildListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        addressLine =findViewById(R.id.address1_edit_text);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("UserDetails");
            mChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    double lat=0, lng=0;
                    Log.d(TAG,dataSnapshot.getChildren().toString());
                    if (dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                        Log.d(TAG,String.valueOf(userDetails.getLatitude()));
                        lat=userDetails.getLatitude();
                        lng=userDetails.getLongitude();
                        if (userDetails.getLongitude()==0){
                            addressLine.setText("");
                        }
                        else {
                            Geocoder geocoder = new Geocoder(AddressActivity.this, Locale.getDefault());
                            List<Address> addressList = null;
                            try {
                                addressList=geocoder.getFromLocation(lat,lng,3);
                                Log.d(TAG,addressList.toString());
                                addressLine.setText(addressList.get(0).getAddressLine(0));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            databaseReference.addChildEventListener(mChildListener);
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Button googleMapButton=findViewById(R.id.google_map_button);
        googleMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddressActivity.this),RC_PICKER);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /*private boolean askPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String permissions[] = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, RC_PERMISSIONS);
        }
        else
            isPermissionEnabled=true;
        return isPermissionEnabled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==RC_PERMISSIONS){
            for(int i=0;i<permissions.length;i++){
                if(ActivityCompat.checkSelfPermission(this,permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
                isPermissionEnabled=true;
            }
        }
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_PICKER){
            if (resultCode==RESULT_OK){
                Place place=PlacePicker.getPlace(this,data);
                Geocoder geocoder=new Geocoder(this, Locale.getDefault());
                List<Address> addresses=new ArrayList<>();
                try {
                    addresses=geocoder.getFromLocation(place.getLatLng().latitude,place.getLatLng().longitude,3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addressLine.setText(addresses.get(0).getAddressLine(0));
                Log.i("Map data yeah and loc",addresses.toString()+"\n"+addresses.get(0));
                DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Map<String,Object> latlngMap=new HashMap<>();
                latlngMap.put("latitude",addresses.get(0).getLatitude());
                latlngMap.put("longitude",addresses.get(0).getLongitude());
                dbRef.updateChildren(latlngMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Log.d(TAG,"firebase child Update successful");
                    }
                });
                Toast.makeText(AddressActivity.this,"Address Saved",Toast.LENGTH_SHORT).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Log.i(TAG," Map Canceled");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Problem getting map data",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"connection failed google place picker api");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.d(TAG,"home as up pressed");
                if(addressLine.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Your location cant be empty!!", Toast.LENGTH_LONG).show();
                    return true;
                }
                break;

            default:
                Log.d(TAG,item.getItemId()+"");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG,"back pressed");
        if(addressLine.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Your location cant be empty!!", Toast.LENGTH_LONG).show();
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.addChildEventListener(mChildListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(mChildListener);
    }
}
