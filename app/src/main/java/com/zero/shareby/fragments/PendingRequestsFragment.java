package com.zero.shareby.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.zero.shareby.adapters.PendingRequestAdapter;
import com.zero.shareby.models.Post;
import com.zero.shareby.models.UserDetails;
import com.zero.shareby.chats.PeerToPeerChat;
import com.zero.shareby.adapters.PendingRequestsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PendingRequestsFragment extends Fragment implements PendingRequestAdapter.ButtonClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG= "PendingRequestFragment";

    ArrayList<Post> pendingPostsList;
    PendingRequestAdapter postsAdapter;
    SharedPreferences preferences;
    SwipeRefreshLayout swipeRefreshLayout;
    private static PendingRequestsFragment fragment;
    RecyclerView listView;

    public PendingRequestsFragment() {
        // Required empty public constructor
    }

    public static PendingRequestsFragment getInstance() {
        if (fragment==null)fragment = new PendingRequestsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_pending_requests, container, false);
        pendingPostsList=new ArrayList<>();
        preferences= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        listView=rootView.findViewById(R.id.pending_requests_listview);
        postsAdapter=new PendingRequestAdapter(getActivity(),pendingPostsList,this,listView);
        listView.setAdapter(postsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        listView.setLayoutManager(layoutManager);
        swipeRefreshLayout = rootView.findViewById(R.id.pending_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
//        AdView adView = rootView.findViewById(R.id.pending_ad);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onReplyButtonClick(String otherUserId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserDetails")
                .child(otherUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Intent goToChat = new Intent(getActivity(),PeerToPeerChat.class);
                    goToChat.putExtra("userObject",dataSnapshot.getValue(UserDetails.class));
                    startActivity(goToChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onQueryReplyButtonClick(final Post post, String text) {
        if (text.trim().isEmpty())
            Toast.makeText(getActivity(),"Text Field is Empty",Toast.LENGTH_SHORT).show();
        else {
//            post.setRepliedUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
//            post.setRepliedName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            String country = preferences.getString(getString(R.string.pref_country), "null");
            String pin = preferences.getString(getString(R.string.pref_pin), "null");
            String key1 = preferences.getString(getString(R.string.pref_key1), "null");
            String key2 = preferences.getString(getString(R.string.pref_key2), "null");
            DatabaseReference replyReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                    .child(key1).child(key2).child("posts").child(post.getRefKey()).child("replies");
            replyReference.setValue(text).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pendingPostsList.remove(post);
                    TransitionManager.beginDelayedTransition(listView);
                    postsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onHaveItemButtonClick(final Post post) {
        post.setRepliedUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        post.setRepliedName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        String country = preferences.getString(getString(R.string.pref_country), "null");
        String pin = preferences.getString(getString(R.string.pref_pin), "null");
        String key1 = preferences.getString(getString(R.string.pref_key1), "null");
        String key2 = preferences.getString(getString(R.string.pref_key2), "null");
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference().child("Groups").child(country).child(pin)
                .child(key1).child(key2).child("posts").child(post.getRefKey());
        postReference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pendingPostsList.remove(post);
                TransitionManager.beginDelayedTransition(listView);
                postsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updatePendingList(){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !preferences.getString(getString(R.string.pref_key1),"nope").equals("nope") && isAdded()) {
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
                    Log.d(TAG, dataSnapshot.toString() + "\n");
                    for (DataSnapshot posts:dataSnapshot.getChildren()){
                        if (!posts.hasChild("sharedUid")
                                && posts.child("priority").getValue(Integer.class)>0) {
                            Post post = posts.getValue(Post.class);
                            post.setRefKey(posts.getKey());
                            pendingPostsList.add(post);
                        }
                    }
                    Collections.sort(pendingPostsList,Collections.reverseOrder(new Comparator<Post>() {
                        @Override
                        public int compare(Post o1, Post o2) {
                            return Long.compare(o1.getTimestamp(),o2.getTimestamp());
                        }
                    }));
                    postsAdapter.notifyDataSetChanged();
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
        pendingPostsList.clear();
        //postsAdapter.clear();
        updatePendingList();
    }
}
