package com.hammad.findmyfamily.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.HomeScreen.HomeActivity;
import com.hammad.findmyfamily.OneTimeScreens.JoinCircleFirstScreenActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityEnterPasswordSignInBinding;

public class EnterPasswordSignInActivity extends AppCompatActivity {

    private ActivityEnterPasswordSignInBinding binding;

    private String encryptedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityEnterPasswordSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setting the user's first name saved in preference from list
        binding.txtUsername.append(" " + SharedPreference.getFirstNamePref() + "?");

        //TextWatcher
        binding.edtPasswordSignIn.addTextChangedListener(passwordTextWatcher);

        //button sign in click listener
        binding.btnContPhoneSignIn.setOnClickListener(v -> buttonClickListener());

        //forget password
        binding.txtForgetPassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        //if the first name retrieved against phone number is incorrect, then moves onto the account verification activity
        binding.txtUsername.setOnClickListener(v -> Toast.makeText(this, "Verifying Account Details", Toast.LENGTH_SHORT).show());
    }

    private TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s=charSequence.toString().trim();

            if(s.length() == 0){

                binding.btnContPhoneSignIn.setEnabled(false);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.disabled_round_button);

            }
            else if(s.length() > 0){

                binding.btnContPhoneSignIn.setEnabled(true);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.white_rounded_button);

                //encrypting the password
                encryptedPassword = Commons.encryptedText(s);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        Commons.signIn(this, encryptedPassword, isSuccessful -> {

            if(isSuccessful) {

                //add FCM token
                Commons.addFCMToken();

                //navigate to next activity
                startActivity(new Intent(EnterPasswordSignInActivity.this, /*JoinCircleFirstScreenActivity*/HomeActivity.class));
                finish();
            }
        });

    }
}