package com.care360.findmyfamilyandfriends.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.databinding.ActivityGeneralScreenTwoBinding;

public class GeneralScreenTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        ActivityGeneralScreenTwoBinding binding = ActivityGeneralScreenTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setting the user first name to the title textview
        binding.txtTitleScreenTwo.setText(getString(R.string.title_screen_two_1) + " " + SharedPreference.getFirstNamePref() + " "+getString(R.string.title_screen_two));

        //button continue click listener
        binding.btnContScreenTwo.setOnClickListener(v -> {

            //navigating to Home Screen
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}