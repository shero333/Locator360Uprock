package com.hammad.findmyfamily.HomeScreen.FragmentLocation.AddMember;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityAddMemberBinding;

public class AddMemberActivity extends AppCompatActivity {

    ActivityAddMemberBinding binding;

    //boolean for saving the view type clicked from Location Fragment
    boolean isToolbarAddMemberButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //getting the intent data from Location Fragment
        getIntentData();

        //toolbar back pressed
        binding.toolbarAddMember.setNavigationOnClickListener(v -> navigateToPreviousActivity());

        //circle name
        binding.txtCircleName.setText(SharedPreference.getCircleName());

        //circle code
        setCircleCode();

        //share invite code click listener
        binding.btnShareCode.setOnClickListener(v -> Toast.makeText(this, "Share Code", Toast.LENGTH_SHORT).show());
    }

    private void getIntentData() {

        Intent intent = getIntent();

        isToolbarAddMemberButtonClicked = intent.getBooleanExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, false);

    }

    private void navigateToPreviousActivity() {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADD_MEMBER_BUTTON_CLICKED, isToolbarAddMemberButtonClicked);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setCircleCode() {

        String inviteCode = SharedPreference.getCircleInviteCode();

        binding.textViewCircle1.setText(String.valueOf(inviteCode.charAt(0)));
        binding.textViewCircle2.setText(String.valueOf(inviteCode.charAt(1)));
        binding.textViewCircle3.setText(String.valueOf(inviteCode.charAt(2)));
        binding.textViewCircle4.setText(String.valueOf(inviteCode.charAt(3)));
        binding.textViewCircle5.setText(String.valueOf(inviteCode.charAt(4)));
        binding.textViewCircle6.setText(String.valueOf(inviteCode.charAt(5)));
    }

    @Override
    public void onBackPressed() {
        navigateToPreviousActivity();
    }
}