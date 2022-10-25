package com.hammad.findmyfamily.HomeScreen.FragmentSafety;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hammad.findmyfamily.databinding.FragmentSafetyBinding;

public class FragmentSafety extends Fragment {

    private FragmentSafetyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("HELLO_123", "onCreateView: ");

        //initializing binding
        binding = FragmentSafetyBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //help alert click listener
        binding.consHelpAlert.setOnClickListener(v -> Toast.makeText(requireContext(), "Help Alert", Toast.LENGTH_SHORT).show());

        //add emergency contact click listener
        binding.btnAddEmergencyContact.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AddEmergencyContactActivity.class)));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("HELLO_123", "onCreate ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("HELLO_123", "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("HELLO_123", "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("HELLO_123", "onStop: ");
    }

    //nullifying binding instance on view destroying
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}