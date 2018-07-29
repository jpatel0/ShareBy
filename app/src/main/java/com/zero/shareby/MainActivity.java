package com.zero.shareby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;



public class MainActivity extends AppCompatActivity{

    public static final String MAP_KEY="map_key";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                }
                else{
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
            }
        };

            if(preferences.getBoolean(MAP_KEY,true)){
                startActivity(new Intent(this,AddressActivity.class));
            }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth!=null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.menu_setting) {
            startActivity(new Intent(this, UserSettings.class));
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile Data or wifi to access this. Press ok to Exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }

}
