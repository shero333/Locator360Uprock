package com.hammad.findmyfamily.HomeScreen;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //constraint location bottom click listener
        binding.consLocHomeScreen.setOnClickListener(v -> bottomConstraintsClickListener());

        //constraint safety bottom click listener
        binding.consSafetyHomeScreen.setOnClickListener(v -> bottomConstraintsClickListener());
    }

    private void bottomConstraintsClickListener(){


    }
}