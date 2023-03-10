package com.care360.findmyfamilyandfriends.OneTimeScreens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.databinding.ActivityJoinCircleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.JoinCircle.CircleModel;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Constants;

import java.util.Objects;

public class JoinCircleActivity extends AppCompatActivity {

    private static final String TAG = "JOIN_CIRCLE";

    private ActivityJoinCircleBinding binding;

    // boolean for differentiating whether activity is called from home fragment or from sign up act
    boolean isCalledFromSignUp;

    CircleModel circleModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //view binding initialization
        binding = ActivityJoinCircleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getting intent data
        getIntentData();

        //button cancel click listener
        binding.txtCancel.setOnClickListener(v -> finish());

        //button join click listener
        binding.btnJoinGroup.setOnClickListener(v -> joinCircleClickListener());
    }

    private void getIntentData() {

        Intent intent = getIntent();

        isCalledFromSignUp = intent.getBooleanExtra(Constants.CIRCLE_JOIN_ACT_KEY,false);

        circleModel = intent.getParcelableExtra(Constants.CIRCLE);

        //if circle data is not null. Set the values to views
        if(circleModel != null) {

            //circle name
            binding.txtCircleName.setText(circleModel.getCircleName());

            // circle name's first char
            binding.txtCircleNameFirstChar.setText(String.valueOf(circleModel.getCircleName().charAt(0)));
        }

    }

    private void joinCircleClickListener() {

        //boolean for checking if member already exist
        boolean memberAlreadyPresent = false;

        //setting the progress bar visibility
        binding.progressBar.setVisibility(View.VISIBLE);

        String currentUserEmail;

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        }
        else {
            currentUserEmail = SharedPreference.getEmailPref();
        }

        //checking if member already exist or not
        for (String member: circleModel.getCircleMembersList()) {

            if(member.equals(currentUserEmail)) {
                memberAlreadyPresent = true;
                break;
            }

        }

        if(memberAlreadyPresent) {
            Toast.makeText(this, "Circle already Joined.", Toast.LENGTH_SHORT).show();

            //setting the progress bar visibility
            binding.progressBar.setVisibility(View.GONE);
        }
        else {

            //add new member in circle
            FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                    .document(circleModel.getCircleOwnerId())
                    .collection(Constants.CIRCLE_COLLECTION)
                    .document(circleModel.getCircleId())
                    .update(Constants.CIRCLE_MEMBERS, FieldValue.arrayUnion(currentUserEmail))
                    .addOnSuccessListener(unused -> {

                        //setting the progress bar visibility
                        binding.progressBar.setVisibility(View.GONE);

                        //saving the circle info in shared preference
                        SharedPreference.setCircleId(circleModel.getCircleId());
                        SharedPreference.setCircleName(circleModel.getCircleName());
                        SharedPreference.setCircleInviteCode(circleModel.getCircleJoinCode());

                        if(isCalledFromSignUp) {
                            startActivity(new Intent(this, AddProfilePictureActivity.class));
                        }
                        else {
                            Intent intent = new Intent();
                            intent.putExtra(Constants.RETURNED_CIRCLE_NAME,circleModel.getCircleName());
                            setResult(Activity.RESULT_OK,intent);
                        }

                        finish();

                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "join circle error: "+e.getMessage());

                        //setting the progress bar visibility
                        binding.progressBar.setVisibility(View.GONE);

                        //toast message
                        Toast.makeText(this, "Error! Try again.", Toast.LENGTH_SHORT).show();
                    });

        }

    }
}