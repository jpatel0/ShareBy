package com.zero.shareby;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class UserProfile extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mAuth=FirebaseAuth.getInstance();
        Button editProfileButton=findViewById(R.id.edit_profile_button);
        TextView profileName=findViewById(R.id.name_text_view);

        if(mAuth.getCurrentUser()!=null){
            profileName.setText(mAuth.getCurrentUser().getDisplayName());
            editProfileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UserProfile.this,EditProfile.class));
                }
            });

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.homeAsUp){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
