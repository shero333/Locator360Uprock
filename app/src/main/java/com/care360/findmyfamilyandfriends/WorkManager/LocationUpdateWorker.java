package com.care360.findmyfamilyandfriends.WorkManager;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.StartScreen.StartScreenActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Battery.BatteryStatusModelClass;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LocationUpdateWorker extends Worker {

    private static final String TAG = "LOC_WORKER_CLASS";
    private static final int NOTIFICATION_ID = 1000;


    Context context;

    public LocationUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {

        context = getApplicationContext();

        FusedLocationProviderClient mLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if(task.isSuccessful()) {

                Location location = task.getResult();

                if(location != null) {

                    String locationAddress = "";

                    //getting the address of current location
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(addresses != null) {
                        locationAddress = addresses.get(0).getAddressLine(0);
                    }

                    String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

                    BatteryStatusModelClass batteryStatus = Commons.getCurrentBatteryStatus(context);

                    // location data
                    Map<String,Object> locData = new HashMap<>();

                    locData.put(Constants.LAT,location.getLatitude());
                    locData.put(Constants.LNG,location.getLongitude());
                    locData.put(Constants.LOC_ADDRESS,locationAddress);
                    locData.put(Constants.IS_PHONE_CHARGING,batteryStatus.isCharging());
                    locData.put(Constants.BATTERY_PERCENTAGE,batteryStatus.getBatteryPercentage());
                    locData.put(Constants.LOC_TIMESTAMP,String.valueOf(System.currentTimeMillis()));

                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(Objects.requireNonNull(currentUserEmail))
                            .collection(Constants.LOCATION_COLLECTION)
                            .document()
                            .set(locData)
                            .addOnSuccessListener(unused -> Log.i(TAG, "worker called: successful location update."))
                            .addOnFailureListener(e -> Log.e(TAG, "worker called: error -> "+e.getMessage()));

                    // updates the values of location in USER collection
                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(currentUserEmail)
                            .update(locData)
                            .addOnSuccessListener(unused -> Log.i(TAG, " successful location updated in USER collection"))
                            .addOnFailureListener(e -> Log.e(TAG, "error. updating location data in USER collection: " + e.getMessage()));

                    //notify
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        showNotification(locationAddress);
//                    }
                }
            }
        });

        return Result.success();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("UnspecifiedImmutableFlag")
    public void showNotification(String address) {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager nMN = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = null;
        Intent resultIntent = new Intent(context, StartScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("com.care360.findmyfamilyandfriends","updated location!", NotificationManager.IMPORTANCE_HIGH);
        }
        Notification n  = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            n = new Notification.Builder(context)
                    .setContentTitle("Location Update")
                    .setContentText(address)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setVibrate(new long[] { 1000, 1000 })
                    .setLights(Color.RED, 700, 500)
                    .setSound(alarmSound)
                    .setOngoing(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setChannelId("com.care360.findmyfamilyandfriends")
                    .setContentIntent(pendingIntent)
                    //.setStyle(new NotificationCompat.BigTextStyle(NorProv))
                    .build();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nMN.createNotificationChannel(channel);
        }
        nMN.notify(NOTIFICATION_ID, n);
    }
}
