package com.zero.shareby;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserSettings extends AppCompatActivity {

    private ListView mlistView;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> settings_list;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        mlistView=findViewById(R.id.settings_list_view);
        settings_list=new ArrayList<>();
        settings_list.add("Profile");
        settings_list.add("Log out");
        mAuth=FirebaseAuth.getInstance();
        setTitle("Hi "+ mAuth.getCurrentUser().getDisplayName());
        mAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,settings_list);
        mlistView.setAdapter(mAdapter);

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(UserSettings.this,UserProfile.class));
                        break;
                    case 1:
                        if(mAuth.getCurrentUser()!=null){
                            AuthUI.getInstance().signOut(getApplicationContext());
                            //UserDetails.clearData();
                            startActivity(new Intent(UserSettings.this,LoginActivity.class));
                            finish();
                        }
                        break;
                        default:
                }
            }
        });
    }


}
