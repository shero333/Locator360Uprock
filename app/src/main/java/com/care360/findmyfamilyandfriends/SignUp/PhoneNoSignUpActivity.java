package com.kl360.findmyfamilyandfriends.SignUp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.kl360.findmyfamilyandfriends.R;
import com.kl360.findmyfamilyandfriends.ResetPassword.ByPhoneNo.OTPActivity;
import com.kl360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.kl360.findmyfamilyandfriends.Util.Constants;
import com.kl360.findmyfamilyandfriends.databinding.ActivityPhoneNoSignUpBinding;

import java.util.ArrayList;
import java.util.List;

public class PhoneNoSignUpActivity extends AppCompatActivity {

    private static final String TAG = "ERROR_PHONE_SIGN_UP";

    private ActivityPhoneNoSignUpBinding binding;

    private String privacyPolicyText;

    private String countryCode;

    //variables for for verifying whether entered number is valid or not
    private PhoneNumberUtil.PhoneNumberType isMobile = null;
    private boolean isPhoneNoValid = false;

    //already existed phone numbers
    List<String> membersPhoneNumberList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityPhoneNoSignUpBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //get registered users phone number list
        getRegisteredPhoneNoList();

        //getting the privacy policy text from string resource
        privacyPolicyText=getString(R.string.privacy_policy_phone_sign_up);

        //picks the default and new selected country code
        pickCountryCode();

        //phone number text watcher
        binding.edtPhoneSignUp.addTextChangedListener(numberTextWatcher);

        //privacy policy click listener
        setHyperLink();

        binding.btnContPhoneSignUp.setOnClickListener(v -> buttonClickListener());
    }

    private void getRegisteredPhoneNoList() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot doc: queryDocumentSnapshots) {
                        membersPhoneNumberList.add(doc.getString(Constants.PHONE_NO));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting registered phone number list: " + e.getMessage()));

    }

    private void pickCountryCode() {

        //selects the default country code
        countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();

        //country code picker
        binding.countryCodePicker.setOnCountryChangeListener(() -> countryCode = binding.countryCodePicker.getSelectedCountryCode());
    }

    private void setHyperLink() {

        SpannableString spannableString=new SpannableString(privacyPolicyText);

        //color span for hyperlinks
        ForegroundColorSpan fcsPrivacyPolicy=new ForegroundColorSpan(Color.BLUE);

        ClickableSpan clickableSpanPrivacyPolicy=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                viewPrivacyPolicy();
            }
        };

        //for making it clickable
        spannableString.setSpan(clickableSpanPrivacyPolicy,25,39,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //for changing the text color to blue
        spannableString.setSpan(fcsPrivacyPolicy,25,39,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtPrivacyPolicy.setText(spannableString);
        binding.txtPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void viewPrivacyPolicy() {
        Uri uri = Uri.parse("https://risibleapps.blogspot.com/2022/02/privacy-policy-at-risibleapps-we.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
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

                binding.btnContPhoneSignUp.setEnabled(true);
                binding.btnContPhoneSignUp.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else {

                binding.btnContPhoneSignUp.setEnabled(false);
                binding.btnContPhoneSignUp.setBackgroundResource(R.drawable.disabled_round_button);
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

            //for formatting the phone number in international format
            //numberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e(TAG, "NumberParseException: " + e.getMessage());
        }
    }

    private void buttonClickListener() {

        String tempNumber = binding.edtPhoneSignUp.getText().toString().trim();
        String phoneNo;

        if(tempNumber.startsWith("0")) {
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

        boolean doesPhoneNoAlreadyExist = false;

        for (int i = 0; i < membersPhoneNumberList.size(); i++) {

            if (membersPhoneNumberList.get(i).equals(phoneNo)) {
                doesPhoneNoAlreadyExist = true;
                break;
            }
        }

        if(doesPhoneNoAlreadyExist) {
            Toast.makeText(this, R.string.phone_no_already_registered, Toast.LENGTH_LONG).show();
        }
        else {

            //saving the entered phone number in preference
            SharedPreference.setPhoneNoPref(phoneNo);

            //navigates to the OTP activity
            Intent intent = new Intent(this, OTPActivity.class);
            intent.putExtra(Constants.OTP_ACT_KEY,true);
            startActivity(intent);
        }
    }
}