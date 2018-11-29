package com.zero.shareby;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseReferences {
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
                    .child(country).child(pin).child(key1).child(key2);
        }
    }
}
