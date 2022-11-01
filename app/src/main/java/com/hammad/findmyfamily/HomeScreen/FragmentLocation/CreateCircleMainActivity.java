package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityCreateCircleMainBinding;

public class CreateCircleMainActivity extends AppCompatActivity {

    ActivityCreateCircleMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityCreateCircleMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // toolbar back pressed
        binding.toolbarCreateCircle.setNavigationOnClickListener(v -> onBackPressed());

        //text watcher
        binding.edtTxtCircleName.addTextChangedListener(createCircleTextWatcher);

        //click listener
        binding.btnCreateCircle.setOnClickListener(v -> buttonClickListener());
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
                binding.btnCreateCircle.setEnabled(true);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else{

                //disable the 'continue' button
                binding.btnCreateCircle.setEnabled(false);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        Toast.makeText(this, "Button Click", Toast.LENGTH_SHORT).show();

    }
}