package com.hammad.findmyfamily.OneTimeScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hammad.findmyfamily.databinding.ActivityGeneralScreenOneBinding;

public class GeneralScreenOneActivity extends AppCompatActivity {

    private ActivityGeneralScreenOneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityGeneralScreenOneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button continue click listener
        binding.btnContScreenOne.setOnClickListener(v -> startActivity(new Intent(this,GeneralScreenTwoActivity.class)));
    }
}