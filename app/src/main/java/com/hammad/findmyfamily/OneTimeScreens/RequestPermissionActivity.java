package com.hammad.findmyfamily.OneTimeScreens;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.R;
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

        //requesting the location permission
        requestLocationPermission();

        //button continue click listener
        binding.btnContReqPermission.setOnClickListener(v -> startActivity(new Intent(this,GeneralScreenOneActivity.class)));

        //remind me later click listener
        binding.txtRemindMeLater.setOnClickListener(v -> startActivity(new Intent(this,GeneralScreenOneActivity.class)));
    }

    private void requestLocationPermission() {

        if(Permissions.hasLocationPermission(this)) {

            //setting the continue button to enabled
            setContinueButtonStatus();
        }
        else if(shouldShowRequestPermissionRationale("Manifest.permission.ACCESS_FINE_LOCATION")){
            Toast.makeText(this, "Location Rationale", Toast.LENGTH_SHORT).show();
        }
        else {
            Permissions.getLocationPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQUEST_CODE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            //setting the button status to enabled
            setContinueButtonStatus();
        }
    }

    private void setContinueButtonStatus(){
        binding.btnContReqPermission.setEnabled(true);
        binding.btnContReqPermission.setBackgroundResource(R.drawable.white_rounded_button);
        binding.btnContReqPermission.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
    }

}