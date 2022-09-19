package com.hammad.findmyfamily.HomeScreen;

import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements CircleAdapterToolbar.OnToolbarCircleClickListener {

    private ActivityHomeBinding binding;

    //recyclerview of extended toolbar
    private RecyclerView circleSelectionRecyclerView;

    //circle list
    private List<String> circleStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initializing binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //recyclerview initialization
        circleSelectionRecyclerView = binding.toolbarExtendedView.recyclerViewCircle;

        //setting the location fragment as default
        replaceFragment(new LocationFragment());

        //items click listener
        clickListeners();

        //setting the toolbar circle recyclerview
        selectCircleRecyclerview();
    }

    private void clickListeners() {

        //constraint location bottom click listener
        binding.consLocationHomeScreen.setOnClickListener(v -> locationClickListener());

        //constraint safety bottom click listener
        binding.consSafetyHomeScreen.setOnClickListener(v -> safetyClickListener());

        //toolbar settings click listener
        binding.toolbar.consSettingToolbar.setOnClickListener(v -> toolbarSettings());

        //toolbar messages click listener
        binding.toolbar.consChatToolbar.setOnClickListener(v -> toolbarChat());

        //toolbar circle name click listener
        binding.toolbar.consCircle.setOnClickListener(v -> circleExtendedView());

        //click listener of the extended toolbar view (circle selection view)
        extendedToolbarViewClickListeners();
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

    private void toolbarSettings() {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    private void toolbarChat() {
        Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
    }

    public void circleExtendedView() {

        //setting the view of toolbar to gone
        binding.toolbar.consToolbar.setVisibility(View.GONE);

        //setting the visibility of extended view to visible
        binding.toolbarExtendedView.consCircleSelection.setVisibility(View.VISIBLE);

    }

    private void extendedToolbarViewClickListeners() {

        //add member click listener
        binding.toolbarExtendedView.imgViewAddCircleMembers.setOnClickListener(v -> addCircleMember());

        //circle name click listener
        binding.toolbarExtendedView.consCircleName.setOnClickListener(v -> circleShrunkView());

        //button create circle click listener
        binding.toolbarExtendedView.btnCreateCircleToolbar.setOnClickListener(v -> createNewCircle());

        //button join circle click listener
        binding.toolbarExtendedView.btnJoinCircleToolbar.setOnClickListener(v -> joinCircle());

        // if this view is clicked, the extended view visibility will be set to gone
        binding.toolbarExtendedView.backgroundOpaqueView.setOnClickListener(v -> circleShrunkView());
    }

    private void addCircleMember() {
        Toast.makeText(this, "Add Member", Toast.LENGTH_SHORT).show();
    }

    private void circleShrunkView() {

        //setting the visibility of extended view to gone
        binding.toolbarExtendedView.consCircleSelection.setVisibility(View.GONE);

        //setting the toolbar view to visible
        binding.toolbar.consToolbar.setVisibility(View.VISIBLE);
    }

    private void createNewCircle() {
        Toast.makeText(this, "Create Circle", Toast.LENGTH_SHORT).show();
    }

    private void joinCircle() {
        Toast.makeText(this, "Join Circle", Toast.LENGTH_SHORT).show();
    }

    private void selectCircleRecyclerview() {

        circleStringList.add("Circle 1");
        circleStringList.add("Circle 2");
        circleStringList.add("Circle 3");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        circleSelectionRecyclerView.setLayoutManager(layoutManager);

        CircleAdapterToolbar adapterToolbar= new CircleAdapterToolbar(this,circleStringList,this);
        circleSelectionRecyclerView.setAdapter(adapterToolbar);
    }

    //toolbar circle name recyclerview click listener
    @Override
    public void onCircleSelected(int position) {

        Toast.makeText(this, circleStringList.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("HELLO_123", "onRequestPermissionsResult: Activity");

        if(requestCode == Constants.REQUEST_CODE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.i("HELLO_123", "onRequestPermissionsResult: if called Activity");
        }
        else
        {
            Toast.makeText(this, "Location Permission Denied Activity", Toast.LENGTH_SHORT).show();
        }
    }

}