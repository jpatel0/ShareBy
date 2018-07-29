package com.zero.shareby;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.zero.shareby.MapsActivity.RC_PERMISSIONS;

public class AddressActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    public static final int RC_PICKER=1353;
    private boolean isPermissionEnabled=false;
    private GoogleApiClient mGoogleApiClient;
    private EditText addressLine1;
    private EditText addressLine2;
    private EditText zipCode;
    private EditText city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        addressLine1=findViewById(R.id.address1_edit_text);
        addressLine2=findViewById(R.id.address2_edit_text);
        zipCode=findViewById(R.id.zip_edit_text);
        city=findViewById(R.id.city_edit_text);


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
                addressLine1.setText(addresses.toString());
                zipCode.setText(addresses.get(0).getPostalCode().trim());
                city.setText(addresses.get(0).getLocality());
                if(!place.getAddress().toString().isEmpty())
                    UserDetails.address=addresses.get(0).getAddressLine(0);
                Log.i("Map data yeah",addresses.toString());
            }
            else if (resultCode==RESULT_CANCELED){
                Log.i("Map data","Canceled");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
