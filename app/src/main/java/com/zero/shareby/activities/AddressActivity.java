package com.zero.shareby.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zero.shareby.R;
import com.zero.shareby.models.CreateGroup;
import com.zero.shareby.models.Post;
import com.zero.shareby.models.UserDetails;
import com.zero.shareby.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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
    private Listener listener;
    private String country=null,currentAddressLine,pin;
    private double latitude=0,longitude=0;

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
                            List<Address> addressList;
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
        listener=new Listener();
        displayUserLocationNeededDialog();
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
        listener.setDone(false);
        listener.exists=false;
        currentAddressLine=null;

        if (requestCode==RC_PICKER){
            if (resultCode==RESULT_OK) {
                int i;
                final Place place = PlacePicker.getPlace(this, data);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = new ArrayList<>();
                ArrayList<String> pinCodes = new ArrayList<>();
                try {
                    addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 10);
                    if (addresses.size()>0){
                        for (i=0;i<addresses.size();++i) {
                            pin=addresses.get(i).getPostalCode();
                            if (pin!=null && !pinCodes.contains(pin)) {
                                pinCodes.add(pin);
                            }
                        }
                        latitude=addresses.get(0).getLatitude();
                        longitude=addresses.get(0).getLongitude();
                        country=addresses.get(0).getCountryName();
                        currentAddressLine=addresses.get(0).getAddressLine(0);
                    }

                    Log.d(TAG,"addresses List"+addresses);
                    Log.d(TAG,"pinCodes"+pinCodes);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (addresses.size() > 0 && !pinCodes.isEmpty() && country!=null) {

                    for (i = 0; i < pinCodes.size(); i++) {
                        checkIfGroupExists(country, pinCodes.get(i), latitude, longitude,i==pinCodes.size()-1);
                    }
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            AddressActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addressLine.setText(currentAddressLine);
                                }
                            });
                            while (!listener.isDone()) {
                                try {
                                    Thread.sleep(200);
                                    Log.d(TAG,"inside Run");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.d(TAG, "IsDone,Exists  " + listener.isDone() + listener.ifExists());
                            if (listener.ifExists()) {
                                Log.d(TAG,"inside Group exists block");
                                /*
                                update user address(lat,lng) to UserDetails first
                                delete user from old grp if it exists
                                set user to new group and update preferences
                                 */
                                Utilities.uploadUserLocation(getApplicationContext(), latitude, longitude);
                                addUserToGroup(country, listener.getPin(), listener.getKey1(), listener.getKey2());
                                goToMainActivity();
                                //userDetails are updated in above function
                            } else {
                                Log.d(TAG,"inside not exists block");
                                /*
                                no group exists for that address
                                select address[0] as default and create group for that location
                                update user address(lat,lng) to UserDetails first, change prefs accordingly
                                 */
                                Utilities.uploadUserLocation(getApplicationContext(), latitude, longitude);
                                AddressActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        placeIntentBuilder(2);
                                    }
                                });
                            }
                        }
                    });
                }
                else
                    Toast.makeText(AddressActivity.this, "Couldn't obtain enough info on the provided location", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Log.d(TAG," Map Canceled");
            }
        }
        else if (requestCode==RC_CREATE_GRP){
            Log.d(TAG,"intent for create grp");
            final SharedPreferences.Editor editLoc=mPref.edit();
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
                DatabaseReference createRef=FirebaseDatabase.getInstance().getReference()
                        .child("Groups").child(country).child(pin).child(latString).child(lngString);
                createRef.setValue(new CreateGroup(addresses.get(0).getAddressLine(0),
                        1,2000,latLng.latitude,latLng.longitude));
                writeTokenToGroup(createRef);
                Log.d(TAG,createRef.getKey());
                Post post=new Post(user.getUid(),user.getDisplayName());
                createRef.child("posts").push().setValue(post);
                editLoc.putString(getResources().getString(R.string.pref_country), country);
                editLoc.putString(getResources().getString(R.string.pref_pin), pin);
                editLoc.putString(getResources().getString(R.string.pref_key1), latString);
                editLoc.putString(getResources().getString(R.string.pref_key2), lngString);
                editLoc.apply();
                uploadUserDetails(country,pin,latString,lngString);
                goToMainActivity();
            }
            else{
                // Change back location of user to previous one
                if (mPref.getFloat(getResources().getString(R.string.pref_old_lat),0.0F)!=0.0F){
                    final float latit=mPref.getFloat(getResources().getString(R.string.pref_old_lat),0.0F),
                            longit=mPref.getFloat(getResources().getString(R.string.pref_old_lng),0.0F);
                    FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = database.getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Map<String, Object> latlngMap = new HashMap<>();
                    latlngMap.put("latitude", latit);
                    latlngMap.put("longitude", longit);
                    dbRef.updateChildren(latlngMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Log.d(TAG, "fireBase user location child Update successful");
                            editLoc.putFloat(getResources().getString(R.string.pref_lat),latit);
                            editLoc.putFloat(getResources().getString(R.string.pref_lng),longit);
                            editLoc.apply();
                        }
                    });
                }
                Toast.makeText(this,"Group not created",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void checkIfGroupExists(String country, final String pin, double latitude, double longitude, final boolean theEnd){
        final long lat=Long.parseLong(getConvertedString(latitude));
        final long lng=Long.parseLong(getConvertedString(longitude));
        final DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin);
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long minDiff=Math.abs(lat),diff=5000;
                DataSnapshot thisTree=null;
                String key1=null,key2=null;
                if (dataSnapshot.getChildrenCount()>0) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        diff = Math.abs(Math.abs(Long.parseLong(data.getKey())) - Math.abs(lat));
                        if (diff <= 2000) {
                            if (minDiff > diff) {
                                minDiff = diff;
                                thisTree=data;
                                key1 = data.getKey();

                            }
                        }
                    }
                    if (diff > 2000) {
                        listener.setDone(false);
                    } else if (key1!=null){
                        Log.d(TAG," key1 :"+key1);
                        minDiff=Math.abs(lng);
                        for (DataSnapshot getLngNode:thisTree.getChildren()){
                            diff = Math.abs(Math.abs(Long.parseLong(getLngNode.getKey())) - Math.abs(lng));
                            if (diff <= 2000) {
                                if (minDiff > diff) {
                                    minDiff = diff;
                                    key2 = getLngNode.getKey();
                                }
                            }
                        }
                        if (diff > 2000) {
                            listener.setDone(false);
                        }
                        else if(key2!=null){
                            Log.d(TAG,"key2:"+key2);
                            listener.onResult(pin,key1,key2,true,true);
                        }
                    }
                }
                if (theEnd)
                    listener.setDone(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void addUserToGroup(String country,String pin,String key1,String key2){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin);
        //groupsRef.child(key1).child(key2).child("members").child(user.getUid()).setValue(true);


        //remove user data from old group
        String k1=mPref.getString(getResources().getString(R.string.pref_key1),"nope"),
                k2=mPref.getString(getResources().getString(R.string.pref_key2),"nope");
        if ((!k1.equals("nope") || !k2.equals("nope")) && oldVsNewDiff(key1,key2,k1,k2)){
            String count=mPref.getString(getResources().getString(R.string.pref_country),"nope"),
                    pinn=mPref.getString(getResources().getString(R.string.pref_pin),"nope");
            DatabaseReference delOldGroup=FirebaseDatabase.getInstance().getReference().child("Groups").child(count).child(pinn)
                    .child(k1).child(k2);
            delOldGroup.child("members").child(user.getUid())
                    .removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            Toast.makeText(getApplicationContext(),"deleted:"+databaseReference.getKey(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
        //add user to the group
        writeTokenToGroup(groupsRef.child(key1).child(key2));
        groupsRef.child(key1).child(key2).child("posts").push().setValue(new Post(user.getUid(),user.getDisplayName()));
        uploadUserDetails(country,pin,key1,key2);
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
        try {
            if (i == 2) {
                // for creating new group
                displayCreateGroupDialog();
            } else {
                // for user's location
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                startActivityForResult(builder.build(AddressActivity.this), RC_PICKER);
            }
        }catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void uploadUserDetails(final String country,final String pin,final String key1,final String key2){

        Map<String,Object> newGrpData=new HashMap<>();
        newGrpData.put("country",country);
        newGrpData.put("pin",pin);
        newGrpData.put("key1",key1);
        newGrpData.put("key2",key2);
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(newGrpData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferences.Editor editLoc=mPref.edit();
                editLoc.putString(getResources().getString(R.string.pref_country), country);
                editLoc.putString(getResources().getString(R.string.pref_pin),pin);
                editLoc.putString(getResources().getString(R.string.pref_key1),key1);
                editLoc.putString(getResources().getString(R.string.pref_key2),key2);
                editLoc.apply();
            }
        });
    }


    private boolean oldVsNewDiff(String new1,String new2,String old1,String old2){
        int diff1=Math.abs(Integer.parseInt(new1)-Integer.parseInt(old1)),
                diff2=Math.abs(Integer.parseInt(new2)-Integer.parseInt(old2));
        return (diff1>2000 || diff2>2000);
    }



    private void writeTokenToGroup(final DatabaseReference createRef){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        createRef.child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
                    }
                });
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

    private void displayCreateGroupDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Create New Group");
        alertBuilder.setMessage("It seems there isn't any group available in your Area. " +
                "You have to create a group by entering the center point location of your locality(200m radius)");
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(AddressActivity.this), RC_CREATE_GRP);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        }).show();
    }

    private void displayUserLocationNeededDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Needed");
        builder.setMessage("Your Home Location is necessary for finding the exact matching group available in your Area. Set it from google maps");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void goToMainActivity(){
        startActivity(new Intent(AddressActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private static class Listener{
        private boolean exists,isDone;
        private String pin,key1,key2;

        private boolean ifExists() {
            return exists;
        }

        private void onResult(String pin,String key1,String key2,boolean exists,boolean isDone) {
            this.exists = exists;
            this.pin=pin;
            this.key1=key1;
            this.key2=key2;
            this.isDone=isDone;
        }

        private boolean isDone() {
            return isDone;
        }

        private void setDone(boolean done) {
            isDone = done;
        }

        private String getPin() {
            return pin;
        }

        private String getKey1() {
            return key1;
        }

        private String getKey2() {
            return key2;
        }
    }

}