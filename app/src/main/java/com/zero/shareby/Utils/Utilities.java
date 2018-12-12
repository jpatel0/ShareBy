package com.zero.shareby.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zero.shareby.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    public static void setPreferences(Context context) {
        final SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 1) {
                        UserDetails currentUserObject = dataSnapshot.getValue(UserDetails.class);
                        SharedPreferences.Editor editor = myPreferences.edit();
                        if (dataSnapshot.hasChild("country") && currentUserObject.getCountry() != null && !currentUserObject.getCountry().equals("null"))
                            editor.putString("country", currentUserObject.getCountry());

                        if (dataSnapshot.hasChild("pin") && currentUserObject.getPin() != null && !currentUserObject.getPin().equals("null"))
                            editor.putString("pin", currentUserObject.getPin());

                        if (dataSnapshot.hasChild("key1") && currentUserObject.getKey1() != null && !currentUserObject.getKey1().equals("null"))
                            editor.putString("key1", currentUserObject.getKey1());

                        if (dataSnapshot.hasChild("key2") && currentUserObject.getKey2() != null && !currentUserObject.getKey2().equals("null"))
                            editor.putString("key2", currentUserObject.getKey2());

                        if (dataSnapshot.hasChild("latitutde") && currentUserObject.getLatitude() != 0)
                            editor.putFloat("lat", (float) currentUserObject.getLatitude());

                        if (dataSnapshot.hasChild("longitude") && currentUserObject.getLongitude() != 0)
                            editor.putFloat("lng", (float) currentUserObject.getLongitude());

                        if (dataSnapshot.hasChild("photoUrl") && !currentUserObject.getPhotoUrl().equals(""))
                            editor.putString("photoUrl", currentUserObject.getPhotoUrl());

                        editor.apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public static void uploadUserLocation(final Context context, final double lat, final double lng){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> latLngMap = new HashMap<>();
            latLngMap.put("latitude", lat);
            latLngMap.put("longitude", lng);
            dbRef.updateChildren(latLngMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    float oldLat=mPref.getFloat(context.getResources().getString(R.string.pref_lat),0.0F),
                            oldLng=mPref.getFloat(context.getResources().getString(R.string.pref_lng),0.0F);
                    SharedPreferences.Editor editLoc=mPref.edit();
                    editLoc.putFloat(context.getResources().getString(R.string.pref_old_lat),oldLat);
                    editLoc.putFloat(context.getResources().getString(R.string.pref_old_lng),oldLng);
                    editLoc.putFloat(context.getResources().getString(R.string.pref_lat),(float) lat);
                    editLoc.putFloat(context.getResources().getString(R.string.pref_lng),(float) lng);
                    editLoc.apply();
                }
            });
        }
    }

    public static String calculateTimeDisplay(long timestamp){
        long diff=System.currentTimeMillis()-timestamp;
        /*
            sec(s) ago
            min(s) ago,
            hrs(s) ago,
            day(s) ago,
            rest  dd,mon yy
         */
        String convertedTime="";
        if (diff<60000)
            convertedTime="a few sec(s) ago";
        else if (diff<3600000)
            convertedTime=Long.toString(diff/60000)+" min(s) ago";
        else if (diff<86400000)
            convertedTime=Long.toString(diff/3600000)+" hr(s) ago";
        else if (diff<129600000)
            convertedTime=Long.toString(diff/86400000)+" day(s) ago";
        else{
            convertedTime=DateFormat.format("dd, MMM yy",timestamp).toString();
        }

        return convertedTime;
    }

    public static String compareUid(String currentUserUid,String otherUserUid){
        if (currentUserUid.length()>otherUserUid.length())
            return currentUserUid+otherUserUid;
        else return otherUserUid+currentUserUid;
    }


    public static boolean compareTimestamps(long t1,long t2){
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());

        Date d1,d2;
        try {
            d1 = df.parse(DateFormat.format("dd-MM-yyyy",t1).toString());
            d2 = df.parse(DateFormat.format("dd-MM-yyyy",t2).toString());
            Log.d("Dates:",""+d1+"  "+d2+"  "+d1.compareTo(d2));
            return d1.compareTo(d2)<0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDateString(long timestamp){
        return DateFormat.format("MMM dd, yy",timestamp).toString();
    }

    public static String getTimeString(long timestamp){
        return DateFormat.format("hh:mm aaa",timestamp).toString();
    }

    public static String getUserUid() throws NullPointerException{
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
