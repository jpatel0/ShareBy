package com.zero.shareby;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class EditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImageView editImageProfile=findViewById(R.id.edit_profile_image);
        EditText editNameText=findViewById(R.id.edit_profile_name);
        if(auth.getCurrentUser()!=null){
            editNameText.setText(auth.getCurrentUser().getDisplayName());
            editImageProfile.setImageResource(R.drawable.chat_image);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_profile_menu:
                saveChangesToProfile();
                break;
            case R.id.discard_changes_menu:
                finish();
                break;
            case R.id.homeAsUp:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChangesToProfile(){

    }

}
