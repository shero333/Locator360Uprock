package com.care360.findmyfamilyandfriends.ResetPassword.ByPhoneNo;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.Account.AccountDashboardActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.ActivityCreateNewPasswordBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutRecentAuthenticationBinding;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ACT_NEW_PASS";

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    private ActivityCreateNewPasswordBinding binding;

    //string for saving the entered passwords
    private String strPassword = "", strConfirmPassword = "";

    // dialog
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreateNewPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //adding text watchers
        binding.edtPassword.addTextChangedListener(passwordTextWatcher);

        binding.edtConfirmPassword.addTextChangedListener(confirmPasswordTextWatcher);

        // button reset password click listener
        binding.btnResetPassword.setOnClickListener(v -> {

            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();
                    updatePasswordClickListener();

                }
            });
        });

        // re-authenticate dialog (if scenario)
        reAuthenticateDialog();

    }

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            strPassword = charSequence.toString().trim();

            if (strPassword.length() >= 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(" ");

                if (strPassword.equals(strConfirmPassword)) {
                    passwordsMatchScenario();
                } else if (!strPassword.equals(strConfirmPassword)) {

                    if (binding.edtConfirmPassword.length() == 0) {

                        binding.layoutConfirmPassword.setHelperText(" ");

                        //setting the reset button state to disable
                        setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
                    } else {
                        passwordsMismatchScenario();
                    }

                }

            }
            else if (strPassword.length() < 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(getString(R.string.minimum_8_character_password));

                //setting the helper text of confirm password to empty
                binding.layoutConfirmPassword.setHelperText(" ");

                //setting the reset button status to disabled
                setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    };

    private final TextWatcher confirmPasswordTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            strConfirmPassword = charSequence.toString().trim();

            if (strConfirmPassword.equals(strPassword)) {

                if (binding.edtConfirmPassword.length() == 0 && binding.edtPassword.length() == 0) {

                    binding.layoutConfirmPassword.setHelperText(" ");

                    //setting the reset button state to disable
                    setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));

                } else {
                    passwordsMatchScenario();
                }

            } else if (!strConfirmPassword.equals(strPassword)) {

                passwordsMismatchScenario();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void setResetPasswordButtonStatus(boolean status, int layout, int color) {
        binding.btnResetPassword.setEnabled(status);
        binding.btnResetPassword.setBackgroundResource(layout);
        binding.btnResetPassword.setTextColor(color);
    }

    private void passwordsMatchScenario() {

        //setting the matched passwords helper text
        binding.layoutConfirmPassword.setHelperText(getString(R.string.passwords_matched));
        binding.layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.holo_green_dark)));

        //setting the reset button state to enabled
        setResetPasswordButtonStatus(true, R.drawable.orange_rounded_button, getColor(R.color.white));
    }

    private void passwordsMismatchScenario() {

        //setting the mis-match passwords helper text
        binding.layoutConfirmPassword.setHelperText(getString(R.string.passwords_does_not_match));
        binding.layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.holo_red_dark)));

        //setting the reset button state to disable
        setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
    }

    private void updatePasswordClickListener() {

        //sets progress bar visibility to VISIBLE
        binding.progressBar.setVisibility(View.VISIBLE);

        //updates the password with associated account
        FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .updatePassword(strConfirmPassword)
                    .addOnSuccessListener(unused -> {

                        //sets progress bar visibility to GONE
                        binding.progressBar.setVisibility(View.GONE);

                        Toast.makeText(this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, AccountDashboardActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {

                        //sets progress bar visibility to GONE
                        binding.progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "failed to update password: " + e.getMessage());

                        if(e.getMessage().equals(getString(R.string.updated_password_by_phone_no))) {
                            dialog.show();
                        }
                        else {
                            Toast.makeText(this, "Error! Failed to Update Password.", Toast.LENGTH_LONG).show();
                        }
                    });
    }

    private void reAuthenticateDialog() {

        dialog = new Dialog(this);

        LayoutRecentAuthenticationBinding binding = LayoutRecentAuthenticationBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());

        // button log out click listener
        binding.btnLogOut.setOnClickListener(v -> Commons.signOut(this));

        // text view cancel
        binding.textCancel.setOnClickListener(v -> dialog.dismiss());

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        dialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.create();
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