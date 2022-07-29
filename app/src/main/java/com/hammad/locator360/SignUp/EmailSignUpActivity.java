package com.hammad.locator360.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.hammad.locator360.SharedPreference.SharedPreference;
import com.hammad.locator360.databinding.ActivityEmailSignUpBinding;

public class EmailSignUpActivity extends AppCompatActivity {

    private ActivityEmailSignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityEmailSignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getting pref values
        Log.i("HELLO_123", "phone no: "+ SharedPreference.getPhoneNoPref());
        Log.i("HELLO_123", "first name: "+ SharedPreference.getFirstNamePref());
        Log.i("HELLO_123", "last name: "+ SharedPreference.getLastNamePref());
    }

    private boolean validateEmailAddress(EditText editText) {
        String input = editText.getText().toString();

        if (!input.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            //enable the continue button
            return true;
        } else {
            //disables the continue button
            return false;
        }
    }
}