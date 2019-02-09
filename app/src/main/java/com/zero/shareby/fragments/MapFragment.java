package com.zero.shareby.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.models.UserDetails;

public class MapFragment extends Fragment implements LocationListener {

    private static final String TAG="MapFragment";
    public static final int RC_PERMISSIONS=23;
    private GoogleMap mMap;
    private MapView mapView;
    /*LocationManager locationManager;
    String provider;
    Location mlocation;*/
    private boolean isPermissionEnabled=false;
    SharedPreferences preferences;
    DatabaseReference uidReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermissions();
        /*locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        */
        Log.d("map","created");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.activity_maps,container,false);
        mapView=rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getContext());
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG,"Map Created ");
                mMap=googleMap;
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(0,0)));
                mMap.moveCamera(CameraUpdateFactory.zoomBy(15));
                //mlocation=locationManager.getLastKnownLocation(provider);
            }
        });
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        uidReference=FirebaseDatabase.getInstance().getReference().child("UserDetails");
        return rootView;
    }

    private void addMarkerOnMap(String uid){
        uidReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                UserDetails userInfo=dataSnapshot.getValue(UserDetails.class);
                LatLng currLocation=new LatLng(userInfo.getLatitude(),userInfo.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currLocation).title(userInfo.getName()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean askPermissions(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String permissions[] = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_PERMISSIONS);
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
                if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),permissions[i])!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
                isPermissionEnabled=true;
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mMap!=null) {
            mMap.clear();
        }
        /*if (askPermissions()) {
            mlocation = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 20000, 50, this);
        }*/
        if (preferences.getString(getString(R.string.pref_key1), "null").equals("null")) {
            Toast.makeText(getActivity(),"preferences null",Toast.LENGTH_SHORT).show();
            getGroupDatabaseReference();
        } else {
            String country=preferences.getString(getString(R.string.pref_country),"null");
            String pin=preferences.getString(getString(R.string.pref_pin),"null");
            String key1=preferences.getString(getString(R.string.pref_key1),"null");
            String key2=preferences.getString(getString(R.string.pref_key2),"null");
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference()
                    .child("Groups").child(country).child(pin).child(key1).child(key2);
            groupRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()>0){

                        for (DataSnapshot member:dataSnapshot.getChildren()){
                            addMarkerOnMap(member.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double lat=0,lng=0;
                    for (DataSnapshot children:dataSnapshot.getChildren()){
                        if (children.getKey().equals("latitude"))
                            lat=children.getValue(Double.class);
                        else if (children.getKey().equals("longitude"))
                            lng=children.getValue(Double.class);
                    }
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void getGroupDatabaseReference(){
        try {
            uidReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                            try {
                                if (!userDetails.getCountry().equals("null")) {
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString(getString(R.string.pref_country), userDetails.getCountry());
                                    editor.putString(getString(R.string.pref_pin), userDetails.getPin());
                                    editor.putString(getString(R.string.pref_key1), userDetails.getKey1());
                                    editor.putString(getString(R.string.pref_key2), userDetails.getKey2());
                                    editor.apply();
                                    onResume();
                                }
                            }catch (NullPointerException e){
                                Log.d(TAG,"Group data not available at UserDetails");
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }



    @Override
    public void onPause() {
        if (mMap!=null)
            mMap.clear();
        mapView.onPause();
        super.onPause();
        /*if(isPermissionEnabled)
            locationManager.removeUpdates(this);
        */
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onLocationChanged(Location location) {
        /*mMap.clear();
        LatLng currLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("You're here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));*/
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {}

}
