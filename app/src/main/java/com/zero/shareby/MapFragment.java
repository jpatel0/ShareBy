package com.zero.shareby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements LocationListener,GoogleMap.OnMapLongClickListener {

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
                mMap.setOnMapLongClickListener(MapFragment.this);
                //mlocation=locationManager.getLastKnownLocation(provider);
            }
        });
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        uidReference=FirebaseDatabase.getInstance().getReference().child("UserDetails");
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void addMarkerOnMap(String uid){
        uidReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG,dataSnapshot.toString());
                UserDetails userInfo=dataSnapshot.getValue(UserDetails.class);
                LatLng currLocation=new LatLng(userInfo.getLatitude(),userInfo.getLongitude());
                mMap.addMarker(new MarkerOptions().position(currLocation).title(userInfo.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));
                mMap.moveCamera(CameraUpdateFactory.zoomBy(15));
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
        /*if (askPermissions()) {
            mlocation = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 20000, 50, this);
        }*/
        if (preferences.getString(getString(R.string.pref_key1), "null").equals("null")) {

        } else {
            String country=preferences.getString(getString(R.string.pref_country),"null");
            String pin=preferences.getString(getString(R.string.pref_pin),"null");
            String key1=preferences.getString(getString(R.string.pref_key1),"null");
            String key2=preferences.getString(getString(R.string.pref_key2),"null");
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference()
                    .child("Groups").child(country).child(pin).child(key1).child(key2).child("members");
            groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if(isPermissionEnabled)
            locationManager.removeUpdates(this);
        */
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomBy(2));
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
