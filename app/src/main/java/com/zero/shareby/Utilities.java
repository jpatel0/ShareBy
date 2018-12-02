package com.zero.shareby;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Utilities {

    public static DatabaseReference getGroupReference(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key1 = preferences.getString("key1","nothing");
        if (key1.equals("nothing")){
            return null;
        }else {
            String country = preferences.getString("country","nothing");
            String key2 = preferences.getString("key2","nothing");
            String pin = preferences.getString("pin","nothing");

            return FirebaseDatabase.getInstance().getReference()
                    .child("Groups").child(country).child(pin).child(key1).child(key2);
        }
    }

    public static void setPreferences(Context context){
        final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                .child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>1){
                    UserDetails currentUserObject = dataSnapshot.getValue(UserDetails.class);
                    SharedPreferences.Editor editor = myPreferences.edit();
                    if (dataSnapshot.hasChild("country") && currentUserObject.getCountry()!=null && !currentUserObject.getCountry().equals("null"))
                        editor.putString("country",currentUserObject.getCountry());

                    if (dataSnapshot.hasChild("pin") && currentUserObject.getPin()!=null && !currentUserObject.getPin().equals("null"))
                        editor.putString("pin",currentUserObject.getPin());

                    if (dataSnapshot.hasChild("key1") && currentUserObject.getKey1()!=null && !currentUserObject.getKey1().equals("null"))
                        editor.putString("key1",currentUserObject.getKey1());

                    if (dataSnapshot.hasChild("key2") && currentUserObject.getKey2()!=null && !currentUserObject.getKey2().equals("null"))
                        editor.putString("key2",currentUserObject.getKey2());

                    if (dataSnapshot.hasChild("latitutde") && currentUserObject.getLatitude()!=0)
                        editor.putFloat("lat",(float) currentUserObject.getLatitude());

                    if (dataSnapshot.hasChild("longitude") && currentUserObject.getLongitude()!=0)
                        editor.putFloat("lng",(float) currentUserObject.getLongitude());

                    if (dataSnapshot.hasChild("photoUrl") && !currentUserObject.getPhotoUrl().equals(""))
                        editor.putString("photoUrl",currentUserObject.getPhotoUrl());

                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
