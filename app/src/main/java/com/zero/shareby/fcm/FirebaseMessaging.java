package com.zero.shareby.fcm;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zero.shareby.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessaging extends FirebaseMessagingService {
    public static final String CHANNEL_ID="myChannel";
    public FirebaseMessaging() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("inside","yes");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0,250,250,250});

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(22, mBuilder.build());

        Log.d("dataChat",remoteMessage.getData().toString()+"\n"+remoteMessage.getNotification().getTitle());
        try
        {
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.d("JSON_OBJECT", object.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }


    @Override
    public void onNewToken(String s) {
        uploadDeviceTokenId(s);

        super.onNewToken(s);
    }

    public static void uploadDeviceTokenId(String id){
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference()
                    .child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("tokenId");
            userReference.setValue(id).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Token Id", "generated");
                }
            });
        }

    }

}
