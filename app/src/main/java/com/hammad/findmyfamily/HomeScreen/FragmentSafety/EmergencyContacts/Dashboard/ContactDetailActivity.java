package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityContactDetailBinding;

public class ContactDetailActivity extends AppCompatActivity {

    ActivityContactDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityContactDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}