package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.ContactsManually;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.EmergencyContactEntity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencyRoomDB.RoomDBHelper;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityAddContactManuallyBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Random;

public class AddContactManuallyActivity extends AppCompatActivity {

    private static final String TAG = "ADD_CONTACT_MANUALLY";
    ActivityAddContactManuallyBinding binding;

    private String countryCode, firstName = "", lastName = "";

    //variables for verifying whether entered number is valid or not
    private PhoneNumberUtil.PhoneNumberType isMobile = null;
    private boolean isPhoneNoValid = false;

    //variables for generating random contact id
    int range = 10000;
    int min = 1000;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddContactManuallyBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //toolbar back pressed
        binding.toolbarAddContactManually.setNavigationOnClickListener(v -> onBackPressed());

        //country code picker
        pickCountryCode();

        binding.editTextPhoneNo.addTextChangedListener(phoneNoTextWatcher);

        binding.editTxtFirstName.addTextChangedListener(firstNameTextWatcher);

        binding.editTxtLastName.addTextChangedListener(lastNameTextWatcher);

        binding.btnSave.setOnClickListener(v -> saveEmergencyContact());
    }

    private void pickCountryCode() {

        //selects the default country code
        countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();

        //country code picker
        binding.countryCodePicker.setOnCountryChangeListener(() -> countryCode = binding.countryCodePicker.getSelectedCountryCode());
    }

    private void validatePhoneNumber(String number, String code) {

        PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();

        String isoCode = numberUtil.getRegionCodeForCountryCode(Integer.parseInt(code));

        try {
            Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(number, isoCode);

            isPhoneNoValid = numberUtil.isValidNumber(phoneNumber);
            isMobile = numberUtil.getNumberType(phoneNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e(TAG, "NumberParseException: " + e.getMessage());
        }
    }

    private final TextWatcher phoneNoTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String phoneNo = charSequence.toString();

            // validating the phone No
            validatePhoneNumber(phoneNo, countryCode);

            // button save enabled/disabled status
            buttonSaveStatus();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher firstNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            firstName = charSequence.toString().trim();

            // button save enabled/disabled status
            buttonSaveStatus();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private final TextWatcher lastNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            lastName = charSequence.toString().trim();

            // button save enabled/disabled status
            buttonSaveStatus();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void buttonSaveStatus() {

        if (isPhoneNoValid && (isMobile.equals(PhoneNumberUtil.PhoneNumberType.MOBILE) || isMobile.equals(PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE))
                && firstName.length() > 0 && lastName.length() > 0) {

            //enabling button save
            binding.btnSave.setEnabled(true);
            binding.btnSave.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            binding.btnSave.setTextColor(getColor(R.color.white));

        } else {
            //disabling button save
            binding.btnSave.setEnabled(false);
            binding.btnSave.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.grey_bottom)));
            binding.btnSave.setTextColor(getColor(R.color.orange));
        }
    }

    private void saveEmergencyContact() {

        String tempNumber = binding.editTextPhoneNo.getText().toString().trim();
        String phoneNo;

        if (tempNumber.startsWith("0")) {
            StringBuilder sb = new StringBuilder(tempNumber);
            sb.deleteCharAt(0);

            //concatenating entered number with country code
            phoneNo = countryCode;
            phoneNo = phoneNo.concat(sb.toString());
        } else {
            //concatenating entered number with country code
            phoneNo = countryCode;
            phoneNo = phoneNo.concat(tempNumber);
        }

        // for manual contact saving, we will generate a random contact id
        Random random = new Random();
        int randomContactId = random.nextInt(range) + min;

        String contactName = firstName.concat(" ").concat(lastName);

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //saving contact in DB
        RoomDBHelper.getInstance(this)
                .emergencyContactDao()
                .addContact(new EmergencyContactEntity(currentUserEmail, String.valueOf(randomContactId), contactName, phoneNo));

        //updating shared preference value
        SharedPreference.setEmergencyContactsStatus(true);

        // is full name shared pref is null, gets the current user full name from firebase
        if (SharedPreference.getFullName().equals(Constants.NULL)) {
            Commons.currentUserFullName();
        }

        //navigates to next activity
        Intent intent = new Intent(this, EmergencyContactDashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Constants.PHONE_NO, phoneNo);
        startActivity(intent);
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