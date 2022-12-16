package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencySOS;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Constants;

@SuppressLint({"MissingFirebaseInstanceTokenRefresh","UnspecifiedImmutableFlag"})

public class FirebaseEmergNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            Log.i("TRY_123", "name: "+title);
            Log.i("TRY_123", "message: "+body);
            Log.i("TRY_123", "from: "+remoteMessage.getData().get(Constants.SENDER_ID));
            Log.i("TRY_123", "to: "+remoteMessage.getData().get(Constants.RECEIVER_ID));
            Log.i("TRY_123", "timeStamp: "+remoteMessage.getData().get(Constants.TIMESTAMP));

            //moves to the next activity with data (location coordinates)
            Intent intent = new Intent(this, EmergencyLocationActivity.class);

            //yet to set it right
            intent.putExtra(Constants.FCM_LAT,remoteMessage.getData().get(Constants.FCM_LAT));
            intent.putExtra(Constants.FCM_LNG,remoteMessage.getData().get(Constants.FCM_LNG));
            intent.putExtra(Constants.IS_APP_IN_FOREGROUND,true);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                Notification.Builder notification = new Notification.Builder(this, Constants.CHANNEL_ID_FCM_NOTIFICATION)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.emerg_sos_icon)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID_FCM_NOTIFICATION,
                        "Heads Up Notification",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(0, notification.build());
            }

        }
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }
}
