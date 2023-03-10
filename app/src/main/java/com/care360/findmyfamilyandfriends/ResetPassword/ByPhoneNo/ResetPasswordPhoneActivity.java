package com.care360.findmyfamilyandfriends.ResetPassword.ByPhoneNo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityResetPasswordPhoneBinding;

import java.util.ArrayList;
import java.util.List;

public class ResetPasswordPhoneActivity extends AppCompatActivity {

    private static final String TAG = "ACT_RESET_PASS";

    ActivityResetPasswordPhoneBinding binding;

    private String countryCode;

    //variables for for verifying whether entered number is valid or not
    private PhoneNumberUtil.PhoneNumberType isMobile = null;

    private boolean isPhoneNoValid = false;

    //list of registered user
    private List<String> registeredPhoneNoList = new ArrayList<>();

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private TemplateView template;
    private AdLoader adloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityResetPasswordPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        template = binding.adTemplate;

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

        //picks the default and new selected country code
        pickCountryCode();

        //phone number text watcher
        binding.edtPhoneResetPass.addTextChangedListener(numberTextWatcher);

        binding.btnNextResetPassword.setOnClickListener(v -> {

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

    }

    @Override
    protected void onResume() {
        super.onResume();

        //get the list of registered phone no
        getPhoneNoList();
    }

    private void getPhoneNoList() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        registeredPhoneNoList.add(doc.getString(Constants.PHONE_NO));
                    }
                })
                .addOnFailureListener(e -> Log.i(TAG, "getPhoneNoList() error: "+e.getMessage()));

    }

    private void pickCountryCode() {

        //selects the default country code
        countryCode = binding.countryCodePickerForgetPass.getSelectedCountryCodeWithPlus();

        //country code picker
        binding.countryCodePickerForgetPass.setOnCountryChangeListener(() -> countryCode = binding.countryCodePickerForgetPass.getSelectedCountryCode());
    }

    private final TextWatcher numberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s=charSequence.toString().trim();

            //verifying whether entered number is valid and is a mobile phone number or not
            validatePhoneNumber(s,countryCode);

            //if number is valid, then enables the continue. Else continue button is disabled.
            if(isPhoneNoValid && (isMobile.equals(PhoneNumberUtil.PhoneNumberType.MOBILE) || isMobile.equals(PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE))){

                //enabling the continue button
                binding.btnNextResetPassword.setEnabled(true);
                binding.btnNextResetPassword.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orange)));
                binding.btnNextResetPassword.setTextColor(Color.WHITE);
            }
            else {

                //enabling the continue button
                binding.btnNextResetPassword.setEnabled(false);
                binding.btnNextResetPassword.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.grey)));
                binding.btnNextResetPassword.setTextColor(getColor(R.color.orange));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void validatePhoneNumber(String number, String code) {

        PhoneNumberUtil numberUtil=PhoneNumberUtil.getInstance();

        String isoCode=numberUtil.getRegionCodeForCountryCode(Integer.valueOf(code));

        try {
            Phonenumber.PhoneNumber phoneNumber=numberUtil.parse(number,isoCode);

            isPhoneNoValid = numberUtil.isValidNumber(phoneNumber);
            isMobile = numberUtil.getNumberType(phoneNumber);

            Log.i(TAG, "international format: "+numberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e(TAG, "NumberParseException: " + e.getMessage());
        }
    }

    private void buttonClickListener() {

        boolean isPhoneNoRegistered = false;

        String tempNumber = binding.edtPhoneResetPass.getText().toString().trim();
        String phoneNo;

        if(tempNumber.startsWith("0"))
        {
            StringBuilder sb=new StringBuilder(tempNumber);
            sb.deleteCharAt(0);

            //concatenating entered number with country code
            phoneNo = countryCode;
            phoneNo = phoneNo.concat(sb.toString());
        }
        else {
            //concatenating entered number with country code
            phoneNo = countryCode;
            phoneNo = phoneNo.concat(tempNumber);
        }

        for (String phoneNumber : registeredPhoneNoList) {
            if(phoneNumber.equals(phoneNo)) {
                isPhoneNoRegistered = true;
                break;
            }
        }

        // if phone no registered, then navigate to next activity. Else display Error Toast
        if(isPhoneNoRegistered) {

            //save number to preference
            SharedPreference.setPhoneNoPref(phoneNo);

            //navigates to the OTP activity
            Intent intent = new Intent(this, OTPActivity.class);
            intent.putExtra(Constants.OTP_ACT_KEY,false);
            startActivity(intent);
            finish();
        }
        else if(!isPhoneNoRegistered) {
            Log.i(TAG, "phone number not registered");
            Toast.makeText(this, "Error! No registered phone number found. ", Toast.LENGTH_LONG).show();
        }

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