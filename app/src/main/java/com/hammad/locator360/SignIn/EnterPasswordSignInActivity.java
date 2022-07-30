package com.hammad.locator360.SignIn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityEnterPasswordSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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

            if(s.length() >= 8){

                binding.btnContPhoneSignIn.setEnabled(true);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.white_rounded_button);

                //encrypting the password
                encryptedPassword = encryptedText(s);

            }
            else if(s.length() < 8){

                binding.btnContPhoneSignIn.setEnabled(false);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.disabled_round_button);
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

        if(encryptedPassword.equals(SharedPreference.getPasswordPref())){
            startActivity(new Intent(this, JoinCircleFirstScreenActivity.class));
            finish();
        }
        else{
            Toast.makeText(this, "Error! Incorrect Password.", Toast.LENGTH_SHORT).show();
        }

    }
}