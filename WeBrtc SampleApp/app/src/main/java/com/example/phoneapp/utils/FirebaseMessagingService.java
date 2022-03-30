package com.example.phoneapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.phoneapp.R;
import com.example.phoneapp.view.videocall.VideoCallActivity;
import com.example.phoneapp.view.voicecall.VoiceCallActivity;
import com.example.phoneapp.viewmodel.FirebaseServiceViewModel;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    /*
    when new device token changes this method will run
    on install, app data cleared new token will be generated.
    */
    FirebaseServiceViewModel viewModel = null;

    public FirebaseMessagingService() {
    /*
     get repository from signleton then pass it to view model
    */
        this.viewModel = new FirebaseServiceViewModel(Singleton.getInstance(Singleton.context).repository);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        //Notify ViewModel that Token has changed.
        viewModel.TokenHasChanged(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        showNotification(title, body);
        if (title.equals("message")) {
            showNotification(title, body);
        } else if (title.equals("videocall")) {


        } else if (title.equals("voicecall")) {


        } else if (title.equals("getOffer")) {
            Singleton.rtcRepository.getOfferfromDb();
        } else if (title.equals("getAnswer")) {
            Singleton.rtcRepository.getAnswersfromDb();
        } else if (title.equals("getCallState")) {
            Singleton.rtcRepository.getCallStatefromDb();
        }else if (title.equals("getOfferCandidate")) {
            Singleton.rtcRepository.getOfferCandidatefromDb();
        }else if (title.equals("getAnswerCandidate")) {
            Singleton.rtcRepository.getAnswerCandidatefromDb();
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("EDM CHANNEL");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);
        notificationbuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        notificationManager.notify(new Random().nextInt(), notificationbuilder.build());
    }
}