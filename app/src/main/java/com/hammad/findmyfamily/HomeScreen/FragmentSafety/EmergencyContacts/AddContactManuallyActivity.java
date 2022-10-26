package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.databinding.ActivityAddContactManuallyBinding;

public class AddContactManuallyActivity extends AppCompatActivity {

    ActivityAddContactManuallyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactManuallyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}