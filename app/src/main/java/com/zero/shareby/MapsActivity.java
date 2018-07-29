package com.zero.shareby;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,GoogleMap.OnMapLongClickListener {

    public static final int RC_PERMISSIONS=23;
    private GoogleMap mMap;
    LocationManager locationManager;
    String provider;
    Location mlocation;
    private boolean isPermissionEnabled=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        askPermissions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        Log.d("map","creaat");
    }


    private boolean askPermissions(){
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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        /*mlocation=locationManager.getLastKnownLocation(provider);
        LatLng currLocation=new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("You're here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));*/
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        Log.d("MAp","onResume");
        super.onResume();
        if (askPermissions()) {
            mlocation = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 20000, 50, this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isPermissionEnabled)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Selected this address"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomBy(2));


        //finish();
    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        LatLng currLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("You're here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currLocation));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(18));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {}


}
