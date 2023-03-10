package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityContactDetailBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ContactDetailActivity extends AppCompatActivity {

    ActivityContactDetailBinding binding;

    ParcelableContactModel contactModel;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityContactDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //getting the intent data
        Intent intent = getIntent();

        if(intent != null) {
            contactModel = intent.getParcelableExtra(Constants.CONTACT_KEY);
        }

        //setting the values to views
        binding.txtContactLetters.setText(Commons.getContactLetters(contactModel.getParContactName()));
        binding.txtContactName.setText(contactModel.getParContactName());
        binding.txtContactNumber.setText(contactModel.getParContactNumber());

        //resend invite click listener
        binding.btnResentInvite.setOnClickListener(v -> {

            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    setAd();

                    if(SharedPreference.getFullName().equals(Constants.NULL)) {
                        Commons.currentUserFullName();
                    }

                    //sending
                    Commons.sendEmergencyContactInvitation(getApplicationContext(), contactModel.getParContactNumber());
                }
            });

        });

        // button delete
        binding.deleteContact.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Delete Contact")
                    .setMessage("want to delete this emergency contact?")
                    .setPositiveButton("Delete", (dialogInterface, i) -> {

                        //deleting contact
                        RoomDBHelper.getInstance(this).emergencyContactDao()
                                .deleteEmergencyContact(contactModel.getParOwnerEmail(), contactModel.getParContactId(), contactModel.getParContactNumber());

                        finish();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();

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