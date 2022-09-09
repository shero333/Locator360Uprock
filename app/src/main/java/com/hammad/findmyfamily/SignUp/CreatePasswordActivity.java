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
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityCreatePasswordBinding;

public class CreatePasswordActivity extends AppCompatActivity {

    private ActivityCreatePasswordBinding binding;

    private String encryptedPassword;

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

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s=charSequence.toString().trim();

            if(s.length() >= 8){

                binding.btnContPasswordSignUp.setEnabled(true);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.white_rounded_button);

                //encrypting the password
                encryptedPassword = Commons.encryptedText(s);

            }
            else if(s.length() < 8){

                binding.btnContPasswordSignUp.setEnabled(false);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener(){

        //saving password to preference
        SharedPreference.setPasswordPref(encryptedPassword);

        //navigating to next activity
        startActivity(new Intent(this, JoinCircleFirstScreenActivity.class));
        finish();

    }
}