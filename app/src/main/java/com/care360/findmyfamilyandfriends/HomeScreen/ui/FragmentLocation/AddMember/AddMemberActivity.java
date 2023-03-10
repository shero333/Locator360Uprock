package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.AddMember;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.BuildConfig;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityAddMemberBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AddMemberActivity extends AppCompatActivity {

    ActivityAddMemberBinding binding;

    //boolean for saving the view type clicked from Location Fragment
    boolean isToolbarAddMemberButtonClicked;


    private InterstitialAd mInterstitialAd = null;
    private AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);

        setAd();

        //getting the intent data from Location Fragment
        getIntentData();

        //toolbar back pressed
        binding.toolbarAddMember.setNavigationOnClickListener(v -> navigateToPreviousActivity());

        //circle name
        binding.txtCircleName.setText(SharedPreference.getCircleName());

        //circle code
        setCircleCode();

        //share invite code click listener
        binding.btnShareCode.setOnClickListener(v -> shareCircleCode());
    }

    private void getIntentData() {

        Intent intent = getIntent();

        isToolbarAddMemberButtonClicked = intent.getBooleanExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, false);

    }

    private void navigateToPreviousActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, isToolbarAddMemberButtonClicked);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setCircleCode() {

        String inviteCode = SharedPreference.getCircleInviteCode();

        binding.textViewCircle1.setText(String.valueOf(inviteCode.charAt(0)));
        binding.textViewCircle2.setText(String.valueOf(inviteCode.charAt(1)));
        binding.textViewCircle3.setText(String.valueOf(inviteCode.charAt(2)));
        binding.textViewCircle4.setText(String.valueOf(inviteCode.charAt(3)));
        binding.textViewCircle5.setText(String.valueOf(inviteCode.charAt(4)));
        binding.textViewCircle6.setText(String.valueOf(inviteCode.charAt(5)));
    }

    @Override
    public void onBackPressed() {
        navigateToPreviousActivity();
    }

    private void shareCircleCode() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = "You're invited to join circle on Find My Family.\nCircle Name: "+SharedPreference.getCircleName()+"\nJoin Code: "+SharedPreference.getCircleInviteCode()
                +"\n\nHaven't download this application yet? Download now from:\n\n"+
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Share via"));
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