package com.hammad.findmyfamily.ResetPassword.ByPhoneNo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityResetPasswordPhoneBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing view binding
        binding = ActivityResetPasswordPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //picks the default and new selected country code
        pickCountryCode();

        //phone number text watcher
        binding.edtPhoneResetPass.addTextChangedListener(numberTextWatcher);

        binding.btnNextResetPassword.setOnClickListener(v -> buttonClickListener());

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

            Log.i("HELLO_123", "international format: "+numberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e("ERROR_PHONE_SIGN_UP", "NumberParseException: " + e.getMessage());
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
        }
        else if(!isPhoneNoRegistered) {
            Log.i(TAG, "phone number not registered");
            Toast.makeText(this, "Error! No registered phone number found. ", Toast.LENGTH_LONG).show();
        }

    }

}