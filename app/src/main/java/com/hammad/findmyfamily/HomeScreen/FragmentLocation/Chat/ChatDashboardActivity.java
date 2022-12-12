package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityChatDashboardBinding;

public class ChatDashboardActivity extends AppCompatActivity {

    ActivityChatDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityChatDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }
}