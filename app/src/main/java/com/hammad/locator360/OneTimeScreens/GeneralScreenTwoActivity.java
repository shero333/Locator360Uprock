package com.hammad.locator360.OneTimeScreens;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}