package com.zero.shareby;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PostActivity extends AppCompatActivity {

    Button postButton;
    EditText titleEditText,descriptionEditText;
    SharedPreferences preferences;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        preferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user=FirebaseAuth.getInstance().getCurrentUser();
        titleEditText=findViewById(R.id.title_edit_text);
        descriptionEditText=findViewById(R.id.description_edit_text);
        postButton=findViewById(R.id.post_button);
        postButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        postButton.setTextColor(getResources().getColor(android.R.color.white));
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()>0) {
                    postButton.setEnabled(true);
                    postButton.setTextColor(getResources().getColor(android.R.color.white));
                    postButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                }
                else {
                    postButton.setEnabled(false);
                    postButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
                    postButton.setTextColor(getResources().getColor(android.R.color.white));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMessage();
            }
        });

    }

    private void postMessage() {
        String title=titleEditText.getText().toString().trim();
        String description=null;
        if (descriptionEditText.getText().toString().trim().length()>0){
            description=descriptionEditText.getText().toString().trim();
        }
        if (user!=null){
            String country = preferences.getString(getString(R.string.pref_country), "null");
            String pin = preferences.getString(getString(R.string.pref_pin), "null");
            String key1 = preferences.getString(getString(R.string.pref_key1), "null");
            String key2 = preferences.getString(getString(R.string.pref_key2), "null");

            DatabaseReference uploadPost = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                    .child(key1).child(key2).child("posts");
            Post newPost=new Post(user.getUid(),null,user.getDisplayName(),title,description,1,0);
            uploadPost.push().setValue(newPost).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(PostActivity.this,"Your Post Request Sent",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        else
            Toast.makeText(this,"An Error occured",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case android.R.id.home:
            case R.id.discard_changes_menu:
                finish();
                break;

        }
        return true;
    }
}
