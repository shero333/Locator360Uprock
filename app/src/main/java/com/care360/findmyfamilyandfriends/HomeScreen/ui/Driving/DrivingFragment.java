package com.care360.findmyfamilyandfriends.HomeScreen.ui.Driving;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.care360.findmyfamilyandfriends.R;

public class DrivingFragment extends Fragment {

    private DrivingViewModel mViewModel;

    public static DrivingFragment newInstance() {
        return new DrivingFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driving, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DrivingViewModel.class);
        // TODO: Use the ViewModel
    }

}