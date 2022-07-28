package com.hammad.locator360;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.SignIn.PhoneNoSignInActivity;
import com.hammad.locator360.SignUp.PhoneNoSignUpActivity;
import com.hammad.locator360.databinding.ActivityWelcomeScreenBinding;

public class WelcomeScreenActivity extends AppCompatActivity {

    private ActivityWelcomeScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Button sign up click listener
        binding.btnSignUp.setOnClickListener(v -> startActivity(new Intent(WelcomeScreenActivity.this, PhoneNoSignUpActivity.class)));

        // Textview sign in click listener
        binding.txtSignIn.setOnClickListener(v -> startActivity(new Intent(WelcomeScreenActivity.this, PhoneNoSignInActivity.class)));

    }
}