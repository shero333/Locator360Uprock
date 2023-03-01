package com.care360.findmyfamilyandfriends.HomeScreen;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.care360.findmyfamilyandfriends.HomeScreen.FragmentLocation.LocationFragment;
import com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.FragmentSafety;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setting the location fragment as default
        replaceFragment(new LocationFragment());

        //items click listener
        clickListeners();
    }


    private void clickListeners() {

        //constraint location bottom click listener
        binding.consLocationHomeScreen.setOnClickListener(v -> locationClickListener());

        //constraint safety bottom click listener
        binding.consSafetyHomeScreen.setOnClickListener(v -> safetyClickListener());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home_screen, fragment);
        fragmentTransaction.commit();
    }

    private void locationClickListener() {

        //setting the background color of location layout to grey
        binding.consLocationHomeScreen.setBackgroundResource(R.drawable.drawable_grey_bottom);
        //setting the icon and text color of location
        binding.imgViewLocation.setImageResource(R.drawable.ic_loc_orange);
        binding.imgViewLocation.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
        binding.textViewLocation.setTextColor(getResources().getColor(R.color.orange));


        //setting the background of safety layout to white
        binding.consSafetyHomeScreen.setBackgroundResource(R.drawable.drawable_white_bottom);
        //setting the icon and text color of safety to grey
        binding.imgViewSafety.setImageResource(R.drawable.ic_safety);
        binding.imgViewSafety.setImageTintList(ColorStateList.valueOf(getColor(R.color.darkGrey)));
        binding.textViewSafety.setTextColor(getResources().getColor(R.color.darkGrey));

        //navigating to fragment location
        replaceFragment(new LocationFragment());

    }

    private void safetyClickListener() {

        //setting the background of safety layout to grey
        binding.consSafetyHomeScreen.setBackgroundResource(R.drawable.drawable_grey_bottom);
        //setting the icon and text color of safety to orange
        binding.imgViewSafety.setImageResource(R.drawable.ic_safety_orange);
        binding.imgViewSafety.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
        binding.textViewSafety.setTextColor(getResources().getColor(R.color.orange));


        //setting the background color of location layout to white
        binding.consLocationHomeScreen.setBackgroundResource(R.drawable.drawable_white_bottom);
        //setting the icon and text color of location
        binding.imgViewLocation.setImageResource(R.drawable.ic_loc);
        binding.imgViewLocation.setImageTintList(ColorStateList.valueOf(getColor(R.color.darkGrey)));
        binding.textViewLocation.setTextColor(getResources().getColor(R.color.darkGrey));

        //navigating to fragment safety
        replaceFragment(new FragmentSafety());

    }

}