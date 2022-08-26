package com.hammad.locator360.CreateCircle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hammad.locator360.R;
import com.hammad.locator360.databinding.ActivityCreateCircleBinding;

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
        binding.btnContCreateCircle.setOnClickListener(v -> startActivity(new Intent(this, ShareCircleCodeActivity.class)));
    }

    private TextWatcher createCircleTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            //validating the email
            if(s.length() > 0){

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