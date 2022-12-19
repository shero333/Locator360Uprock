package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.hammad.findmyfamily.databinding.ActivityAccountBinding;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarAccount.setNavigationOnClickListener(v -> onBackPressed());
    }
}