package com.hammad.locator360.SignIn;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.SignUp.NameSignUpActivity;
import com.hammad.locator360.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    ActivityResetPasswordBinding binding;

    private String countryCode;

    //variables for for verifying whether entered number is valid or not
    private PhoneNumberUtil.PhoneNumberType isMobile = null;
    private boolean isPhoneNoValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //picks the default and new selected country code
        pickCountryCode();

        //phone number text watcher
        binding.edtPhoneResetPass.addTextChangedListener(numberTextWatcher);

        binding.btnNextResetPassword.setOnClickListener(v -> buttonClickListener());


    }

    private void pickCountryCode() {

        //selects the default country code
        countryCode = binding.countryCodePickerForgetPass.getSelectedCountryCodeWithPlus();

        //country code picker
        binding.countryCodePickerForgetPass.setOnCountryChangeListener(() -> {
            countryCode = binding.countryCodePickerForgetPass.getSelectedCountryCode();
        });
    }

    private TextWatcher numberTextWatcher = new TextWatcher() {
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

            Log.i("HELLO_123", "international format: "+numberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e("ERROR_PHONE_SIGN_UP", "NumberParseException: " + e.getMessage());
        }
    }

    private void buttonClickListener(){

        String tempNumber = binding.edtPhoneResetPass.getText().toString().trim();
        String phoneNo = "";

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

        //navigates to the next activity
        startActivity(new Intent(this, NameSignUpActivity.class));

    }
}