package com.hammad.locator360.OneTimeScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hammad.locator360.databinding.ActivityGeneralScreenOneBinding;

public class GeneralScreenOneActivity extends AppCompatActivity {

    private ActivityGeneralScreenOneBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityGeneralScreenOneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}