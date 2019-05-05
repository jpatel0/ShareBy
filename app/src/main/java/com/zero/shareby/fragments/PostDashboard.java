package com.zero.shareby.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;
import com.zero.shareby.activities.PostActivity;
import com.zero.shareby.models.Post;
import com.zero.shareby.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class PostDashboard extends Fragment implements PostAdapter.MyPostButtonClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG="PostDashboard";
    private ArrayList<Post> data;
    private static PostDashboard fragment;
    SharedPreferences preferences;
    PostAdapter postAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    public PostDashboard() {
    }

    public static PostDashboard getInstance() {
        if (fragment==null)fragment = new PostDashboard();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_post_dashboard, container, false);
        data=new ArrayList<>();
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        /*   fake data
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a hammer","Just a description"));
        data.add(new MyData("I want a stormBreaker","Just a description"));
        data.add(new MyData("I know hammer","Just a description"));*/

        postAdapter=new PostAdapter(getActivity(),data,this);
        ListView listView=rootView.findViewById(R.id.post_dashboard_list_view);
        listView.setAdapter(postAdapter);
        FloatingActionButton newPostButton=rootView.findViewById(R.id.post_floating_button);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),PostActivity.class));
            }
        });
        swipeRefreshLayout = rootView.findViewById(R.id.post_dashboard_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onConfirmButtonClick(final Post post) {
        post.setSharedUid(post.getRepliedUid());
        post.setTimestamp(System.currentTimeMillis());
        String country = preferences.getString(getString(R.string.pref_country), "null");
        String pin = preferences.getString(getString(R.string.pref_pin), "null");
        String key1 = preferences.getString(getString(R.string.pref_key1), "null");
        String key2 = preferences.getString(getString(R.string.pref_key2), "null");
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                .child(key1).child(key2).child("posts").child(post.getRefKey());
        postReference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                data.clear();
                postAdapter.clear();
                updatePostDashboard();
            }
        });
    }

    @Override
    public void onDeleteButtonClick(Post post) {
        String country = preferences.getString(getString(R.string.pref_country), "null");
        String pin = preferences.getString(getString(R.string.pref_pin), "null");
        String key1 = preferences.getString(getString(R.string.pref_key1), "null");
        String key2 = preferences.getString(getString(R.string.pref_key2), "null");
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                .child(key1).child(key2).child("posts").child(post.getRefKey());
        postReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar.make(getActivity().findViewById(R.id.post_dashboard_layout),"Post Removed Successfully",Snackbar.LENGTH_SHORT).show();
                onRefresh();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    private void updatePostDashboard(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !preferences.getString(getString(R.string.pref_key1),"nope").equals("nope")) {
            String country = preferences.getString(getString(R.string.pref_country), "null");
            String pin = preferences.getString(getString(R.string.pref_pin), "null");
            String key1 = preferences.getString(getString(R.string.pref_key1), "null");
            String key2 = preferences.getString(getString(R.string.pref_key2), "null");

            DatabaseReference getMyPosts = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                    .child(key1).child(key2).child("posts");

            Query query = getMyPosts.orderByChild("timestamp");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        Log.d(TAG, dataSnapshot.toString() + "\n");
                        for (DataSnapshot myPost:dataSnapshot.getChildren()){
                            if (myPost.child("reqUid").getValue(String.class).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    && myPost.child("priority").getValue(Integer.class)>0)
                                data.add(myPost.getValue(Post.class));
                        }
                        Collections.sort(data,Collections.reverseOrder(new Comparator<Post>() {
                            @Override
                            public int compare(Post o1, Post o2) {
                                return Long.compare(o1.getTimestamp(),o2.getTimestamp());
                            }
                        }));
                        TransitionManager.beginDelayedTransition((ListView) Objects.requireNonNull(getView()).getRootView().findViewById(R.id.post_dashboard_list_view));
                        postAdapter.notifyDataSetChanged();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        data.clear();
        postAdapter.clear();
        updatePostDashboard();
    }
}
