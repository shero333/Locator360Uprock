package com.hammad.findmyfamily.HomeScreen.FragmentSafety;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.EmergencyContactsLandingActivity;
import com.hammad.findmyfamily.databinding.FragmentSafetyBinding;

public class FragmentSafety extends Fragment {

    private FragmentSafetyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing binding
        binding = FragmentSafetyBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //help alert click listener
        binding.consHelpAlert.setOnClickListener(v -> Toast.makeText(requireContext(), "Help Alert", Toast.LENGTH_SHORT).show());

        //add emergency contact click listener
        binding.btnAddEmergencyContact.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EmergencyContactsLandingActivity.class)));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    //nullifying binding instance on view destroying
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}