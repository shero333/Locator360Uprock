package com.hammad.locator360.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.Util.Commons;
import com.hammad.locator360.databinding.ActivityEmailSignInBinding;

public class EmailSignInActivity extends AppCompatActivity {

    private ActivityEmailSignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing
        binding = ActivityEmailSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //email TextWatcher
        binding.edtEmailSignIn.addTextChangedListener(emailTextWatcher);

        binding.btnContEmailSignIn.setOnClickListener(v -> buttonClickListener());

        //sign in with phone number
        binding.txtSignInWithNumber.setOnClickListener(v -> {
            startActivity(new Intent(this,PhoneNoSignInActivity.class));
            finish();
        });

    }

    private TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString();

            //validating the email
            if(Commons.validateEmailAddress(s)){

                //enabling the 'continue' button
                binding.btnContEmailSignIn.setEnabled(true);
                binding.btnContEmailSignIn.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else{

                //disable the 'continue' button
                binding.btnContEmailSignIn.setEnabled(false);
                binding.btnContEmailSignIn.setBackgroundResource(R.drawable.disabled_round_button);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener(){
        //saving email in preference
        SharedPreference.setEmailPref(binding.edtEmailSignIn.getText().toString());

        //navigating to next activity
        startActivity(new Intent(this, EnterPasswordSignInActivity.class));
    }
}