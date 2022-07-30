package com.hammad.locator360;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.locator360.databinding.ActivityJoinCircleFirstScreenBinding;

public class JoinCircleFirstScreenActivity extends AppCompatActivity {

    private ActivityJoinCircleFirstScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityJoinCircleFirstScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnSignOut.setOnClickListener(v ->{

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,WelcomeScreenActivity.class));
            finish();

        });
    }
}