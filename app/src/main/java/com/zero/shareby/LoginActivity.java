package com.zero.shareby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN=123;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();


        providers= Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );



        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    //Already Signed in
                    UserDetails.uid=firebaseAuth.getUid();
                    UserDetails.name=firebaseAuth.getCurrentUser().getDisplayName();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    finish();
                }
                else {
                    //New User Sign up
                    startActivityForResult(AuthUI.getInstance()
                                            .createSignInIntentBuilder()
                                            .setIsSmartLockEnabled(true)
                                            .setTheme(R.style.AppTheme)
                                            .setAvailableProviders(providers)
                                            .setLogo(R.drawable.sign)
                                            .build(),RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                //Signed in successfully
                Toast.makeText(this,"Signed in successfully..",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }

            else
                //Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
                finish();
        }

    }

    /*private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("yeah","back pressed");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected(LoginActivity.this)) buildDialog(LoginActivity.this).show();
        else {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener!=null)
            mAuth.removeAuthStateListener(mAuthStateListener);
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
