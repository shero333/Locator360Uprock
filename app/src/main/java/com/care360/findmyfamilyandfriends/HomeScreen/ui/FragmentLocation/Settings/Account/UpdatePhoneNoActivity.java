package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Settings.Account;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityUpdatePhoneNoBinding;

public class UpdatePhoneNoActivity extends AppCompatActivity {

    ActivityUpdatePhoneNoBinding binding;

    String phoneNumber = "";
    private TemplateView template;
    private AdLoader adloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityUpdatePhoneNoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this);

        template = binding.adTemplate;


        adloader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(nativeAd -> {

                    template.setStyles(new NativeTemplateStyle.Builder().build());
                    template.setNativeAd(nativeAd);

                })
                .build();

        adloader.loadAd(new AdRequest.Builder().build());


        //get phone number from intent
        Intent intent = getIntent();

        if(intent != null) {
            phoneNumber = intent.getStringExtra(Constants.PHONE_NO);
        }

        // toolbar back pressed
        binding.toolbarUpdatePhoneNo.setNavigationOnClickListener(v -> onBackPressed());

        //find a way to match the first 3-4 letters of phone number with international format, selects country from that and display rest of phone number in edit

        PhoneNumberUtil numberUtil=PhoneNumberUtil.getInstance();

        //binding.ccpUpdatePhoneNo.setCountryForPhoneCode();
        //Toast.makeText(this, binding.ccpUpdatePhoneNo.getSelectedCountryCodeWithPlus(), Toast.LENGTH_SHORT).show();
    }
}