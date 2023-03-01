package com.kl360.findmyfamilyandfriends.CreateCircle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kl360.findmyfamilyandfriends.R;
import com.kl360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.kl360.findmyfamilyandfriends.databinding.ActivityCreateCircleBinding;

public class CreateCircleActivity extends AppCompatActivity {

    private ActivityCreateCircleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreateCircleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //text watcher
        binding.edtTxtCircleName.addTextChangedListener(createCircleTextWatcher);

        //button continue click listener
        binding.btnContCreateCircle.setOnClickListener(v -> {

            //saving the value in shared preference
            SharedPreference.setCircleName(binding.edtTxtCircleName.getText().toString());

            //navigate to next activity
            startActivity(new Intent(this, ShareCircleCodeActivity.class));
        });
    }

    private final TextWatcher createCircleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            //validating the circle name (minimum 4 characters)
            if(s.length() > 3){

                //enabling the 'continue' button
                binding.btnContCreateCircle.setEnabled(true);
                binding.btnContCreateCircle.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else{

                //disable the 'continue' button
                binding.btnContCreateCircle.setEnabled(false);
                binding.btnContCreateCircle.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}