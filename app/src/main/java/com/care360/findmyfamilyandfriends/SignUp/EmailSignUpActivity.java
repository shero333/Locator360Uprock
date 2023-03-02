package com.care360.findmyfamilyandfriends.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityEmailSignUpBinding;

import java.util.ArrayList;
import java.util.List;

public class EmailSignUpActivity extends AppCompatActivity {

    private static final String TAG = "EMAIL_SIGN_UP";

    private ActivityEmailSignUpBinding binding;

    List<String> registeredEmailList = new ArrayList<>();

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityEmailSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //get data
        getRegisteredUsersEmail();

        //TextWatcher
        binding.edtEmailSignUp.addTextChangedListener(emailTextWatcher);

        //button click listener
        binding.btnContEmailSignUp.setOnClickListener(v -> buttonClickListener());

    }

    private final TextWatcher emailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            if (Commons.validateEmailAddress(s)) {

                binding.btnContEmailSignUp.setEnabled(true);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.white_rounded_button);
            } else if (!Commons.validateEmailAddress(s)) {
                binding.btnContEmailSignUp.setEnabled(false);
                binding.btnContEmailSignUp.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void buttonClickListener() {

        boolean doesEmailAlreadyExist = false;

        for (int i = 0; i < registeredEmailList.size(); i++) {

            if (registeredEmailList.get(i).equals(binding.edtEmailSignUp.getText().toString())) {
                doesEmailAlreadyExist = true;
                break;
            }
        }

        if(doesEmailAlreadyExist) {
            Toast.makeText(this, getString(R.string.email_already_in_use), Toast.LENGTH_LONG).show();
        }
        else {
            SharedPreference.setEmailPref(binding.edtEmailSignUp.getText().toString());

            startActivity(new Intent(this,CreatePasswordActivity.class));
        }

    }

    //function for checking if entered email already exists or not
    private void getRegisteredUsersEmail() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        registeredEmailList.add(doc.getId());
                    }
                })
                .addOnFailureListener(e -> Log.i(TAG, "onFailure: " + e.getMessage()));

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