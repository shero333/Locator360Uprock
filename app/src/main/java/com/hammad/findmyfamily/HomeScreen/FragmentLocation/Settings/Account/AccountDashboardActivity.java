package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.Account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityAccountDashboardBinding;

public class AccountDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ACCOUNT_DASHBOARD_ACT";

    ActivityAccountDashboardBinding binding;

    UserDetail userDetail;
    int randomImageBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityAccountDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarAccount.setNavigationOnClickListener(v -> onBackPressed());

        // profile click
        binding.consProfile.setOnClickListener(v -> {

            Intent intent = new Intent(this,UpdateProfileInfoActivity.class);
            intent.putExtra(Constants.USER_INFO,userDetail);
            intent.putExtra(Constants.RANDOM_COLOR,randomImageBackgroundColor);
            startActivity(intent);
        });

        // update phone number
        binding.textEditPhoneNo.setOnClickListener(v -> {
            Intent intent = new Intent(this,UpdatePhoneNoActivity.class);
            intent.putExtra(Constants.PHONE_NO,userDetail.getPhoneNumber());
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //getting the user info from firebase
        getUserInfo();
    }

    private void getUserInfo() {

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(doc -> {

                    userDetail = new UserDetail(doc.getString(Constants.FIRST_NAME), doc.getString(Constants.LAST_NAME)
                            ,doc.getString(Constants.PHONE_NO), doc.getString(Constants.IMAGE_PATH));

                    randomImageBackgroundColor = Commons.randomColor();

                    //set data to views
                    if(userDetail.getImagePath().equals(Constants.NULL)) {
                        binding.textNameLetter.setVisibility(View.VISIBLE);
                        binding.textNameLetter.setText(Commons.getContactLetters(userDetail.getFirstName().concat(" ").concat(userDetail.getLastName())));

                        //setting random generated color to image view background
                        binding.imageProfile.setBackgroundColor(randomImageBackgroundColor);
                    }
                    else if(!userDetail.getImagePath().equals(Constants.NULL)) {
                        binding.textNameLetter.setVisibility(View.GONE);

                        Glide.with(this)
                                .load(userDetail.getImagePath())
                                .into(binding.imageProfile);
                    }

                    binding.textUserFullName.setText(userDetail.getFirstName().concat(" ".concat(userDetail.getLastName())));


                })
                .addOnFailureListener(e -> Log.e(TAG, "error getting user info: "+e.getMessage()));
    }
}