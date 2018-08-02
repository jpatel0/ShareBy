package com.zero.shareby;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

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


        providers= Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        final FirebaseDatabase database=FirebaseDatabase.getInstance();

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    //Already Signed in
                    Log.d(TAG,"Auth State is not null");
                    final UserDetails userDetails=new UserDetails();

                    RelativeLayout relParent = new RelativeLayout(LoginActivity.this);
                    RelativeLayout.LayoutParams relParentParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    relParent.setLayoutParams(relParentParam);

                    final ProgressBar pb=new ProgressBar(LoginActivity.this);
                    RelativeLayout.LayoutParams progressBarViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    pb.setLayoutParams(progressBarViewParams);
                    progressBarViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                    relParent.addView(pb);
                    setContentView(relParent, relParentParam);

                    pb.setVisibility(View.VISIBLE);
                    userDetails.setUid(firebaseAuth.getUid());
                    userDetails.setName(firebaseAuth.getCurrentUser().getDisplayName());
                    DatabaseReference dbReference=database.getReference().child("UserDetails");
                    dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG,dataSnapshot.toString());
                            Log.d(TAG,dataSnapshot.getChildrenCount()+"");
                            if(dataSnapshot.hasChild(firebaseAuth.getCurrentUser().getUid())) {

                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                pb.setVisibility(View.INVISIBLE);
                                finish();
                                Log.d(TAG,"yrs");
                            }
                            else {
                                DatabaseReference fd=FirebaseDatabase.getInstance().getReference().child("UserDetails").child(firebaseAuth.getCurrentUser().getUid());
                                fd.setValue(userDetails, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
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


}
