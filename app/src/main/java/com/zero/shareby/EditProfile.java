package com.zero.shareby;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zero.shareby.Utils.UserDetails;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class EditProfile extends AppCompatActivity {

    private static final String TAG=EditProfile.class.getSimpleName();
    private static final int RC_PICK=567;

    Uri photoUri;
    Uri downloadedUri;
    ImageView editImageProfile;
    EditText editNameText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate called");
        setContentView(R.layout.activity_edit_profile);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        photoUri=null;
        downloadedUri=null;
        progressBar=findViewById(R.id.edit_layout_progress_bar);
        editImageProfile=findViewById(R.id.edit_profile_image);
        editNameText=findViewById(R.id.edit_profile_name);
        final TextView editAddress=findViewById(R.id.edit_address_text_view);
        Button changeLocationButton=findViewById(R.id.change_location_button);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double lat,lng;
                UserDetails userDetails=dataSnapshot.getValue(UserDetails.class);
                Log.d(TAG,String.valueOf(userDetails.getLatitude()));
                lat=userDetails.getLatitude();
                lng=userDetails.getLongitude();
                if (userDetails.getLongitude()==0){
                }
                else {
                    Geocoder geocoder = new Geocoder(EditProfile.this, Locale.getDefault());
                    List<Address> addressList;
                    try {
                        addressList=geocoder.getFromLocation(lat,lng,3);
                        Log.d(TAG,addressList.toString());
                        editAddress.setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,AddressActivity.class));
            }
        });

        editImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto=new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("image/*");
                startActivityForResult(Intent.createChooser(pickPhoto,"Complete action using"),RC_PICK);
            }
        });


        if(auth.getCurrentUser()!=null){
            editNameText.setText(auth.getCurrentUser().getDisplayName());
            if (auth.getCurrentUser().getPhotoUrl()==null) {
                editImageProfile.setImageResource(R.drawable.sign);
                progressBar.setVisibility(View.GONE);
            }
            else{
                Log.d(TAG,"PHOTO"+auth.getCurrentUser().getPhotoUrl().toString());
                Glide.with(EditProfile.this)
                        .load(auth.getCurrentUser().getPhotoUrl())
                        .into(editImageProfile);
                progressBar.setVisibility(View.GONE);
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==RC_PICK && resultCode==RESULT_OK && data!=null){
            photoUri=data.getData();
            try {
                editImageProfile.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, photoUri.toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG,"onBack pressed");
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile_menu:
                saveChangesToProfile();
                break;

            case R.id.discard_changes_menu:

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesToProfile(){
        String name=editNameText.getText().toString().trim();
        if (name.isEmpty()){
            Toast.makeText(this,"Name Field is Empty",Toast.LENGTH_LONG).show();
        }

        else {
            if (photoUri != null) {
                progressBar.setVisibility(View.VISIBLE);

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(
                            photoUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                try {
                    stream.close();
                    stream=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long time=System.currentTimeMillis();
                final StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(String.valueOf(time));
                storageRef.putBytes(byteArray).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (task.isSuccessful()) {
                            String oldFileURL;
                            Uri oldPhotoUrl=FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
                            if (oldPhotoUrl!=null) {
                                Log.d(TAG,oldPhotoUrl.toString());
                                if (!oldPhotoUrl.toString().contains("googleusercontent.com")) {
                                    oldFileURL = oldPhotoUrl.getLastPathSegment();
                                    StorageReference oldRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(oldFileURL.substring(oldFileURL.indexOf("/")));
                                    Log.d(TAG, oldFileURL.substring(oldFileURL.indexOf("/")));
                                    oldRef.delete();
                                }
                            }
                            return storageRef.getDownloadUrl();
                        }
                        else
                            throw task.getException();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        downloadedUri = task.getResult();
                        UserProfileChangeRequest profileUpdates;
                        profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(editNameText.getText().toString().trim())
                                .setPhotoUri(downloadedUri)
                                .build();
                        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                                                    .child("UserDetails").child(user.getUid()).child("photoUrl");
                                            ref.setValue(downloadedUri.toString());
                                            Log.d(TAG, "User profile updated with profile image.");
                                            Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                });
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                final UserProfileChangeRequest profileUpdates;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                DatabaseReference userNameref=FirebaseDatabase.getInstance().getReference().child("UserDetails").child(user.getUid()).child("name");
                userNameref.setValue(name);
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User profile updated w/o image.");
                                    Toast.makeText(EditProfile.this, "Updated", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }
                        });
            }
        }
    }
}
