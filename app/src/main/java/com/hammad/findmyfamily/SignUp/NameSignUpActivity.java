package com.hammad.findmyfamily.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.ActivityNameSignUpBinding;

public class NameSignUpActivity extends AppCompatActivity {

    private ActivityNameSignUpBinding binding;
    private boolean isFirstNameEntered,isLastNameEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityNameSignUpBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //TextWatchers
        binding.edtFirstNameSignUp.addTextChangedListener(firstNameTextWatcher);

        binding.edtLastNameSignUp.addTextChangedListener(lastNameTextWatcher);

        binding.btnContName.setOnClickListener(v -> buttonClickListener());

    }

    private TextWatcher firstNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if(s.length() > 0) {
                isFirstNameEntered = true;
            }
            else if(s.length() == 0){
                isFirstNameEntered = false;
            }

            /*
                function for checking the length of both edit texts (first & last name)
                If length of both are greater than zero, enables the 'continue'
            */
            enableContinueButton();

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private TextWatcher lastNameTextWatcher =new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if(s.length() > 0) {
                isLastNameEntered = true;
            }
            else if(s.length() == 0){
                isLastNameEntered = false;
            }

            /*
                function for checking the length of both edit texts (first & last name)
                If length of both are greater than zero, enables the 'continue'
            */
            enableContinueButton();

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void enableContinueButton(){
        //if true, enables the continue button
        if(isFirstNameEntered && isLastNameEntered){

            binding.btnContName.setEnabled(true);
            binding.btnContName.setBackgroundResource(R.drawable.white_rounded_button);
        }
        else if(!isFirstNameEntered || !isLastNameEntered){
            binding.btnContName.setEnabled(false);
            binding.btnContName.setBackgroundResource(R.drawable.disabled_round_button);
        }
    }

    private void buttonClickListener(){

        String firstName = binding.edtFirstNameSignUp.getText().toString().trim();

        String lastName = binding.edtLastNameSignUp.getText().toString().trim();

        //saving values to shared preference
        SharedPreference.setFirstNamePref(firstName);
        SharedPreference.setLastNamePref(lastName);

        //navigating to next activity
        startActivity(new Intent(this,EmailSignUpActivity.class));
    }
}