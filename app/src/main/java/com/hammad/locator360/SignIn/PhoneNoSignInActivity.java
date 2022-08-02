package com.hammad.locator360.SignIn;

import static com.hammad.locator360.Util.Constants.EMAIL;
import static com.hammad.locator360.Util.Constants.FIRST_NAME;
import static com.hammad.locator360.Util.Constants.PASSWORD;
import static com.hammad.locator360.Util.Constants.PHONE_NO;
import static com.hammad.locator360.Util.Constants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hammad.locator360.R;
import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.SignIn.model.User;
import com.hammad.locator360.SignUp.PhoneNoSignUpActivity;
import com.hammad.locator360.databinding.ActivityPhoneNoSignInBinding;

import java.util.ArrayList;
import java.util.List;

public class PhoneNoSignInActivity extends AppCompatActivity {

    private ActivityPhoneNoSignInBinding binding;

    private String countryCode;

    //variables for for verifying whether entered number is valid or not
    private PhoneNumberUtil.PhoneNumberType isMobile = null;
    private boolean isPhoneNoValid = false;

    private FirebaseFirestore fStore;

    //list of registered user
    private List<User> registeredUserList = new ArrayList<>();

    //this integer saves the index of registered user (if any)
    private int userInfoIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityPhoneNoSignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initialize Firestore
        fStore = FirebaseFirestore.getInstance();

        //getting the default and selected country code
        pickCountryCode();

        //TextWatcher
        binding.edtPhoneSignIn.addTextChangedListener(numberTextWatcher);

        //button continue click listener
        binding.btnContPhoneSignIn.setOnClickListener(v -> buttonCLickListener());

        //Textview login with email click listener
        binding.txtSignInWithEmail.setOnClickListener(v -> {
            startActivity(new Intent(this,EmailSignInActivity.class));
            finish();
        });

    }

    private void pickCountryCode() {

        //selects the default country code
        countryCode = binding.countryCodePickerSignIn.getSelectedCountryCodeWithPlus();

        //country code picker
        binding.countryCodePickerSignIn.setOnCountryChangeListener(() -> {
            countryCode = binding.countryCodePickerSignIn.getSelectedCountryCode();
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

                binding.btnContPhoneSignIn.setEnabled(true);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else {

                binding.btnContPhoneSignIn.setEnabled(false);
                binding.btnContPhoneSignIn.setBackgroundResource(R.drawable.disabled_round_button);
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

        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.e("ERROR_PHONE_SIGN_IN", "NumberParseException: " + e.getMessage());
        }
    }

    private void buttonCLickListener(){
        String tempNumber = binding.edtPhoneSignIn.getText().toString().trim();
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

        //checking if entered number exist in registered user. If yes, login; else navigate to sign up
        for(int i =0; i < registeredUserList.size(); i++) {
            if(registeredUserList.get(i).getPhoneNo().equals(phoneNo)){

                //saves the user info index
                userInfoIndex = i;
                break;
            }
        }

        //if value <0, it means no registered user. Navigates to Sign up
        if(userInfoIndex < 0){
            startActivity(new Intent(this, PhoneNoSignUpActivity.class));
        }
        else if(userInfoIndex >= 0){

            //saving the values of Email, FirstName, and Password to preference
            SharedPreference.setEmailPref(registeredUserList.get(userInfoIndex).getEmail());
            SharedPreference.setFirstNamePref(registeredUserList.get(userInfoIndex).getFirstName());

            // Navigating to next activity
            startActivity(new Intent(this,EnterPasswordSignInActivity.class));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //getting the registered user list
        getRegisteredUserList();
    }

    private void getRegisteredUserList(){

        fStore.collection(USERS).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                  for(DocumentSnapshot doc: queryDocumentSnapshots) {

                      registeredUserList.add(new User(doc.getString(FIRST_NAME), doc.getString(PHONE_NO), doc.getString(EMAIL)));
                  }
                });
    }


}