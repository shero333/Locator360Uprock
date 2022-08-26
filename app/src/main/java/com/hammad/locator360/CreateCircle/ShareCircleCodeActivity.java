package com.hammad.locator360.CreateCircle;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.locator360.OneTimeScreens.AddProfilePictureActivity;
import com.hammad.locator360.R;
import com.hammad.locator360.databinding.ActivityShareCircleCodeBinding;

import java.util.ArrayList;
import java.util.List;

public class ShareCircleCodeActivity extends AppCompatActivity {

    private ActivityShareCircleCodeBinding binding;

    //List of all edit texts (6 here)
    List<EditText> editTextList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityShareCircleCodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //adding all the edit texts in list
        addEditTextViews();

        //populating the edit texts as invite code view
        populateInviteCodeView();

        //button share code click listener
        binding.btnShareCode.setOnClickListener(v -> Toast.makeText(this, "Share Code", Toast.LENGTH_SHORT).show());

        //text close click listener
        binding.txtClose.setOnClickListener(v -> startActivity(new Intent(this, AddProfilePictureActivity.class)));
    }

    private void addEditTextViews(){
        editTextList.add(binding.edtInputCircle1);
        editTextList.add(binding.edtInputCircle2);
        editTextList.add(binding.edtInputCircle3);
        editTextList.add(binding.edtInputCircle4);
        editTextList.add(binding.edtInputCircle5);
        editTextList.add(binding.edtInputCircle6);
    }

    private void populateInviteCodeView(){

        //requesting the focus at 1st edit text (as default)
        binding.edtInputCircle1.requestFocus();

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

    //checking the status (enabled/disabled) of submit button
    private void submitButtonStatus(){

        //checking if all Edit texts length are equal to 1
        if(isEditTextLengthZero(binding.edtInputCircle1) && isEditTextLengthZero(binding.edtInputCircle2) &&
                isEditTextLengthZero(binding.edtInputCircle3) && isEditTextLengthZero(binding.edtInputCircle4) &&
                isEditTextLengthZero(binding.edtInputCircle5) && isEditTextLengthZero(binding.edtInputCircle6))
        {
            //setting the submit button to enabled
            binding.btnShareCode.setEnabled(true);
            binding.btnShareCode.setBackgroundResource(R.drawable.white_rounded_button);
        }
        else{
            //setting the submit button to disabled
            binding.btnShareCode.setEnabled(false);
            binding.btnShareCode.setBackgroundResource(R.drawable.disabled_round_button);
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
}