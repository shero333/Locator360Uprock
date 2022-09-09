package com.hammad.findmyfamily.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.OneTimeScreens.JoinCircleFirstScreenActivity;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityEnterPasswordSignInBinding;

public class EnterPasswordSignInActivity extends AppCompatActivity {

    private ActivityEnterPasswordSignInBinding binding;

    private String encryptedPassword;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityEnterPasswordSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initializing firebase authentication
        fAuth = FirebaseAuth.getInstance();

        //setting the user's first name saved in preference from list
        binding.txtUsername.append(SharedPreference.getFirstNamePref() + "?");

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

    private void buttonClickListener(){

        fAuth.signInWithEmailAndPassword(SharedPreference.getEmailPref(),encryptedPassword)
                .addOnSuccessListener(authResult -> {

                    Toast.makeText(EnterPasswordSignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EnterPasswordSignInActivity.this, JoinCircleFirstScreenActivity.class));
                    finish();

                })
                .addOnFailureListener(e -> {

                    if(e.getMessage().contains("The password is invalid or the user does not have a password.")) {
                        Toast.makeText(this, "Error! Incorrect Password", Toast.LENGTH_SHORT).show();

                        Log.e("ERROR_ENTER_PASS", "incorrect password error: " + e.getMessage());
                    }
                    else if(e.getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")){
                        Toast.makeText(this, "Error! Incorrect Email", Toast.LENGTH_SHORT).show();

                        Log.e("ERROR_ENTER_PASS", "incorrect email error: " + e.getMessage());
                    }
                    else {
                        Toast.makeText(this, "Error! Try Again.", Toast.LENGTH_SHORT).show();
                        e.getMessage();

                        Log.e("ERROR_ENTER_PASS", "error: " + e.getMessage());
                    }

                });

    }
}