package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencyContacts.LandingActivity.EmergencyContactLandingActivity;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencySOS.EmergencySOSActivity;
import com.care360.findmyfamilyandfriends.Permission.Permissions;
import com.care360.findmyfamilyandfriends.SharedPreference.SharedPreference;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.databinding.FragmentSafetyBinding;
import com.care360.findmyfamilyandfriends.databinding.LayoutSendSmsPermissionDialogBinding;

public class FragmentSafety extends Fragment {

    private FragmentSafetyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing binding
        binding = FragmentSafetyBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //help alert click listener
        binding.consHelpAlert.setOnClickListener(v -> {

            if(Permissions.hasSmsPermission(getContext())) {
                //navigate to emergency SOS activity
                navigateToEmergencySOSActivity();
            }
            else {
                sendSMSPermission();
            }
        });

        //add emergency contact click listener
        binding.btnAddEmergencyContact.setOnClickListener(v -> {

            // if any contact is added in DB, then Dashboard activity is called. Else, Contact Landing Activity is called
            if(SharedPreference.getEmergencyContactsStatus()) {
                Intent intent = new Intent(requireActivity(), EmergencyContactDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
            else if(!SharedPreference.getEmergencyContactsStatus()) {
                Intent intent =new Intent(requireActivity(), EmergencyContactLandingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        // saving current user full name in shared pref
        Commons.currentUserFullName();

        return view;
    }

    private void navigateToEmergencySOSActivity() {
        startActivity(new Intent(requireActivity(), EmergencySOSActivity.class));
    }

    private void sendSMSPermission() {

        // shows the info dialog
        Dialog smsDialog = new Dialog(getContext());

        LayoutSendSmsPermissionDialogBinding dialogBinding = LayoutSendSmsPermissionDialogBinding.inflate(getLayoutInflater());
        smsDialog.setContentView(dialogBinding.getRoot());

        //allow permission click listener
        dialogBinding.btnAllowPermission.setOnClickListener(v -> {
            smsResultLauncher.launch(Manifest.permission.SEND_SMS);
            smsDialog.dismiss();
        });

        // deny permission click listener
        dialogBinding.denyPermission.setOnClickListener(v -> smsDialog.dismiss());

        //setting the transparent background
        smsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //this sets the width of dialog to 90%
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels * 0.9);

        //setting the width and height of alert dialog
        smsDialog.getWindow().setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        smsDialog.show();

    }

    ActivityResultLauncher<String> smsResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

        if(result) {
            //permission is allowed, navigate to next activity
            navigateToEmergencySOSActivity();
        }
        else {
            Toast.makeText(getContext(), "SMS Permission Denied.", Toast.LENGTH_SHORT).show();
        }

    });

    //nullifying binding instance on view destroying
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}