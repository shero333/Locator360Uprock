package com.hammad.findmyfamily.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityEmailSignUpBinding;

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

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if(Commons.validateEmailAddress(s)){

                binding.btnContEmailSignUp.setEnabled(true);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.white_rounded_button);
            }

            else if(!Commons.validateEmailAddress(s)){
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