package com.zero.shareby.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.zero.shareby.R;
import com.zero.shareby.models.UserDetails;
import com.zero.shareby.Utils.Utilities;
import com.zero.shareby.fcm.FirebaseMessaging;

import java.util.Arrays;
import java.util.List;

import static com.zero.shareby.fragments.MapFragment.RC_PERMISSIONS;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN=123;
    private static final String TAG=LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        final SharedPreferences userAvailable= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        providers= Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        final FirebaseDatabase database=FirebaseDatabase.getInstance();

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    //Already Signed in
                    askPermissions();
                    if (!userAvailable.getBoolean("uploaded",false)) {


                        RelativeLayout relParent = new RelativeLayout(LoginActivity.this);
                        RelativeLayout.LayoutParams relParentParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        relParent.setLayoutParams(relParentParam);

                        final ProgressBar pb = new ProgressBar(LoginActivity.this);
                        RelativeLayout.LayoutParams progressBarViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        pb.setLayoutParams(progressBarViewParams);
                        progressBarViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                        relParent.addView(pb);
                        setContentView(relParent, relParentParam);

                        pb.setVisibility(View.VISIBLE);
                        DatabaseReference dbReference = database.getReference().child("UserDetails").child(firebaseAuth.getCurrentUser().getUid());
                        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, dataSnapshot.toString());
                                Log.d(TAG, dataSnapshot.getChildrenCount() + "");
                                if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                                    Utilities.setPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor=userAvailable.edit();
                                    editor.putBoolean("uploaded",true);
                                    editor.commit();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    pb.setVisibility(View.INVISIBLE);
                                    finish();
                                } else {
                                    UserDetails userDetails = new UserDetails();
                                    userDetails.setUid(firebaseAuth.getUid());
                                    userDetails.setName(firebaseAuth.getCurrentUser().getDisplayName());
                                    if (firebaseAuth.getCurrentUser().getPhotoUrl()!=null)
                                        userDetails.setPhotoUrl(firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                                    DatabaseReference fd = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(firebaseAuth.getCurrentUser().getUid());
                                    fd.setValue(userDetails, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.w(TAG, "getInstanceId failed", task.getException());
                                                            return;
                                                        }
                                                        // Get new Instance ID token
                                                        String token = task.getResult().getToken();
                                                        FirebaseMessaging.uploadDeviceTokenId(token);
                                                    }
                                                });
                                            Utilities.setPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor=userAvailable.edit();
                                            editor.putBoolean("uploaded",true);
                                            editor.commit();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            pb.setVisibility(View.INVISIBLE);
                                            finish();
                                        }
                                    });


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }
                else {
                    //New User Sign up
                    startActivityForResult(AuthUI.getInstance()
                                            .createSignInIntentBuilder()
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

                Toast.makeText(LoginActivity.this,"Sign In Successful",Toast.LENGTH_SHORT).show();
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


    private void askPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String permissions[] = {android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, RC_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==RC_PERMISSIONS){
            for(String perm : permissions){
                if(ActivityCompat.checkSelfPermission(this,perm)!=PackageManager.PERMISSION_GRANTED){
                    return;
                }
            }
        }
    }

}
