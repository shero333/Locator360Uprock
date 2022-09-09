package com.hammad.findmyfamily.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.databinding.ActivityJoinGroupBinding;

public class JoinGroupActivity extends AppCompatActivity {

    private ActivityJoinGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding initialization
        binding = ActivityJoinGroupBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //button cancel click listener
        binding.txtCancel.setOnClickListener(v -> finish());

        //button join click listener
        binding.btnJoinGroup.setOnClickListener(v -> joinGroupClickListener());
    }

    private void setGroupIcon(){
    }

    private void joinGroupClickListener(){
        startActivity(new Intent(this, AddProfilePictureActivity.class));
        finish();
    }
}