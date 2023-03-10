package com.care360.findmyfamilyandfriends.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.care360.findmyfamilyandfriends.CreateCircle.CreateCircleActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityJoinCircleFirstScreenBinding;

import java.util.ArrayList;
import java.util.List;

public class JoinCircleFirstScreenActivity extends AppCompatActivity {

    //List of all edit texts (6 here)
    List<EditText> editTextList = new ArrayList<>();

    private ActivityJoinCircleFirstScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize binding
        binding = ActivityJoinCircleFirstScreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //adding all the edit texts in list
        addEditTextViews();

        //populating the edit texts as invite code view
        populateInviteCodeView();

        // signs up the user the basic info entered till now
        Commons.signUp(this);

        binding.btnSubmit.setOnClickListener(v -> buttonSubmitClickListener());

        binding.btnCreateCircle.setOnClickListener(v -> startActivity(new Intent(this, CreateCircleActivity.class)));
    }

    private void addEditTextViews() {
        editTextList.add(binding.edtInput1);
        editTextList.add(binding.edtInput2);
        editTextList.add(binding.edtInput3);
        editTextList.add(binding.edtInput4);
        editTextList.add(binding.edtInput5);
        editTextList.add(binding.edtInput6);
    }

    private void populateInviteCodeView() {

        //requesting the focus at 1st edit text (as default)
        binding.edtInput1.requestFocus();

        for (int j = 0; j < 6; j++) {
            //current iteration variable
            int currentIndex = j;

            editTextList.get(j).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = charSequence.toString().trim();

                    if (s.length() == 1) {
                        if (currentIndex < 5) {
                            editTextList.get(currentIndex + 1).requestFocus();
                        }

                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.drawable_pin_entered);
                    } else if (editTextList.get(currentIndex).isFocused()) {
                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.pin_view_state_list);
                    } else if (!editTextList.get(currentIndex).isFocused() || s.length() == 0) {
                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.drawable_no_pin);
                    }

                    //checking state of submit button state (enabled/disabled)
                    submitButtonStatus();

                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }
    }

    //checking the status (enabled/disabled) of submit button
    private void submitButtonStatus() {

        //checking if all Edit texts length are equal to 1
        if (Commons.isEditTextLengthZero(binding.edtInput1) && Commons.isEditTextLengthZero(binding.edtInput2) &&
                Commons.isEditTextLengthZero(binding.edtInput3) && Commons.isEditTextLengthZero(binding.edtInput4) &&
                Commons.isEditTextLengthZero(binding.edtInput5) && Commons.isEditTextLengthZero(binding.edtInput6)) {
            //setting the submit button to enabled
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.setBackgroundResource(R.drawable.orange_rounded_button);
        } else {
            //setting the submit button to disabled
            binding.btnSubmit.setEnabled(false);
            binding.btnSubmit.setBackgroundResource(R.drawable.disabled_round_button);
        }
    }

    private void buttonSubmitClickListener() {

        //setting the progress bar visibility
        binding.progressBar.setVisibility(View.VISIBLE);

        //variable for saving the entered code
        String enteredInviteCode = Commons.getEditTextData(binding.edtInput1)
                                    .concat(Commons.getEditTextData(binding.edtInput2))
                                    .concat(Commons.getEditTextData(binding.edtInput3)
                                    .concat(Commons.getEditTextData(binding.edtInput4)
                                    .concat(Commons.getEditTextData(binding.edtInput5)
                                    .concat(Commons.getEditTextData(binding.edtInput6)))));

        // join circle method
        Commons.checkCircleAvailability(this, enteredInviteCode, (doesCircleExist, circleModel) -> {

            //setting the progress bar visibility
            binding.progressBar.setVisibility(View.GONE);

            if (doesCircleExist) {
                Intent intent = new Intent(this, JoinCircleActivity.class);
                intent.putExtra(Constants.CIRCLE, circleModel);
                intent.putExtra(Constants.CIRCLE_JOIN_ACT_KEY, true);
                startActivity(intent);
                finish();
            }

        });

    }

}