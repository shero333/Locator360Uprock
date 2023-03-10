package com.care360.findmyfamilyandfriends.CreateCircle;

import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.databinding.ActivityShareCircleCodeBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.OneTimeScreens.AddProfilePictureActivity;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.util.HashMap;
import java.util.Map;

public class ShareCircleCodeActivity extends AppCompatActivity {

    private static final String TAG = "SHARE_CIRCLE_CODE_ACT";

    private ActivityShareCircleCodeBinding binding;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityShareCircleCodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        //getting the randomly generated circle code
        setCircleCode(Commons.getRandomGeneratedCircleCode());

        //share code click listener
        binding.btnShareCode.setOnClickListener(v -> shareCircleCode());

        //text close click listener
        binding.txtClose.setOnClickListener(v -> startActivity(new Intent(this, AddProfilePictureActivity.class)));
    }

    //setting the code to textviews
    private void setCircleCode(String code) {
        binding.textViewCircle1.setText(String.valueOf(code.charAt(0)));
        binding.textViewCircle2.setText(String.valueOf(code.charAt(1)));
        binding.textViewCircle3.setText(String.valueOf(code.charAt(2)));
        binding.textViewCircle4.setText(String.valueOf(code.charAt(3)));
        binding.textViewCircle5.setText(String.valueOf(code.charAt(4)));
        binding.textViewCircle6.setText(String.valueOf(code.charAt(5)));

        //saving the circle code in preference as well
        SharedPreference.setCircleInviteCode(code);

        // saves the circle info in Firebase
        createCircle();
    }

    //this function will have to implement the Firebase dynamic link
    private void shareCircleCode() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String body = "You're invited to join circle on Find My Family.\nCircle Name: "+SharedPreference.getCircleName()+"\nJoin Code: "+SharedPreference.getCircleInviteCode()

                +"\n\nHaven't download this application yet? Download now from:\n\n"+
                "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

        intent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void createCircle() {

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference dr = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                .document(currentUserEmail);

        //setting the circle info as sub-collection data
        Map<String, Object> circleData = new HashMap<>();

        circleData.put(Constants.CIRCLE_NAME, SharedPreference.getCircleName());
        circleData.put(Constants.CIRCLE_JOIN_CODE, SharedPreference.getCircleInviteCode());
        circleData.put(Constants.CIRCLE_ADMIN, currentUserEmail);
        circleData.put(Constants.CIRCLE_ID,Constants.NULL);
        circleData.put(Constants.CIRCLE_CODE_EXPIRY_DATE, String.valueOf(System.currentTimeMillis()+259200000)); // 259200000 milliseconds = 3 days
        circleData.put(Constants.CIRCLE_MEMBERS, FieldValue.arrayUnion(SharedPreference.getEmailPref()));

        dr.collection(Constants.CIRCLE_COLLECTION).add(circleData)
                .addOnSuccessListener(documentReference -> {

                    Log.i(TAG, "createCircle: successful");

                    /*
                        when in 'Circle' sub-collection, a new document is created,
                        its id will be stored in 'User' collection document as field. Later this ids will be used to extract Circle info
                    */

                    DocumentReference documentRefCircleId = FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                            .document(currentUserEmail);

                    //add the created circle id as field in same circle document
                    FirebaseFirestore.getInstance().collection(USERS_COLLECTION)
                            .document(currentUserEmail)
                            .collection(Constants.CIRCLE_COLLECTION)
                            .document(documentReference.getId())
                            .update(Constants.CIRCLE_ID,documentReference.getId());

                    documentRefCircleId.update(Constants.CIRCLE_IDS, FieldValue.arrayUnion(documentReference.getId()))
                            .addOnSuccessListener(unused -> Log.i(TAG, "circle id added successfully in USER collection"))
                            .addOnFailureListener(e -> Log.e(TAG, "failed to add circle id in USER collection: "+e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e(TAG, "error in creating circle: " +e.getMessage()));

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