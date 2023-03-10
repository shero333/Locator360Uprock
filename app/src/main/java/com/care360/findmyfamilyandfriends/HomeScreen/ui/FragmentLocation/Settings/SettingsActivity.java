package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.Account.AccountDashboardActivity;
import com.care360.findmyfamilyandfriends.databinding.ActivitySettingsBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.CircleManagement.CircleManagementActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTING_ACT";
    
    ActivitySettingsBinding binding;

    private InterstitialAd mInterstitialAd = null;
    private AdRequest adRequest;
    private TemplateView template;
    private AdLoader adloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        template = binding.adTemplate;

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        adloader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {

                    template.setStyles(new NativeTemplateStyle.Builder().build());
                    template.setNativeAd(nativeAd);

                })
                .build();

        adloader.loadAd(new AdRequest.Builder().build());

//        binding.bannerAd.loadAd(adRequest);

        setAd();

        // personal data
        binding.consPersonalData.setOnClickListener(v -> startActivity(new Intent(this, AccountDashboardActivity.class)));

        // circle management
        binding.consCircleManagement.setOnClickListener(v -> startActivity(new Intent(this, CircleManagementActivity.class)));

        // about
        binding.consAbout.setOnClickListener(v -> viewPrivacyPolicy());

        // sign out
        binding.consLogout.setOnClickListener(v -> Commons.signOut(this));
    }

    private void getCurrentUserInfo() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()))
                .get()
                .addOnSuccessListener(doc -> {

                    //setting the data to views
                    setDataToViews(doc.getString(Constants.FIRST_NAME),doc.getString(Constants.LAST_NAME),doc.getString(Constants.PHONE_NO),doc.getString(Constants.IMAGE_PATH));

                })
                .addOnFailureListener(e -> Log.i(TAG, "getting user info error:" + e.getMessage()));
    }

    private void setDataToViews(String firstName, String lastName, String phoneNo, String imagePath) {

        if(imagePath != null)
        {
            // profile image (if any)
            if(imagePath.equals(Constants.NULL)) {
                binding.textUserNameLetters.setVisibility(View.VISIBLE);
                binding.textUserNameLetters.setText(String.valueOf(firstName.charAt(0)));
            }
            else {
                binding.textUserNameLetters.setVisibility(View.GONE);

                //loading the image
                Glide.with(this)
                        .load(imagePath)
                        .into(binding.profileImage);
            }
        }

        if(firstName != null && lastName != null) {
            // user name
            binding.textFullName.setText(firstName.concat(" ").concat(lastName));
        }
        // user name
        //binding.textFullName.setText(firstName.concat(" ").concat(lastName));

        if(phoneNo != null) {
            binding.textPhoneNo.setText(phoneNo);
        }
        // phone number
        //binding.textPhoneNo.setText(phoneNo);

    }

    private void viewPrivacyPolicy() {
        Uri uri = Uri.parse("https://risibleapps.blogspot.com/2022/02/privacy-policy-at-risibleapps-we.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //exit animation activity
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get the current user info
        getCurrentUserInfo();
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