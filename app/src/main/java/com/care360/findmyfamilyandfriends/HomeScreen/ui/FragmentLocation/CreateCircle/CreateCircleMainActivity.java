package com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.CreateCircle;

import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityCreateCircleMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateCircleMainActivity extends AppCompatActivity {

    private static final String TAG = "CIRCLE_MAIN";

    ActivityCreateCircleMainBinding binding;
    private InterstitialAd mInterstitialAd = null;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreateCircleMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        adRequest = new AdRequest.Builder().build();

        setAd();
        //banner

        binding.bannerAd.loadAd(adRequest);

        // toolbar back pressed
        binding.toolbarCreateCircle.setNavigationOnClickListener(v -> onBackPressed());

        //text watcher
        binding.edtTxtCircleName.addTextChangedListener(createCircleTextWatcher);

        //click listener
        binding.btnCreateCircle.setOnClickListener(v -> buttonClickListener());
    }

    private final TextWatcher createCircleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            //validating the circle name (minimum 4 characters)
            if(s.length() > 3) {

                //enabling the 'continue' button
                binding.btnCreateCircle.setEnabled(true);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else {

                //disable the 'continue' button
                binding.btnCreateCircle.setEnabled(false);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        //setting progress bar visibility
        binding.progressBar.setVisibility(View.VISIBLE);

        String circleName = binding.edtTxtCircleName.getText().toString();
        String circleCode = Commons.getRandomGeneratedCircleCode();
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        //setting the circle info as sub-collection data
        Map<String, Object> circleData = new HashMap<>();

        circleData.put(Constants.CIRCLE_NAME, circleName);
        circleData.put(Constants.CIRCLE_JOIN_CODE, circleCode);
        circleData.put(Constants.CIRCLE_ADMIN, currentUserEmail);
        circleData.put(Constants.CIRCLE_ID,null);
        circleData.put(Constants.CIRCLE_CODE_EXPIRY_DATE, String.valueOf(System.currentTimeMillis()+259200000)); // 259200000 milliseconds = 3 days.
        circleData.put(Constants.CIRCLE_MEMBERS, FieldValue.arrayUnion(currentUserEmail));

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .collection(Constants.CIRCLE_COLLECTION)
                .add(circleData)
                .addOnSuccessListener(documentReference -> {

                    //add the created circle id as field in same circle document
                    FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                            .document(currentUserEmail)
                            .collection(Constants.CIRCLE_COLLECTION)
                            .document(documentReference.getId())
                            .update(Constants.CIRCLE_ID,documentReference.getId());

                    //saving the newly created circle document id in User collection
                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(currentUserEmail)
                            .update(Constants.CIRCLE_IDS,FieldValue.arrayUnion(documentReference.getId()))
                            .addOnSuccessListener(unused -> {

                                //setting progress bar visibility
                                binding.progressBar.setVisibility(View.GONE);

                                // setting the circle id, name & join code to shared preference
                                SharedPreference.setCircleId(documentReference.getId());
                                SharedPreference.setCircleName(circleName);
                                SharedPreference.setCircleInviteCode(circleCode);

                                //navigating back to location fragment
                                finishCurrentActivity(true);

                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "failed to add Circle id: " + e.getMessage());
                                //setting progress bar visibility
                                binding.progressBar.setVisibility(View.GONE);
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "failed to create new circle: "+e.getMessage());

                    //setting progress bar visibility
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Error! Failed to create circle.", Toast.LENGTH_LONG).show();
                });

    }

    private void finishCurrentActivity(boolean isCircleCreated) {

        Intent intentToReturn = new Intent();
        intentToReturn.putExtra(Constants.IS_CIRCLE_CREATED,isCircleCreated);
        setResult(Activity.RESULT_OK,intentToReturn);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishCurrentActivity(false);
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