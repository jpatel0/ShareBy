package com.zero.shareby;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.Preference;
import android.preference.PreferenceManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;


public class AddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG=AddressActivity.class.getSimpleName();
    public static final int RC_PICKER=1353,RC_CREATE_GRP=7828;
    private boolean isPermissionEnabled=false;
    GoogleApiClient mGoogleApiClient;
    private TextView addressLine;
    DatabaseReference databaseReference;
    ChildEventListener mChildListener;
    Button googleMapButton;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        addressLine =findViewById(R.id.address1_edit_text);
        mPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        googleMapButton=findViewById(R.id.google_map_button);
        googleMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeIntentBuilder(1);
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
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_PICKER){
            if (resultCode==RESULT_OK) {
                final Place place = PlacePicker.getPlace(this, data);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = new ArrayList<>();
                String country=null,pin=null;
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 2);
                    country=addresses.get(0).getCountryName();
                    pin=addresses.get(0).getPostalCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.size() > 0 && pin!=null && country!=null) {
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    addressLine.setText(addresses.get(0).getAddressLine(0));
                    Log.d("Map data yeah and loc", addresses.toString() + "\n" + addresses.get(0));
                    DatabaseReference dbRef = database.getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> latlngMap = new HashMap<>();
                    final double latit=addresses.get(0).getLatitude(),longit=addresses.get(0).getLongitude();
                    latlngMap.put("latitude", addresses.get(0).getLatitude());
                    latlngMap.put("longitude", addresses.get(0).getLongitude());
                    dbRef.updateChildren(latlngMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Log.d(TAG, "fireBase user location child Update successful");
                            SharedPreferences.Editor editLoc=mPref.edit();
                            editLoc.putFloat(getResources().getString(R.string.pref_lat),(float) latit);
                            editLoc.putFloat(getResources().getString(R.string.pref_lng),(float) longit);
                            editLoc.apply();
                        }
                    });


                    final long lat=Long.parseLong(getConvertedString(addresses.get(0).getLatitude()));
                    final long lng=Long.parseLong(getConvertedString(addresses.get(0).getLongitude()));
                    final DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin);
                    final String country_key=country,pin_key=pin;
                    groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long minDiff=Math.abs(lat),diff=5000;
                            DataSnapshot thisTree=null;
                            String key1=null,key2=null;
                            if (dataSnapshot.getChildrenCount()>0) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    diff = Math.abs(Long.parseLong(data.getKey())) - Math.abs(lat);
                                    if (diff <= 2300) {
                                        if (minDiff > diff) {
                                            minDiff = diff;
                                            thisTree=data;
                                            key1 = data.getKey();

                                        }
                                    }
                                }
                                if (diff > 2300) {
                                    placeIntentBuilder(2);
                                } else if (key1!=null){
                                    Log.d(TAG," key1 :"+key1);
                                    minDiff=Math.abs(lng);
                                    for (DataSnapshot getLngNode:thisTree.getChildren()){
                                        diff = Math.abs(Long.parseLong(getLngNode.getKey())) - Math.abs(lng);
                                        if (diff <= 2300) {
                                            if (minDiff > diff) {
                                                minDiff = diff;
                                                key2 = getLngNode.getKey();
                                            }
                                        }
                                    }
                                    if (diff > 2300) {
                                        placeIntentBuilder(2);
                                    }
                                    else if(key2!=null){
                                        Log.d(TAG," key2 :"+key2);
                                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                        groupsRef.child(key1).child(key2).child("members").child(user.getUid()).setValue(true);;
                                        groupsRef.child(key1).child(key2).child("posts").push().setValue(new Post(user.getUid(),user.getDisplayName()));
                                        SharedPreferences.Editor editLoc=mPref.edit();
                                        editLoc.putString(getResources().getString(R.string.pref_country), country_key);
                                        editLoc.putString(getResources().getString(R.string.pref_pin),pin_key);
                                        editLoc.putString(getResources().getString(R.string.pref_key1),key1);
                                        editLoc.putString(getResources().getString(R.string.pref_key2),key2);
                                        editLoc.apply();
                                        uploadUserDetails(country_key,pin_key,key1,key2);
                                    }
                                }
                            }
                            else{
                                placeIntentBuilder(2);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(AddressActivity.this, "Address Saved", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(AddressActivity.this, "Coordinates are invalid", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Log.d(TAG," Map Canceled");
            }
        }
        else if (requestCode==RC_CREATE_GRP){
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(this, data);
                LatLng latLng=place.getLatLng();
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = new ArrayList<>();
                String country="",pin="";
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,2);
                    country=addresses.get(0).getCountryName();
                    pin=addresses.get(0).getPostalCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String latString = getConvertedString(latLng.latitude);
                String lngString = getConvertedString(latLng.longitude);
                Log.d(TAG,"country:"+country+"  pin:"+pin+"  lat:"+latLng.latitude);
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference createRef=FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin).child(latString).child(lngString);
                createRef.setValue(new CreateGroup(addresses.get(0).getAddressLine(0),1,2300,latLng.latitude,latLng.longitude));
                createRef.child("members").child(user.getUid()).setValue(true);
                Log.d(TAG,createRef.getKey());
                Post post=new Post(user.getUid(),user.getDisplayName());
                createRef.child("posts").push().setValue(post);
                SharedPreferences.Editor editLoc=mPref.edit();
                editLoc.putString(getResources().getString(R.string.pref_country), country);
                editLoc.putString(getResources().getString(R.string.pref_pin), pin);
                editLoc.putString(getResources().getString(R.string.pref_key1), latString);
                editLoc.putString(getResources().getString(R.string.pref_key2), lngString);
                editLoc.apply();
                uploadUserDetails(country,pin,latString,lngString);
            }
            else
                Toast.makeText(this,"Group not created",Toast.LENGTH_SHORT).show();
        }
    }

    public String getConvertedString(double number){
        String convertedString = Double.toString(number);
        convertedString=convertedString+"000000";
        int index=convertedString.indexOf(".");
        convertedString=convertedString.replace(".", "");
        convertedString=convertedString.substring(0,index+6);
        Log.d(TAG,"Converted String:"+convertedString);
        return convertedString;
    }


    private void placeIntentBuilder(int i){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            if (i == 2) {
                startActivityForResult(builder.build(AddressActivity.this), RC_CREATE_GRP);
            } else {
                startActivityForResult(builder.build(AddressActivity.this), RC_PICKER);
            }
        }catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void uploadUserDetails(String country,String pin,String key1,String key2){
        Map<String,Object> newGrpData=new HashMap<>();
        newGrpData.put("country",country);
        newGrpData.put("pin",pin);
        newGrpData.put("key1",key1);
        newGrpData.put("key2",key2);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(newGrpData);
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