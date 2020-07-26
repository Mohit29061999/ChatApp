package com.example.chatapp;

import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String click_action = remoteMessage.getNotification().getClickAction();
        String from_user_id = remoteMessage.getData().get("from_user_id");

       showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),click_action,from_user_id);
       // showNotification(title,message);


    }

    public void showNotification(String title,String message,String click_action,String from_user_id){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"myNotification").setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background).setAutoCancel(true).setContentText(message);

        Intent resultIntent =new Intent(click_action);
        resultIntent.putExtra("user_id",from_user_id);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify((int)System.currentTimeMillis(),builder.build());
    }
}
