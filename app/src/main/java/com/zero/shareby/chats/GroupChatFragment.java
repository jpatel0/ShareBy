package com.zero.shareby.chats;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.zero.shareby.DatabaseReferences;
import com.zero.shareby.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChatFragment extends Fragment {
    private static final String TAG = "GroupChatFragment";
    ArrayList<Chat> chatsData;
    ChatsAdapter chatsAdapter;
    private ChildEventListener mListener=null;
    private DatabaseReference mChatRef;
    private DatabaseReference mGrpRef;

    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView chats_list = view.findViewById(R.id.chats_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        chats_list.setHasFixedSize(true);
        chats_list.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        chats_list.setLayoutManager(layoutManager);

        chatsData =new ArrayList<>();
        chatsAdapter=new ChatsAdapter(chatsData);
        chats_list.setAdapter(chatsAdapter);
        mGrpRef = DatabaseReferences.getGroupReference(getContext());
    }


    private void attachChildListener(){
        if (mListener==null) {
            mListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Chat getChatObject = dataSnapshot.getValue(Chat.class);
                        if (getChatObject.getSentBy().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            getChatObject.setBelongsToCurrentUser(true);
                        else
                            getChatObject.setBelongsToCurrentUser(false);
                        chatsData.add(getChatObject);
                    }
                    chatsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
        }
        mChatRef.addChildEventListener(mListener);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGrpRef == null){
            Toast.makeText(getContext(),"Grp not available",Toast.LENGTH_SHORT).show();
        }else {
            mChatRef = mGrpRef.child("chats");
            chatsAdapter.notifyDataSetChanged();
            Log.d(TAG, mChatRef.toString());
            attachChildListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mListener!=null){
            chatsData.clear();
            mChatRef.removeEventListener(mListener);
            mListener=null;
        }
    }
}
