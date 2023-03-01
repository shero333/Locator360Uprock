package com.care360.findmyfamilyandfriends.OneTimeScreens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.BuildConfig;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityRequestPermissionBinding;

public class RequestPermissionActivity extends AppCompatActivity {


    private static final String TAG = "ACT_REQ_PERM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiating binding
        ActivityRequestPermissionBinding binding = ActivityRequestPermissionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button continue click listener
        binding.btnContReqPermission.setOnClickListener(v -> requestLocationPermission());

        //remind me later click listener
        binding.txtRemindMeLater.setOnClickListener(v -> navigateToNextActivity());
    }

    private void requestLocationPermission() {

        if(Permissions.hasLocationPermission(this))
        {
            navigateToNextActivity();
        }
        else {
            Permissions.getLocationPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQUEST_CODE_LOCATION) {

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "location permission allowed");
                //if permissions allowed, navigate to next activity
                navigateToNextActivity();
            }
            else {
                Log.i(TAG, "location permission denied");

                //navigate to app settings screen
                Commons.locationPermissionDialog(this, isSuccessful -> startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID))));
            }
        }

    }

    private void navigateToNextActivity() {
        startActivity(new Intent(this,GeneralScreenOneActivity.class));
    }

}