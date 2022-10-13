package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityAddEmergencyContactBinding;

public class AddEmergencyContactActivity extends AppCompatActivity {

    private ActivityAddEmergencyContactBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityAddEmergencyContactBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnInviteContact.setOnClickListener(v -> Toast.makeText(this, "Invite Contact", Toast.LENGTH_SHORT).show());
    }
}