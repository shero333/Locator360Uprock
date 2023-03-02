package com.care360.findmyfamilyandfriends.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.ResetPassword.ByEmail.ResetPasswordEmailActivity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.ActivityEnterPasswordSignInBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class EnterPasswordSignInActivity extends AppCompatActivity {

    private ActivityEnterPasswordSignInBinding binding;

    private String password;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityEnterPasswordSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //setting the user's first name saved in preference from list
        //binding.txtUsername.append(" " + SharedPreference.getFirstNamePref() + "?");

        //TextWatcher
        binding.edtPasswordSignIn.addTextChangedListener(passwordTextWatcher);

        //button sign in click listener
        binding.btnContPhoneSignIn.setOnClickListener(v -> {

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

        //forget password
        binding.txtForgetPassword.setOnClickListener(v -> {

            mInterstitialAd.show(this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();
                    startActivity(new Intent(EnterPasswordSignInActivity.this, ResetPasswordEmailActivity.class));

                }
            });
        });

        //if the first name retrieved against phone number is incorrect, then moves onto the account verification activity
        //binding.txtUsername.setOnClickListener(v -> Toast.makeText(this, "Verifying Account Details", Toast.LENGTH_SHORT).show());
    }

    private final TextWatcher passwordTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            password =charSequence.toString().trim();

            if(password.length() == 0) {

                binding.btnContPhoneSignIn.setEnabled(false);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.disabled_round_button);

            }
            else if(password.length() > 0) {

                binding.btnContPhoneSignIn.setEnabled(true);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.white_rounded_button);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        Commons.signIn(this, password, isSuccessful -> {

            if(isSuccessful) {

                //add FCM token
                Commons.addFCMToken();

                //navigate to next activity
                startActivity(new Intent(EnterPasswordSignInActivity.this, HomeActivity.class));
                finish();
            }
        });

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