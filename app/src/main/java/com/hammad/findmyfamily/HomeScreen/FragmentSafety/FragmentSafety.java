package com.hammad.findmyfamily.HomeScreen.FragmentSafety;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.Dashboard.EmergencyContactDashboardActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.LandingActivity.EmergencyContactLandingActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencySOS.EmergencySOSActivity;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.databinding.FragmentSafetyBinding;

public class FragmentSafety extends Fragment {

    private FragmentSafetyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing binding
        binding = FragmentSafetyBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //help alert click listener
        binding.consHelpAlert.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EmergencySOSActivity.class)));

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

    //nullifying binding instance on view destroying
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}