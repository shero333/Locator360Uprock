package com.hammad.locator360.OneTimeScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hammad.locator360.databinding.ActivityRequestPermissionBinding;

public class RequestPermissionActivity extends AppCompatActivity {


    private ActivityRequestPermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //instantiating binding
        binding = ActivityRequestPermissionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}