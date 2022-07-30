package com.hammad.locator360.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.databinding.ActivityEmailSignUpBinding;

public class EmailSignUpActivity extends AppCompatActivity {

    private ActivityEmailSignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityEmailSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //TextWatcher
        binding.edtEmailSignUp.addTextChangedListener(emailTextWatcher);

        //button click listener
        binding.btnContEmailSignUp.setOnClickListener(v -> buttonClickListener());

    }

    private boolean validateEmailAddress(String input) {
        if (!input.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            //enable the continue button
            return true;
        } else {
            //disables the continue button
            return false;
        }
    }

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if(validateEmailAddress(s)){

                binding.btnContEmailSignUp.setEnabled(true);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.white_rounded_button);
            }

            else if(!validateEmailAddress(s)){
                binding.btnContEmailSignUp.setEnabled(false);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener(){

        //saving email in shared preference
        SharedPreference.setEmailPref(binding.edtEmailSignUp.getText().toString().trim());

        //navigating to next activity
        startActivity(new Intent(this,CreatePasswordActivity.class));
    }
}