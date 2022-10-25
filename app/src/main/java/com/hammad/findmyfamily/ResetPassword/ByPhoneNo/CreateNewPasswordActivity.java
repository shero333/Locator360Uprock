package com.hammad.findmyfamily.ResetPassword.ByPhoneNo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.StartScreen.StartScreenActivity;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityCreateNewPasswordBinding;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ACT_NEW_PASS";

    private ActivityCreateNewPasswordBinding binding;

    //string for saving the entered passwords
    private String strPassword = "", strConfirmPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreateNewPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //adding text watchers
        binding.edtPassword.addTextChangedListener(passwordTextWatcher);

        binding.edtConfirmPassword.addTextChangedListener(confirmPasswordTextWatcher);

        // button reset password click listener
        binding.btnResetPassword.setOnClickListener(v -> resetPasswordClickListener());
    }

    private final TextWatcher passwordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            strPassword = charSequence.toString().trim();

            if (strPassword.length() >= 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(" ");

                if (strPassword.equals(strConfirmPassword)) {
                    passwordsMatchScenario();
                } else if (!strPassword.equals(strConfirmPassword)) {

                    if (binding.edtConfirmPassword.length() == 0) {

                        binding.layoutConfirmPassword.setHelperText(" ");

                        //setting the reset button state to disable
                        setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
                    } else {
                        passwordsMismatchScenario();
                    }

                }

            }
            else if (strPassword.length() < 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(getString(R.string.minimum_8_character_password));

                //setting the helper text of confirm password to empty
                binding.layoutConfirmPassword.setHelperText(" ");

                //setting the reset button status to disabled
                setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    };
    private final TextWatcher confirmPasswordTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            strConfirmPassword = charSequence.toString().trim();

            if (strConfirmPassword.equals(strPassword)) {

                if (binding.edtConfirmPassword.length() == 0 && binding.edtPassword.length() == 0) {

                    binding.layoutConfirmPassword.setHelperText(" ");

                    //setting the reset button state to disable
                    setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));

                } else {
                    passwordsMatchScenario();
                }

            } else if (!strConfirmPassword.equals(strPassword)) {

                passwordsMismatchScenario();
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void setResetPasswordButtonStatus(boolean status, int layout, int color) {
        binding.btnResetPassword.setEnabled(status);
        binding.btnResetPassword.setBackgroundResource(layout);
        binding.btnResetPassword.setTextColor(color);
    }

    private void passwordsMatchScenario() {

        //setting the matched passwords helper text
        binding.layoutConfirmPassword.setHelperText(getString(R.string.passwords_matched));
        binding.layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.holo_green_dark)));

        //setting the reset button state to enabled
        setResetPasswordButtonStatus(true, R.drawable.orange_rounded_button, getColor(R.color.white));
    }

    private void passwordsMismatchScenario() {

        //setting the mis-match passwords helper text
        binding.layoutConfirmPassword.setHelperText(getString(R.string.passwords_does_not_match));
        binding.layoutConfirmPassword.setHelperTextColor(ColorStateList.valueOf(getColor(R.color.holo_red_dark)));

        //setting the reset button state to disable
        setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
    }

    private void resetPasswordClickListener() {

        //sets progress bar visibility to VISIBLE
        binding.progressBar.setVisibility(View.VISIBLE);

        //update password in firebase
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .whereEqualTo(Constants.PHONE_NO, SharedPreference.getPhoneNoPref())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    //gets the registered email
                    String val = queryDocumentSnapshots.getDocuments().get(0).getString(Constants.EMAIL);

                    //update password value in registered email document
                    DocumentReference dr = FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION).document(val);

                    dr.update(Constants.PASSWORD, strConfirmPassword)
                            .addOnSuccessListener(unused -> {

                                //sets progress bar visibility to GONE
                                binding.progressBar.setVisibility(View.GONE);

                                Toast.makeText(this, "Password reset successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, StartScreenActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {

                                //sets progress bar visibility to GONE
                                binding.progressBar.setVisibility(View.GONE);

                                Log.e(TAG, "failed to update password: " + e.getMessage());
                                Toast.makeText(this, "Error! Failed to Update Password.", Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {

                    //sets progress bar visibility to GONE
                    binding.progressBar.setVisibility(View.GONE);

                    Log.i(TAG, "cannot find phone no: " + e.getMessage());
                    Toast.makeText(this, "Error! cannot find Entered Phone Number.", Toast.LENGTH_LONG).show();
                });
    }

}