package com.hammad.findmyfamily.StartScreen;

import static com.hammad.findmyfamily.Util.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencySOS.EmergencyLocationActivity;
import com.hammad.findmyfamily.HomeScreen.HomeActivity;
import com.hammad.findmyfamily.SignIn.PhoneNoSignInActivity;
import com.hammad.findmyfamily.SignUp.PhoneNoSignUpActivity;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityStartScreenBinding;

public class StartScreenActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        com.hammad.findmyfamily.databinding.ActivityStartScreenBinding binding = ActivityStartScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initializing Firebase firestore
        fStore=FirebaseFirestore.getInstance();

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

        if(intent.getExtras() != null) {
            if (intent.getExtras().getString(Constants.FCM_LAT) != null && intent.getExtras().getString(Constants.FCM_LNG) != null)
            {
                Intent sosEmergIntent = new Intent(this, EmergencyLocationActivity.class);

                sosEmergIntent.putExtra(Constants.FCM_LAT,intent.getExtras().getString(Constants.FCM_LAT));
                sosEmergIntent.putExtra(Constants.FCM_LNG,intent.getExtras().getString(Constants.FCM_LNG));
                sosEmergIntent.putExtra(Constants.IS_APP_IN_FOREGROUND,false);
                startActivity(sosEmergIntent);
                finish();
            }
            else {
                if(FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    checkCurrentLoginStatus(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
            }
        }
        else {
            if(FirebaseAuth.getInstance().getCurrentUser() != null)
            {
                checkCurrentLoginStatus(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            }
        }
    }

    private void checkCurrentLoginStatus(String email) {

        DocumentReference documentReference=fStore.collection(USERS_COLLECTION).document(email);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {

            startActivity(new Intent(StartScreenActivity.this, HomeActivity.class));
            finish();
        });
    }
}