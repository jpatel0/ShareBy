package com.zero.shareby;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.customAdapter.PostAdapter;

import java.util.ArrayList;

public class PostDashboard extends Fragment {
    private static final String TAG="PostDashboard";
    private ArrayList<Post> data;
    SharedPreferences preferences;
    PostAdapter postAdapter;
    public PostDashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_post_dashboard, container, false);
        data=new ArrayList<>();
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        /*data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a stormBreaker","Just a description"));
        data.add(new MyData("I know hammer","Just a description"));*/

        postAdapter=new PostAdapter(getActivity(),data);
        ListView listView=rootView.findViewById(R.id.post_dashboard_list_view);
        listView.setAdapter(postAdapter);
        FloatingActionButton newPostButton=rootView.findViewById(R.id.post_floating_button);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostActivity.class));
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        data.clear();
        postAdapter.clear();
        updatePostDashboard();
        super.onResume();
    }

    private void updatePostDashboard(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !preferences.getString(getString(R.string.pref_key1),"nope").equals("nope")) {
            String country = preferences.getString(getString(R.string.pref_country), "null");
            String pin = preferences.getString(getString(R.string.pref_pin), "null");
            String key1 = preferences.getString(getString(R.string.pref_key1), "null");
            String key2 = preferences.getString(getString(R.string.pref_key2), "null");

            DatabaseReference getMyPosts = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                    .child(key1).child(key2).child("posts");

            Query query = getMyPosts.orderByChild("reqUid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, dataSnapshot.toString() + "\n");
                    for (DataSnapshot myPost:dataSnapshot.getChildren()){
                        if (myPost.hasChild("sharedUid"))
                            data.add(myPost.getValue(Post.class));
                    }
                    postAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
