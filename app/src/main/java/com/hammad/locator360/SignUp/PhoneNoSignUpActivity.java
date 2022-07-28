package com.hammad.locator360.SignUp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hammad.locator360.databinding.ActivityPhoneNoSignUpBinding;

public class PhoneNoSignUpActivity extends AppCompatActivity {

    private ActivityPhoneNoSignUpBinding binding;

    private String privacyPolicyText="By signing up you accept our terms of service\n and privacy policy";

    private String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityPhoneNoSignUpBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        //country code picker
        binding.countryCodePicker.setOnCountryChangeListener(() -> {
            countryCode = binding.countryCodePicker.getSelectedCountryCode();
        });

        countryCode = binding.countryCodePicker.getSelectedCountryCode();

        //privacy policy and terms of services click listeners
        setHyperLink();

        binding.btnContPhoneSignUp.setOnClickListener(view1 -> {

            validatePhoneNumber(binding.edtPhoneSignUp.getText().toString(),countryCode);

        });

    }

    private void setHyperLink(){
        SpannableString spannableString=new SpannableString(privacyPolicyText);

        //color span for hyperlinks
        ForegroundColorSpan fcsTermsOfServices=new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan fcsPrivacyPolicy=new ForegroundColorSpan(Color.BLUE);

        ClickableSpan clickableSpanTermsOfService=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                viewTermsOfService();
            }
        };

        ClickableSpan clickableSpanPrivacyPolicy=new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                viewPrivacyPolicy();
            }
        };

        //for making it clickable
        spannableString.setSpan(clickableSpanTermsOfService,29,45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpanPrivacyPolicy,51,65,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //for changing the text color to blue
        spannableString.setSpan(fcsTermsOfServices,29,45,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(fcsPrivacyPolicy,51,65,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtPrivacyPolicy.setText(spannableString);
        binding.txtPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void viewPrivacyPolicy() {
        Uri uri = Uri.parse("https://risibleapps.blogspot.com/2022/02/privacy-policy-at-risibleapps-we.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void viewTermsOfService() {
        Uri uri = Uri.parse("https://www.google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void validatePhoneNumber(String number, String code) {

        PhoneNumberUtil numberUtil=PhoneNumberUtil.getInstance();

        String isoCode=numberUtil.getRegionCodeForCountryCode(Integer.valueOf(code));
        Log.i("HELLO_123", "iso code: "+isoCode);

        PhoneNumberUtil.PhoneNumberType isMobile = null;
        boolean isValid = false;

        try {
            Phonenumber.PhoneNumber phoneNumber=numberUtil.parse(number,isoCode);

            isValid= numberUtil.isValidNumber(phoneNumber);
            isMobile = numberUtil.getNumberType(phoneNumber);

            Log.i("HELLO_123", "origional format: "+numberUtil.formatInOriginalFormat(phoneNumber,isoCode));

        } catch (NumberParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Number Parse Exception", Toast.LENGTH_SHORT).show();
            Log.i("HELLO_123", "number parse: "+e.getMessage());
        }

        Log.i("HELLO_123", "isValid: "+isValid);
        Log.i("HELLO_123", "num type: "+isMobile);

    }
}