package com.hammad.findmyfamily.HomeScreen.FragmentLocation.CreateCircle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityCreateCircleMainBinding;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateCircleMainActivity extends AppCompatActivity {

    private static final String TAG = "CIRCLE_MAIN";

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
            if(s.length() > 3) {

                //enabling the 'continue' button
                binding.btnCreateCircle.setEnabled(true);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.white_rounded_button);
            }
            else {

                //disable the 'continue' button
                binding.btnCreateCircle.setEnabled(false);
                binding.btnCreateCircle.setBackgroundResource(R.drawable.disabled_round_button);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void buttonClickListener() {

        //setting progress bar visibility
        binding.progressBar.setVisibility(View.VISIBLE);

        String circleName = binding.edtTxtCircleName.getText().toString();
        String circleCode = Commons.getRandomGeneratedCircleCode();
        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        //setting the circle info as sub-collection data
        Map<String, Object> circleData = new HashMap<>();

        circleData.put(Constants.CIRCLE_NAME, circleName);
        circleData.put(Constants.CIRCLE_JOIN_CODE, circleCode);
        circleData.put(Constants.CIRCLE_ADMIN, currentUserEmail);
        circleData.put(Constants.CIRCLE_TIME_STAMP, new Timestamp(new Date()));
        circleData.put(Constants.CIRCLE_CODE_EXPIRY_DATE, new Timestamp(new Date()));
        circleData.put(Constants.CIRCLE_MEMBERS, FieldValue.arrayUnion(currentUserEmail));

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .collection(Constants.CIRCLE_COLLECTION)
                .add(circleData)
                .addOnSuccessListener(documentReference -> {

                    //saving the newly created circle document id in User collection
                    FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                            .document(currentUserEmail)
                            .update(Constants.CIRCLE_IDS,FieldValue.arrayUnion(documentReference.getId()))
                            .addOnSuccessListener(unused -> {

                                //setting progress bar visibility
                                binding.progressBar.setVisibility(View.GONE);

                                // setting the circle id & name to shared preference
                                SharedPreference.setCircleId(documentReference.getId());
                                SharedPreference.setCircleName(circleName);

                                //navigating back to location fragment
                                finishCurrentActivity(true);

                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "failed to add Circle id: " + e.getMessage());
                                //setting progress bar visibility
                                binding.progressBar.setVisibility(View.GONE);
                            });

                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "failed to create new circle: "+e.getMessage());

                    //setting progress bar visibility
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Error! Failed to create circle.", Toast.LENGTH_LONG).show();
                });

    }

    private void finishCurrentActivity(boolean isCircleCreated) {

        Intent intentToReturn = new Intent();
        intentToReturn.putExtra(Constants.IS_CIRCLE_CREATED,isCircleCreated);
        setResult(Activity.RESULT_OK,intentToReturn);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishCurrentActivity(false);
    }
}