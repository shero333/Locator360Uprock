package com.care360.findmyfamilyandfriends.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.databinding.ActivityGeneralScreenOneBinding;

public class GeneralScreenOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        ActivityGeneralScreenOneBinding binding = ActivityGeneralScreenOneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button continue click listener
        binding.btnContScreenOne.setOnClickListener(v -> startActivity(new Intent(this,GeneralScreenTwoActivity.class)));
    }
}