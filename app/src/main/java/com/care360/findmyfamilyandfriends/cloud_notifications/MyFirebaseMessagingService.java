package com.care360.findmyfamilyandfriends.cloud_notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.care360.findmyfamilyandfriends.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

@SuppressLint({"MissingFirebaseInstanceTokenRefresh","UnspecifiedImmutableFlag"})
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this,notification);

        ringtone.play();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){

            ringtone.setLooping(false);
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100,300,300,300};
        vibrator.vibrate(pattern,-1);

//        int resourceImage = getResources().getIdentifier(Objects.requireNonNull(message.getNotification()).getIcon(),"drawable",getPackageCodePath());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"com.care360.findmyfamilyandfriends");

        builder.setSmallIcon(R.mipmap.ic_launcher_round);

        NotificationManager mnotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String channelId = "com.care360.findmyfamilyandfriends";
            NotificationChannel channel = new NotificationChannel(channelId,"Help someone!",NotificationManager.IMPORTANCE_HIGH);
            mnotificationManager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        mnotificationManager.notify(100,builder.build());
    }
}
