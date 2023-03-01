package com.care360.findmyfamilyandfriends.ResetPassword.ByEmail;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityResetPasswordEmailBinding;

public class ResetPasswordEmailActivity extends AppCompatActivity {

    private static final String TAG = "RESET_PASS_EMAIL";

    private ActivityResetPasswordEmailBinding binding;

    private String enteredEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityResetPasswordEmailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //text watcher
        binding.edtEmail.addTextChangedListener(emailTextWatcher);

        //button click listener
        binding.btnSendInstruction.setOnClickListener(v -> buttonClickListener());

    }

    private final TextWatcher emailTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            enteredEmail = charSequence.toString();

            //setting the helper text value to empty
            binding.layoutEmail.setHelperText(" ");

            //validating the email
            if (Commons.validateEmailAddress(enteredEmail)) {

                //enabling the button
                binding.btnSendInstruction.setEnabled(true);
                binding.btnSendInstruction.setBackgroundResource(R.drawable.orange_rounded_button);
                binding.btnSendInstruction.setTextColor(getColor(R.color.white));
            }
            else {

                //disable the button
                binding.btnSendInstruction.setEnabled(false);
                binding.btnSendInstruction.setBackgroundResource(R.drawable.disabled_round_button);
                binding.btnSendInstruction.setTextColor(getColor(R.color.orange));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    };

    private void buttonClickListener() {

        // checking if the entered email is registered or not
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(enteredEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()) {

                        //setting the helper text to empty
                        binding.layoutEmail.setHelperText(" ");

                        //sending the recovery email
                        FirebaseAuth.getInstance().
                                sendPasswordResetEmail(enteredEmail)
                                .addOnSuccessListener(unused -> {

                                    Log.i(TAG, "password reset email send successfully");

                                    //navigating to next activity
                                    startActivity(new Intent(this, CheckEmailActivity.class));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "password reset email sending failed: " + e.getMessage());
                                    Toast.makeText(this, "Error! Failed to send Reset Password Request.", Toast.LENGTH_LONG).show();
                                });
                    }
                    else if(!documentSnapshot.exists()) {

                        //setting the helper text
                        binding.layoutEmail.setHelperText("email does not exist");

                        //toast message
                        Toast.makeText(this, "Error! email does not exist.", Toast.LENGTH_LONG).show();

                    }

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "on failure: " + e.getMessage());
                    Toast.makeText(this, "Error! Try again.", Toast.LENGTH_LONG).show();
                });

    }
}