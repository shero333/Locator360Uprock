package com.care360.findmyfamilyandfriends.SignIn;

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
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.ActivityEmailSignInBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class EmailSignInActivity extends AppCompatActivity {

    private ActivityEmailSignInBinding binding;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing
        binding = ActivityEmailSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //email TextWatcher
        binding.edtEmailSignIn.addTextChangedListener(emailTextWatcher);

        binding.btnContEmailSignIn.setOnClickListener(v -> {

            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();
                    buttonClickListener();

                }
            });

        });

        //sign in with phone number
        binding.txtSignInWithNumber.setOnClickListener(v -> {

            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();

                    startActivity(new Intent(EmailSignInActivity.this,PhoneNoSignInActivity.class));
                    finish();
                }
            });

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

    private void buttonClickListener() {

        //saving email in preference
        SharedPreference.setEmailPref(binding.edtEmailSignIn.getText().toString());

        //navigating to next activity
        startActivity(new Intent(this, EnterPasswordSignInActivity.class));
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