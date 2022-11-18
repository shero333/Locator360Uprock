package com.hammad.findmyfamily.WorkManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.BatteryStatusModelClass;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LocationUpdateWorker extends Worker {

    private static final String TAG = "LOC_WORKER_CLASS";
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

                }
            }

        });

        return Result.success();
    }

}
