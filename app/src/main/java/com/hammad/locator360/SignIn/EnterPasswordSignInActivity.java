package com.hammad.locator360.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hammad.locator360.JoinCircleFirstScreenActivity;
import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.databinding.ActivityEnterPasswordSignInBinding;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        binding.txtForgetPassword.setOnClickListener(v -> startActivity(new Intent(this,ForgetPasswordActivity.class)));

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
                encryptedPassword = encryptedText(s);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    /*
        These two function for encrypting SHA-256 hash code. If remain same for a particular group of text.
        We will save this encrypted text in firebase, and when user enters password, we will convert it into Hex and then compare with the firebase password.
    */
    private String encryptedText(String text) {
        MessageDigest digest;
        String encryptedText= "";
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));

            encryptedText = bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            Log.e("ERROR_CREATE_PSS_ACT", "NoSuchAlgorithmException " + e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.e("ERROR_CREATE_PSS_ACT", "UnsupportedEncodingException " + e.getMessage());
            e.printStackTrace();

        }

        return encryptedText;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

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