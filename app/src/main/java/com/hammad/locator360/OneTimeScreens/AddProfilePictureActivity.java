package com.hammad.locator360.OneTimeScreens;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.databinding.ActivityAddProfilePictureBinding;

public class AddProfilePictureActivity extends AppCompatActivity {

    private ActivityAddProfilePictureBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize view binding
        binding = ActivityAddProfilePictureBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //upload picture click listener
    }

}