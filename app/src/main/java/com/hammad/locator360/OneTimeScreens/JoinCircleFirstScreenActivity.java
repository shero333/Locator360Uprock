package com.hammad.locator360.OneTimeScreens;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.databinding.ActivityJoinCircleFirstScreenBinding;

public class JoinCircleFirstScreenActivity extends AppCompatActivity {

    private ActivityJoinCircleFirstScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityJoinCircleFirstScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        //pin TextWatcher
       /* binding.pinView.addTextChangedListener(pinTextWatcher);*/

        binding.btnSubmit.setOnClickListener(v -> Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show());

    }

    private TextWatcher pinTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString();

            Toast.makeText(JoinCircleFirstScreenActivity.this, s, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}