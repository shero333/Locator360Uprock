package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.ActivityCircleManagementBinding;

public class CircleManagementActivity extends AppCompatActivity {

    ActivityCircleManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityCircleManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarCircleManagement.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbarCircleManagement.setTitle(SharedPreference.getCircleName());
    }
}