package com.hammad.locator360.SignUp;

import static com.hammad.locator360.Util.Constants.EMAIL;
import static com.hammad.locator360.Util.Constants.FIRST_NAME;
import static com.hammad.locator360.Util.Constants.LAST_NAME;
import static com.hammad.locator360.Util.Constants.PASSWORD;
import static com.hammad.locator360.Util.Constants.PHONE_NO;
import static com.hammad.locator360.Util.Constants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.locator360.JoinCircleFirstScreenActivity;
import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.databinding.ActivityCreatePasswordBinding;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class CreatePasswordActivity extends AppCompatActivity {

    private ActivityCreatePasswordBinding binding;

    private String encryptedPassword;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreatePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initializing Firebase Auth & FireStore
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

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
                encryptedPassword = encryptedText(s);

            }
            else if(s.length() < 8){

                binding.btnContPasswordSignUp.setEnabled(false);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.disabled_round_button);
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

        //saving password to preference
        SharedPreference.setPasswordPref(encryptedPassword);

        //signing up
        signUp();

    }

    private void signUp(){

        fAuth.createUserWithEmailAndPassword(SharedPreference.getEmailPref(),SharedPreference.getPasswordPref())
                .addOnSuccessListener(authResult -> {

                    FirebaseUser firebaseUser = fAuth.getCurrentUser();

                    DocumentReference dr = fStore.collection(USERS)
                            .document(firebaseUser.getEmail());

                    Map<String, Object> userInfo= new HashMap<>();

                    userInfo.put(FIRST_NAME,SharedPreference.getFirstNamePref());
                    userInfo.put(LAST_NAME,SharedPreference.getLastNamePref());
                    userInfo.put(PHONE_NO,SharedPreference.getPhoneNoPref());
                    userInfo.put(EMAIL,SharedPreference.getEmailPref());
                    userInfo.put(PASSWORD,SharedPreference.getPasswordPref());

                    dr.set(userInfo);

                    Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, JoinCircleFirstScreenActivity.class));
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error! Failed to Sign Up.", Toast.LENGTH_SHORT).show();
                    Log.e("ERROR_CREATE_PSS_ACT", "signUp failed "+e.getMessage());

                });

    }


}