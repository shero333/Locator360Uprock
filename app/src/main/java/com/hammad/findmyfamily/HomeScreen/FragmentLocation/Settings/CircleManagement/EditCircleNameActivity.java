package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEditCircleNameBinding;

public class EditCircleNameActivity extends AppCompatActivity {

    private static final String TAG = "EDT_CIRCLE_NAME_ACT";
    ActivityEditCircleNameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityEditCircleNameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // toolbar back pressed
        binding.toolbarUpdateCircleName.setNavigationOnClickListener(v -> onBackPressed());

        // setting the circle name in edit text
        binding.edtTxtCircleName.setText(SharedPreference.getCircleName());

        //text watcher
        binding.edtTxtCircleName.addTextChangedListener(updateCircleNameTextWatcher);

        // update button click listener
        binding.btnUpdateCircleName.setOnClickListener(v -> buttonClickListener());
    }

    private final TextWatcher updateCircleNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String s = charSequence.toString().trim();

            //validating the circle name (minimum 4 characters)
            if(s.length() > 3) {

                //enabling the 'continue' button
                binding.btnUpdateCircleName.setEnabled(true);
                binding.btnUpdateCircleName.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else {

                //disable the 'continue' button
                binding.btnUpdateCircleName.setEnabled(false);
                binding.btnUpdateCircleName.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        //setting progress bar visibility
        binding.progressBar.setVisibility(View.VISIBLE);

        String updatedCircleName = binding.edtTxtCircleName.getText().toString().trim();

        // updates the circle name
        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(SharedPreference.getCircleAdminId())
                .collection(Constants.CIRCLE_COLLECTION)
                .document(SharedPreference.getCircleId())
                .update(Constants.CIRCLE_NAME,updatedCircleName)
                .addOnSuccessListener(unused -> {

                    //setting progress bar visibility
                    binding.progressBar.setVisibility(View.GONE);

                    Log.i(TAG, "circle name updated successfully");
                    Toast.makeText(this, "Circle Name Updated", Toast.LENGTH_SHORT).show();

                    // saving the updated circle name in shared preference
                    SharedPreference.setCircleName(updatedCircleName);
                })
                .addOnFailureListener(e -> {

                    //setting progress bar visibility
                    binding.progressBar.setVisibility(View.GONE);

                    Log.e(TAG, "error updating circle name: " + e.getMessage());
                    Toast.makeText(this, "Error! Failed to update Circle Name.", Toast.LENGTH_SHORT).show();
                });
    }
}