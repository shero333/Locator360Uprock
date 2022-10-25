package com.hammad.findmyfamily.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.OneTimeScreens.JoinCircleFirstScreenActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.ActivityCreatePasswordBinding;

public class CreatePasswordActivity extends AppCompatActivity {

    private ActivityCreatePasswordBinding binding;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreatePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //TextWatcher
        binding.edtPasswordSignUp.addTextChangedListener(passwordTextWatcher);

        //button click listener
        binding.btnContPasswordSignUp.setOnClickListener(v -> buttonClickListener());

    }

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            password =charSequence.toString().trim();

            if(password.length() >= 8) {

                //if password length is correct, then removes the helper text
                binding.layoutPasswordSignUp.setHelperText(" ");

                binding.btnContPasswordSignUp.setEnabled(true);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.white_rounded_button);

            }
            else if(password.length() < 8) {

                //if password length is incorrect, then sets the helper text
                binding.layoutPasswordSignUp.setHelperText(getString(R.string.minimum_8_character_password));

                binding.btnContPasswordSignUp.setEnabled(false);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        //saving password to preference
        SharedPreference.setPasswordPref(password);

        //navigating to next activity
        startActivity(new Intent(this, JoinCircleFirstScreenActivity.class));
        finish();
    }
}