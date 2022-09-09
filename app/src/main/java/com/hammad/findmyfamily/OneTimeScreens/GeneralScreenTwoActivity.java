package com.hammad.findmyfamily.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.HomeScreen.HomeActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.ActivityGeneralScreenTwoBinding;

public class GeneralScreenTwoActivity extends AppCompatActivity {

    private ActivityGeneralScreenTwoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityGeneralScreenTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setting the user first name to the title textview
        binding.txtTitleScreenTwo.setText(getString(R.string.title_screen_two_1) + " " + SharedPreference.getFirstNamePref() + " "+getString(R.string.title_screen_two));

        //button continue click listener
        binding.btnContScreenTwo.setOnClickListener(v -> {

            //sign up

            //navigating to Home Screen
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}