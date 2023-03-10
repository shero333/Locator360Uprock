package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.LandingActivity.EmergencyContactLandingActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencySOS.EmergencySOSActivity;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.FragmentSafetyBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutSendSmsPermissionDialogBinding;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

public class FragmentSafety extends Fragment {

    private FragmentSafetyBinding binding;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    private AdLoader adloader;
    private TemplateView template;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing binding
        binding = FragmentSafetyBinding.inflate(inflater, container, false);

        MobileAds.initialize(requireContext());

        //banner
        adRequest = new AdRequest.Builder().build();

        setAd();

        template = binding.adTemplate;


        //help alert click listener
        binding.consHelpAlert.setOnClickListener(v -> {

            navigateToEmergencySOSActivity();

//            if (Permissions.hasSmsPermission(requireContext())) {
//                //navigate to emergency SOS activity
//            }
////            else {
//                sendSMSPermission();
////            }

        });

        //add emergency contact click listener
        binding.btnAddEmergencyContact.setOnClickListener(v -> {

            // if any contact is added in DB, then Dashboard activity is called. Else, Contact Landing Activity is called
            if (SharedPreference.getEmergencyContactsStatus()) {
                Intent intent = new Intent(requireActivity(), EmergencyContactDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else if (!SharedPreference.getEmergencyContactsStatus()) {
                Intent intent = new Intent(requireActivity(), EmergencyContactLandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        // saving current user full name in shared pref
        Commons.currentUserFullName();

        return binding.getRoot();
    }

    private void navigateToEmergencySOSActivity() {
        startActivity(new Intent(requireActivity(), EmergencySOSActivity.class));
    }

    private void sendSMSPermission() {

        // shows the info dialog
        Dialog smsDialog = new Dialog(getContext());

        LayoutSendSmsPermissionDialogBinding dialogBinding = LayoutSendSmsPermissionDialogBinding.inflate(getLayoutInflater());
        smsDialog.setContentView(dialogBinding.getRoot());

        //allow permission click listener
        dialogBinding.btnAllowPermission.setOnClickListener(v -> {
            smsResultLauncher.launch(Manifest.permission.SEND_SMS);
            smsDialog.dismiss();
        });

        // deny permission click listener
        dialogBinding.denyPermission.setOnClickListener(v -> smsDialog.dismiss());

        //setting the transparent background
        smsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Objects.requireNonNull(requireActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        smsDialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        smsDialog.show();

    }

    ActivityResultLauncher<String> smsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

        if (result) {
            //permission is allowed, navigate to next activity
            navigateToEmergencySOSActivity();
        } else {
            Toast.makeText(getContext(), "SMS Permission Denied.", Toast.LENGTH_SHORT).show();
        }

    });

    //nullifying binding instance on view destroying
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setAd() {

        InterstitialAd.load(
                requireContext(),
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.bannerAd.loadAd(adRequest);

        adloader = new AdLoader.Builder(requireContext(), "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {

                    template.setStyles(new NativeTemplateStyle.Builder().build());
                    template.setNativeAd(nativeAd);

                })
                .build();

        adloader.loadAd(new AdRequest.Builder().build());
    }
}