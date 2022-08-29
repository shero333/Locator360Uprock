package com.hammad.locator360.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.HomeScreen.HomeActivity;
import com.hammad.locator360.databinding.ActivityGeneralScreenTwoBinding;

public class GeneralScreenTwoActivity extends AppCompatActivity {

    private ActivityGeneralScreenTwoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityGeneralScreenTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button continue click listener
        binding.btnContScreenTwo.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}