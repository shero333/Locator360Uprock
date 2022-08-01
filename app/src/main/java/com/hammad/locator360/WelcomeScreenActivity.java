package com.hammad.locator360;

import static com.hammad.locator360.Util.Constants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.locator360.SignIn.PhoneNoSignInActivity;
import com.hammad.locator360.SignUp.PhoneNoSignUpActivity;
import com.hammad.locator360.databinding.ActivityWelcomeScreenBinding;

public class WelcomeScreenActivity extends AppCompatActivity {

    private ActivityWelcomeScreenBinding binding;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //initializing Firebase firestore
        fStore=FirebaseFirestore.getInstance();

        // Button sign up click listener
        binding.btnSignUp.setOnClickListener(v -> startActivity(new Intent(WelcomeScreenActivity.this, PhoneNoSignUpActivity.class)));

        // Textview sign in click listener
        binding.txtSignIn.setOnClickListener(v -> startActivity(new Intent(WelcomeScreenActivity.this, PhoneNoSignInActivity.class)));

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

        DocumentReference documentReference=fStore.collection(USERS).document(email);

        documentReference.get().addOnSuccessListener(documentSnapshot -> {

           startActivity(new Intent(WelcomeScreenActivity.this,JoinCircleFirstScreenActivity.class));
           finish();
        });
    }
}