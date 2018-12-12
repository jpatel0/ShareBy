package com.zero.shareby.chats;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.Utils.UserDetails;
import com.zero.shareby.Utils.Utilities;
import com.zero.shareby.customAdapter.GroupContactsAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivity extends AppCompatActivity implements GroupContactsAdapter.MyListenerInterface{

    List<UserDetails> groupContactUsers;
    ListView listView;
    GroupContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        groupContactUsers = new ArrayList<>();
        listView = findViewById(R.id.group_contacts_list_view);
        adapter = new GroupContactsAdapter(this,groupContactUsers,this);
        listView.setAdapter(adapter);
        getContactList();
    }

    private void getContactList(){
        DatabaseReference groupRef = Utilities.getGroupReference(this);
        if (groupRef!=null){
            groupRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                        Log.d("ContactsList",dataSnapshot.toString());
                        for (DataSnapshot userID:dataSnapshot.getChildren()){
                            if (!userID.getKey().equals(Utilities.getUserUid())) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                        .child("UserDetails").child(userID.getKey());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Log.d("ContactsUser",dataSnapshot.toString());
                                        if (dataSnapshot.exists())
                                            groupContactUsers.add(dataSnapshot.getValue(UserDetails.class));
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else Toast.makeText(this,"Couldn't obtain group Data",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void clicked(UserDetails user) {
        Intent startChat = new Intent(this,PeerToPeerChat.class);
        startChat.putExtra("userObject",user);
        startActivity(startChat);
        finish();
    }
}
