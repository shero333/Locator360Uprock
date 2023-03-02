package com.care360.findmyfamilyandfriends.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.OneTimeScreens.JoinCircleFirstScreenActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.databinding.ActivityCreatePasswordBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class CreatePasswordActivity extends AppCompatActivity {

    private ActivityCreatePasswordBinding binding;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreatePasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //TextWatcher
        binding.edtPasswordSignUp.addTextChangedListener(passwordTextWatcher);

        //button click listener
        binding.btnContPasswordSignUp.setOnClickListener(v -> buttonClickListener());

    }

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            password =charSequence.toString().trim();

            if(password.length() >= 8) {

                //if password length is correct, then removes the helper text
                binding.layoutPasswordSignUp.setHelperText(" ");

                binding.btnContPasswordSignUp.setEnabled(true);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.white_rounded_button);

            }
            else if(password.length() < 8) {

                //if password length is incorrect, then sets the helper text
                binding.layoutPasswordSignUp.setHelperText(getString(R.string.minimum_8_character_password));

                binding.btnContPasswordSignUp.setEnabled(false);
                binding.btnContPasswordSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        //saving password to preference
        SharedPreference.setPasswordPref(password);

        //navigating to next activity
        startActivity(new Intent(this, JoinCircleFirstScreenActivity.class));
        finish();
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