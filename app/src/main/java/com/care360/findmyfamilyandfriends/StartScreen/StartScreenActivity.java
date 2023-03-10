package com.care360.findmyfamilyandfriends.StartScreen;

import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Chat.ChatDetailActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.Chat.Model.UserInfo;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.EmergencySOS.EmergencyLocationActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.SignIn.PhoneNoSignInActivity;
import com.care360.findmyfamilyandfriends.SignUp.PhoneNoSignUpActivity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityStartScreenBinding;
import com.care360.findmyfamilyandfriends.update_manager.UpdateManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StartScreenActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;

    ActivityStartScreenBinding binding;

    private InterstitialAd mInterstitialAd = null;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityStartScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);

        setAd();

        //initializing Firebase firestore
        fStore = FirebaseFirestore.getInstance();

        // Button sign up click listener
        binding.btnSignUp.setOnClickListener(v -> startActivity(new Intent(StartScreenActivity.this, PhoneNoSignUpActivity.class)));

        // Textview sign in click listener
        binding.txtSignIn.setOnClickListener(v -> startActivity(new Intent(StartScreenActivity.this, PhoneNoSignInActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
            This function is used to check if the app flow is normal,or app is opened from notification.
            If app is triggered from notification, then emergency location activity is opened, else normal flow.
        */
        conditionCheck();
    }

    private void conditionCheck() {

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            if (intent.getExtras().getString(Constants.FCM_LAT) != null && intent.getExtras().getString(Constants.FCM_LNG) != null) {
                // setting the progress bar visibility to 'GONE'
                binding.progressBar.setVisibility(View.GONE);

                Intent sosEmergIntent = new Intent(this, EmergencyLocationActivity.class);

                sosEmergIntent.putExtra(Constants.FCM_LAT, intent.getExtras().getString(Constants.FCM_LAT));
                sosEmergIntent.putExtra(Constants.FCM_LNG, intent.getExtras().getString(Constants.FCM_LNG));
                sosEmergIntent.putExtra(Constants.IS_APP_IN_FOREGROUND, false);
                startActivity(sosEmergIntent);
                finish();
            } else if (intent.getExtras().getString(Constants.SENDER_ID) != null) {
                FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                        .document(intent.getStringExtra(Constants.SENDER_ID))
                        .get()
                        .addOnSuccessListener(doc -> {

                            String fullName = doc.getString(Constants.FIRST_NAME).concat(" ").concat(doc.getString(Constants.LAST_NAME));

                            UserInfo userInfo = new UserInfo(doc.getId(), doc.getString(Constants.FCM_TOKEN), fullName, doc.getString(Constants.IMAGE_PATH));

                            // setting the progress bar visibility to 'GONE'
                            binding.progressBar.setVisibility(View.GONE);

                            Intent chatIntent = new Intent(this, ChatDetailActivity.class);
                            chatIntent.putExtra(Constants.KEY_USER_INFO, userInfo);
                            chatIntent.putExtra(Constants.IS_APP_IN_FOREGROUND, false);
                            chatIntent.putExtra(Constants.RANDOM_COLOR, Commons.randomColor());
                            startActivity(chatIntent);
                            finish();
                        });
            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    setAd();
                    checkCurrentLoginStatus(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // setting the progress bar visibility to 'GONE'
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        } else {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                setAd();
                checkCurrentLoginStatus(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // setting the progress bar visibility to 'GONE'
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void checkCurrentLoginStatus(String email) {

        DocumentReference documentReference = fStore.collection(USERS_COLLECTION).document(email);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {

            runOnUiThread(() -> {

                try {
                    sleep(3500);

                    startActivity(new Intent(StartScreenActivity.this,UpdateManager.class));

                    startActivity(new Intent(StartScreenActivity.this, HomeActivity.class));
                    finish();

                    binding.progressBar.setVisibility(View.GONE);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            });
        });
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