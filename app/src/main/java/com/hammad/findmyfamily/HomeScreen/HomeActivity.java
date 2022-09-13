package com.hammad.findmyfamily.HomeScreen;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hammad.findmyfamily.HomeScreen.CustomToolbar.CircleAdapterToolbar;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.LocationFragment;
import com.hammad.findmyfamily.HomeScreen.FragmentSafety.FragmentSafety;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    //for toolbar recyclerview
    private RecyclerView toolbarRecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //setting the value
        toolbarRecyclerview = binding.toolbar.recyclerViewToolbar;

        //setting the location fragment as default
        replaceFragment(new LocationFragment());

        //items click listener
        clickListeners();

        //setting the toolbar circle recyclerview
        setToolbarRecyclerview();
    }

    private void clickListeners(){

        //constraint location bottom click listener
        binding.consLocationHomeScreen.setOnClickListener(v -> locationClickListener());

        //constraint safety bottom click listener
        binding.consSafetyHomeScreen.setOnClickListener(v -> safetyClickListener());

        //toolbar settings click listener
        binding.toolbar.consSettingToolbar.setOnClickListener(v -> toolbarSettings());

        //toolbar messages click listener
        binding.toolbar.consSettingToolbar.setOnClickListener(v -> toolbarMessages());

        //toolbar circle name click listener
        binding.toolbar.textViewCircleName.setOnClickListener(v -> toolbarCircleName());

        //toolbar arrow down click listener
        binding.toolbar.imgViewCircleArrowDown.setOnClickListener(v -> toolbarCircleName());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home_screen,fragment);
        fragmentTransaction.commit();
    }

    private void locationClickListener(){

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

    private void safetyClickListener(){

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

    private void toolbarSettings(){
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    private void toolbarMessages(){
        Toast.makeText(this, "Messaging", Toast.LENGTH_SHORT).show();
    }

    public void toolbarCircleName(){
        Toast.makeText(this, "Circle is Clicked", Toast.LENGTH_SHORT).show();
    }

    private void setToolbarRecyclerview(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        toolbarRecyclerview.setLayoutManager(layoutManager);

        CircleAdapterToolbar adapterToolbar= new CircleAdapterToolbar(this);
        toolbarRecyclerview.setAdapter(adapterToolbar);
    }

}