package com.hammad.findmyfamily.SignIn.ResetPassword;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.StartScreen.StartScreenActivity;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.ActivityCreateNewPasswordBinding;

public class CreateNewPasswordActivity extends AppCompatActivity {

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

            String s = charSequence.toString().trim();

            strPassword = Commons.encryptedText(s);

            if(s.length() >= 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(" ");

                if(strPassword.equals(strConfirmPassword)) {
                    passwordsMatchScenario();
                }
                else if(!strPassword.equals(strConfirmPassword)) {

                    if(binding.edtConfirmPassword.length() == 0) {

                        binding.layoutConfirmPassword.setHelperText(" ");

                        //setting the reset button state to disable
                        setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));
                    }
                    else {
                        passwordsMismatchScenario();
                    }

                }

            }
            else if(s.length() < 8) {

                //setting the helper text
                binding.layoutPassword.setHelperText(getString(R.string.minimum_8_character_password));

                //setting the helper text of confirm password to empty
                binding.layoutConfirmPassword.setHelperText(" ");

                //setting the reset button status to disabled
                setResetPasswordButtonStatus(false,R.drawable.disabled_round_button,getColor(R.color.orange));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }

    };

    private final TextWatcher confirmPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            strConfirmPassword = Commons.encryptedText(s);

            if(strConfirmPassword.equals(strPassword)) {

                if(binding.edtConfirmPassword.length() == 0 && binding.edtPassword.length() == 0) {

                    binding.layoutConfirmPassword.setHelperText(" ");

                    //setting the reset button state to disable
                    setResetPasswordButtonStatus(false, R.drawable.disabled_round_button, getColor(R.color.orange));

                }
                else {
                    passwordsMatchScenario();
                }

            }
            else if(!strConfirmPassword.equals(strPassword)) {

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

        //update password in firebase

        Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, StartScreenActivity.class));
        finish();
    }

}