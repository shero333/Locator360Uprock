package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTING_ACT";
    
    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the current user info
        getCurrentUserInfo();

        // personal data
        binding.consPersonalData.setOnClickListener(v -> Toast.makeText(this, "Personal Data", Toast.LENGTH_SHORT).show());

        // circle management
        binding.consCircleManagement.setOnClickListener(v -> Toast.makeText(this, "Circle Management", Toast.LENGTH_SHORT).show());

        // about
        binding.consAbout.setOnClickListener(v -> Toast.makeText(this, "About", Toast.LENGTH_SHORT).show());

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
                .addOnFailureListener(e -> Log.e(TAG, "getting user info error:" + e.getMessage()));
        
    }

    private void setDataToViews(String firstName, String lastName, String phoneNo, String imagePath) {

        // profile image (if any)
        if(imagePath.equals(Constants.NULL)) {
            binding.textUserNameLetters.setVisibility(View.VISIBLE);
            binding.textUserNameLetters.setText(Commons.getContactLetters(firstName.concat(" ").concat(lastName)));
        }
        else if(!imagePath.equals(Constants.NULL)) {
            binding.textUserNameLetters.setVisibility(View.GONE);

            //loading the image
            Glide
                    .with(this)
                    .load(imagePath)
                    .into(binding.profileImage);
        }

        // user name
        binding.textFullName.setText(firstName.concat(" ").concat(lastName));

        // phone number
        binding.textPhoneNo.setText(phoneNo);

    }


}