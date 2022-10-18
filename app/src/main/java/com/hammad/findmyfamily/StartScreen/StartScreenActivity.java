package com.hammad.findmyfamily.StartScreen;

import static com.hammad.findmyfamily.Util.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.HomeScreen.HomeActivity;
import com.hammad.findmyfamily.OneTimeScreens.JoinCircleFirstScreenActivity;
import com.hammad.findmyfamily.SignIn.PhoneNoSignInActivity;
import com.hammad.findmyfamily.SignUp.PhoneNoSignUpActivity;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityStartScreenBinding;

public class StartScreenActivity extends AppCompatActivity {

    private ActivityStartScreenBinding binding;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityStartScreenBinding.inflate(getLayoutInflater());
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
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            checkCurrentLoginStatus(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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