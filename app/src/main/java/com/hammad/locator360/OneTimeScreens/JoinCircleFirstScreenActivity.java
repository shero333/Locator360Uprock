package com.hammad.locator360.OneTimeScreens;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.R;
import com.hammad.locator360.databinding.ActivityJoinCircleFirstScreenBinding;

import java.util.ArrayList;
import java.util.List;

public class JoinCircleFirstScreenActivity extends AppCompatActivity {

    private ActivityJoinCircleFirstScreenBinding binding;

    //List of all edit texts (6 here)
    List<EditText> editTextList = new ArrayList<>();

    //variable for saving the entered code
    private String enteredInviteCode;

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

        binding.btnSubmit.setOnClickListener(v -> buttonSubmitClickListener());

        binding.btnCreateCircle.setOnClickListener(v -> Toast.makeText(this, "Create Circle", Toast.LENGTH_SHORT).show());
    }

    private void addEditTextViews(){
        editTextList.add(binding.edtInput1);
        editTextList.add(binding.edtInput2);
        editTextList.add(binding.edtInput3);
        editTextList.add(binding.edtInput4);
        editTextList.add(binding.edtInput5);
        editTextList.add(binding.edtInput6);
    }

    private void populateInviteCodeView(){

        //requesting the focus at 1st edit text (as default)
        binding.edtInput1.requestFocus();

        for (int j = 0; j < 6; j++)
        {
            //current iteration variable
            int currentIndex = j;

            editTextList.get(j).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String s = charSequence.toString().trim();

                    if(s.length() == 1)
                    {
                        if(currentIndex < 5)
                        {
                            editTextList.get(currentIndex+1).requestFocus();
                        }

                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.drawable_pin_entered);
                    }
                    else if(editTextList.get(currentIndex).isFocused())
                    {
                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.pin_view_state_list);
                    }
                    else if(!editTextList.get(currentIndex).isFocused() || s.length() == 0)
                    {
                        editTextList.get(currentIndex).setBackgroundResource(R.drawable.drawable_no_pin);
                    }

                    //checking state of submit button state (enabled/disabled)
                    submitButtonStatus();

                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    private boolean isEditTextLengthZero(EditText editText){

        if(editText.getText().toString().trim().length() == 1){
            return true;
        }
        else {
            return false;
        }
    }

    //checking the status (enabled/disabled) of submit button
    private void submitButtonStatus(){

        //checking if all Edit texts length are equal to 1
        if(isEditTextLengthZero(binding.edtInput1) && isEditTextLengthZero(binding.edtInput2) &&
                isEditTextLengthZero(binding.edtInput3) && isEditTextLengthZero(binding.edtInput4) &&
                isEditTextLengthZero(binding.edtInput5) && isEditTextLengthZero(binding.edtInput6))
        {
            //setting the submit button to enabled
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.setBackgroundResource(R.drawable.orange_rounded_button);
        }
        else{
            //setting the submit button to disabled
            binding.btnSubmit.setEnabled(false);
            binding.btnSubmit.setBackgroundResource(R.drawable.disabled_round_button);
        }
    }

    private void buttonSubmitClickListener(){
        enteredInviteCode = getEditTextData(binding.edtInput1).concat(getEditTextData(binding.edtInput2))
                            .concat(getEditTextData(binding.edtInput3).concat(getEditTextData(binding.edtInput4)
                            .concat(getEditTextData(binding.edtInput5).concat(getEditTextData(binding.edtInput6)))));

        Toast.makeText(this, enteredInviteCode, Toast.LENGTH_SHORT).show();

        //navigating to next activity
        startActivity(new Intent(this,JoinGroupActivity.class));
    }

    //function for returning the edit text values
    private String getEditTextData(EditText editText){
        return editText.getText().toString().trim();
    }
}