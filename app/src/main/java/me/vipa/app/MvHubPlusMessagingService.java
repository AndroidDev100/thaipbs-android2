package me.vipa.app;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.pushbase.MoEPushHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import me.vipa.app.activities.splash.ui.ActivitySplash;
import me.vipa.app.utils.helpers.ksPreferenceKeys.KsPreferenceKeys;
import me.vipa.brightcovelibrary.Logger;

public class MvHubPlusMessagingService extends FirebaseMessagingService {
    String assetId,assetType;
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Logger.d("firebase onNewToken: " + token);
        MoEFireBaseHelper.getInstance().passPushToken(MvHubPlusApplication.getInstance(), token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // If the application is in the foreground handle or display both data and notification FCM messages here.
        // Here is where you can display your own notifications built from a received FCM message.
        super.onMessageReceived(remoteMessage);
        Logger.d("From: " + remoteMessage.getFrom());
        Logger.d("Received message: " + remoteMessage.getData());
        if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.getData())) {
            Logger.d("MoEngage notification received");
            MoEFireBaseHelper.getInstance()
                    .passPushPayload(getApplicationContext(), remoteMessage.getData());
        } else if (remoteMessage.getData().size() > 0) {
            JSONObject jsonData = new JSONObject(remoteMessage.getData());
            Logger.d("FCM_Payload: " + new Gson().toJson(remoteMessage));
            Logger.d("FCM_Payload: " + jsonData);
            Logger.d("Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            try {
                JSONObject jsonData = new JSONObject(remoteMessage.getData());
                Logger.d("FCM_Payload_final: " + jsonData);
                if (jsonData.has("id")) {
                    assetId = jsonData.getString("id");
                    if (jsonData.has("contentType")) {
                        assetType = jsonData.getString("contentType");
                    }
                    Logger.d("FCM_Payload_final: " + assetId + " " + assetType);
                    KsPreferenceKeys.getInstance().setNotificationPayload(assetId, jsonData);
                }
            } catch (Exception e) {
                Logger.w(e);
            }

            Logger.d("Message Notification Body: " + new Gson().toJson(remoteMessage.getNotification()));
            displayNotification(remoteMessage.getNotification());
        }
    }

    private void displayNotification(RemoteMessage.Notification notification) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Intent intent = new Intent(this, ActivitySplash.class);
        intent.putExtra("assetId",assetId);
        intent.putExtra("assetType",assetType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent=null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        NotificationCompat.Builder notificationBuilder;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setContentInfo(notification.getTitle())
                    .setLargeIcon(icon)
                    .setColor(this.getResources().getColor(R.color.more_text_color_dark))
                    .setLights(this.getResources().getColor(R.color.more_text_color_dark), 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.drawable.notification_icon) ;
        } else {
              notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent)
                    .setContentInfo(notification.getTitle())
                    .setLargeIcon(icon)
                    .setColor(this.getResources().getColor(R.color.more_text_color_dark))
                    .setLights(this.getResources().getColor(R.color.more_text_color_dark), 1000, 300)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }

        try {
            String picture_url = String.valueOf(notification.getImageUrl());
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            Logger.w(e);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

}
