package com.hammad.findmyfamily.HomeScreen.FragmentLocation.Settings.CircleManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.AddMember.AddMemberActivity;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.databinding.ActivityCircleManagementBinding;

public class CircleManagementActivity extends AppCompatActivity {

    private static final String TAG = "CIR_MANAG_ACT";

    ActivityCircleManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing binding
        binding = ActivityCircleManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //toolbar
        binding.toolbarCircleManagement.setNavigationOnClickListener(v -> onBackPressed());
        binding.toolbarCircleManagement.setTitle(SharedPreference.getCircleName());

        // add member click listener
        binding.textAddMembers.setOnClickListener(v -> startActivity(new Intent(this, AddMemberActivity.class)));

        // get current Cirlce info
        getCirlceInfo();
    }

    private void getCirlceInfo() {

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if(currentUserEmail.equals(SharedPreference.getCircleAdminId())) {
            // current user is admin of this circle

            // setting the visibility of admin views to visible
            binding.groupCircleDetails.setVisibility(View.VISIBLE);
            binding.groupAdmin.setVisibility(View.VISIBLE);

            // setting the visibility of no admin views to gone
            binding.groupNotAdmin.setVisibility(View.GONE);
        }
        else if(!currentUserEmail.equals(SharedPreference.getCircleAdminId())) {
            // current user not admin of this circle

            // setting the visibility of admin views to visible
            binding.groupCircleDetails.setVisibility(View.GONE);
            binding.groupAdmin.setVisibility(View.GONE);

            // setting the visibility of no admin views to gone
            binding.groupNotAdmin.setVisibility(View.VISIBLE);
        }

    }
}