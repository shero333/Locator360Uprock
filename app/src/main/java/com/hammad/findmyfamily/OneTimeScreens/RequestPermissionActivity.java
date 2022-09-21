package com.hammad.findmyfamily.OneTimeScreens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityRequestPermissionBinding;

public class RequestPermissionActivity extends AppCompatActivity {


    private ActivityRequestPermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiating binding
        binding = ActivityRequestPermissionBinding.inflate(getLayoutInflater());
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
                Log.i("HELLO_123", "PR Screen: permissions allowed");
                //if permissions allowed, navigate to next activity
                navigateToNextActivity();
            }
            else {
                Log.i("HELLO_123", "PR Screen: location denied");

                //navigate to app settings screen
                Commons.navigateToAppSettings(this);
            }
            /*else if(shouldShowRequestPermissionRationale(Manifest.permission_group.LOCATION)) {
                Toast.makeText(this, "Location Permission Denied!", Toast.LENGTH_SHORT).show();
                Log.i("HELLO_123", "PR Screen: permission deny");
            }
            else if(!shouldShowRequestPermissionRationale(Manifest.permission_group.LOCATION)){
                Log.i("HELLO_123", "PR Screen: location denied and don't show again");

                //navigate to app settings screen
                Commons.navigateToAppSettings(this);
            }*/
        }

    }

    private void navigateToNextActivity() {
        startActivity(new Intent(this,GeneralScreenOneActivity.class));
    }


}