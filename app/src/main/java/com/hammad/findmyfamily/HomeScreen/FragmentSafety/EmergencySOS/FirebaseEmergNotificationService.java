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
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.ChatDashboardActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.ChatDetailActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat.DB.MessageEntity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Constants;

@SuppressLint({"MissingFirebaseInstanceTokenRefresh", "UnspecifiedImmutableFlag"})

public class FirebaseEmergNotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("TRY_123", "onMessageReceived: ");

        if (remoteMessage.getNotification() != null) {

            Log.i("TRY_123", "remote message not null");
            Log.i("TRY_123", "sender id: "+remoteMessage.getData().get(Constants.SENDER_ID));
            Log.i("TRY_123", "receiver id: "+remoteMessage.getData().get(Constants.RECEIVER_ID));

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // if android is 12 or onward, use FLAG IMMUTABLE with notification
            int flag = 0;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                flag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
            }
            else if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S){
                flag = PendingIntent.FLAG_UPDATE_CURRENT;
            }

            // when emergency notification is triggered
            if (remoteMessage.getData().get(Constants.FCM_LAT) != null && remoteMessage.getData().get(Constants.FCM_LNG) != null)
            {

                //moves to the next activity with data (location coordinates)
                Intent intent = new Intent(this, EmergencyLocationActivity.class);

                //yet to set it right
                intent.putExtra(Constants.FCM_LAT, remoteMessage.getData().get(Constants.FCM_LAT));
                intent.putExtra(Constants.FCM_LNG, remoteMessage.getData().get(Constants.FCM_LNG));
                intent.putExtra(Constants.IS_APP_IN_FOREGROUND, true);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

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
            else if (remoteMessage.getData().get(Constants.SENDER_ID) != null && remoteMessage.getData().get(Constants.RECEIVER_ID) != null)
            {
                Log.i("TRY_123", "onMessageReceived: ELSE IF called");
                String senderId,receiverId,message,timestamp;

                senderId = remoteMessage.getData().get(Constants.SENDER_ID);
                receiverId = remoteMessage.getData().get(Constants.RECEIVER_ID);
                message = body;
                timestamp = remoteMessage.getData().get(Constants.TIMESTAMP);

                // this indicates that the user is still in chat detail activity, and updates the messages list
                if (!ChatDetailActivity.isOnline(remoteMessage.getData().get(Constants.SENDER_ID)))
                {
                    Log.i("TRY_123", "if called");
                    //saves the notification in DB
                    saveMessageToDatabase(senderId,receiverId,message,timestamp);

                    Intent intent = new Intent(this, ChatDashboardActivity.class);
                    intent.putExtra(Constants.SENDER_ID, remoteMessage.getData().get(Constants.SENDER_ID));

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flag);

                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        Notification.Builder notification = new Notification.Builder(this, Constants.CHANNEL_ID_FCM_NOTIFICATION)
                                .setContentTitle(title)
                                .setContentText(body)
                                .setSmallIcon(R.drawable.ic_message_24)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID_FCM_NOTIFICATION,
                                "Heads Up Notification",
                                NotificationManager.IMPORTANCE_HIGH);
                        notificationManager.createNotificationChannel(channel);
                        notificationManager.notify(0, notification.build());
                    }

                }
                else {
                    Log.i("TRY_123", "else called");

                    //only saves in DB
                    saveMessageToDatabase(senderId,receiverId,message,timestamp);
                }

                /*//dummy cond
                if (remoteMessage.getData().get(Constants.SENDER_ID) != null) {
                    if (!ChatDetailActivity.isOnline(remoteMessage.getData().get(Constants.SENDER_ID))) {
                        //generate notification and saves in DB
                        Log.i("TRY_123", "if called: ");
                    } else {
                        Log.i("TRY_123", "else called: ");
                        //only saves in DB
                    }
                }*/

            }
        }
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);
    }

    private void saveMessageToDatabase(String senderId, String receiverId, String message, String timestamp) {
        RoomDBHelper
                .getInstance(this)
                .messageDao()
                .saveMessage(new MessageEntity(senderId,receiverId,message,timestamp));
    }
}
