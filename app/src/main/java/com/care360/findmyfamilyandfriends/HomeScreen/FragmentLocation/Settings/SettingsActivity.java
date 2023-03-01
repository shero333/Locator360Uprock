package com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.Account.AccountDashboardActivity;
import com.kl360.findmyfamilyandfriends.HomeScreen.FragmentLocation.Settings.CircleManagement.CircleManagementActivity;
import com.kl360.findmyfamilyandfriends.R;
import com.kl360.findmyfamilyandfriends.Util.Commons;
import com.kl360.findmyfamilyandfriends.Util.Constants;
import com.kl360.findmyfamilyandfriends.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTING_ACT";
    
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // personal data
        binding.consPersonalData.setOnClickListener(v -> startActivity(new Intent(this, AccountDashboardActivity.class)));

        // circle management
        binding.consCircleManagement.setOnClickListener(v -> startActivity(new Intent(this, CircleManagementActivity.class)));

        // about
        binding.consAbout.setOnClickListener(v -> viewPrivacyPolicy());

        // sign out
        binding.consLogout.setOnClickListener(v -> Commons.signOut(this));
    }

    private void getCurrentUserInfo() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(doc -> {

                    //setting the data to views
                    setDataToViews(doc.getString(Constants.FIRST_NAME),doc.getString(Constants.LAST_NAME),doc.getString(Constants.PHONE_NO),doc.getString(Constants.IMAGE_PATH));

                })
                .addOnFailureListener(e -> Log.i(TAG, "getting user info error:" + e.getMessage()));
    }

    private void setDataToViews(String firstName, String lastName, String phoneNo, String imagePath) {

        if(imagePath != null)
        {
            // profile image (if any)
            if(imagePath.equals(Constants.NULL)) {
                binding.textUserNameLetters.setVisibility(View.VISIBLE);
                binding.textUserNameLetters.setText(String.valueOf(firstName.charAt(0)));
            }
            else if(!imagePath.equals(Constants.NULL)) {
                binding.textUserNameLetters.setVisibility(View.GONE);

                //loading the image
                Glide
                        .with(this)
                        .load(imagePath)
                        .into(binding.profileImage);
            }
        }

        if(firstName != null && lastName != null) {
            // user name
            binding.textFullName.setText(firstName.concat(" ").concat(lastName));
        }
        // user name
        //binding.textFullName.setText(firstName.concat(" ").concat(lastName));

        if(phoneNo != null) {
            binding.textPhoneNo.setText(phoneNo);
        }
        // phone number
        //binding.textPhoneNo.setText(phoneNo);

    }

    private void viewPrivacyPolicy() {
        Uri uri = Uri.parse("https://risibleapps.blogspot.com/2022/02/privacy-policy-at-risibleapps-we.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //exit animation activity
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get the current user info
        getCurrentUserInfo();
    }
}