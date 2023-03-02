package com.care360.findmyfamilyandfriends.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.databinding.ActivityNameSignUpBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class NameSignUpActivity extends AppCompatActivity {

    private ActivityNameSignUpBinding binding;
    private boolean isFirstNameEntered,isLastNameEntered;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityNameSignUpBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //TextWatchers
        binding.edtFirstNameSignUp.addTextChangedListener(firstNameTextWatcher);

        binding.edtLastNameSignUp.addTextChangedListener(lastNameTextWatcher);

        binding.btnContName.setOnClickListener(v -> buttonClickListener());

    }

    private final TextWatcher firstNameTextWatcher = new TextWatcher() {
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

    private final TextWatcher lastNameTextWatcher =new TextWatcher() {
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

    private void setAd() {

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {

                        Log.d("AdError", adError.toString());
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("AdError", "Ad was loaded.");
                        mInterstitialAd = interstitialAd;
                    }
                });
    }

}