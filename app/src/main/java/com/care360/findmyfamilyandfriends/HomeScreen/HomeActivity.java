package com.care360.findmyfamilyandfriends.HomeScreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.care360.findmyfamilyandfriends.HomeScreen.ui.Driving.DrivingFragment;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentLocation.LocationFragment;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.FragmentSafety.FragmentSafety;
import com.care360.findmyfamilyandfriends.HomeScreen.ui.subscription.SubscriptionFragment;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.databinding.ActivityHomeBinding;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener, NavigationBarView.OnItemReselectedListener {

    private ActivityHomeBinding binding;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private AdLoader adLoader;
    private int count = 0;
    private int count2 = 0;
    private Fragment fragment = null;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //initializing binding
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        setAd();

        setContentView(view);
//
//        new AppBarConfiguration.Builder(
//                R.id.navigation_location,
//                R.id.navigation_safety,
//                R.id.navigation_driving,
//                R.id.navigation_subscription)
//                .build();

        fragment = new LocationFragment();
        replaceFragment(fragment);

        binding.navView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.navigation_location) {

                fragment = new LocationFragment();
                replaceFragment(fragment);
                item.setChecked(true);

            } else if (item.getItemId() == R.id.navigation_safety){
                fragment = new FragmentSafety();
                replaceFragment(fragment);
                item.setChecked(true);

            } else if (item.getItemId() == R.id.navigation_driving){
                fragment = new DrivingFragment();
                replaceFragment(fragment);
                item.setChecked(true);

            }else if (item.getItemId() == R.id.navigation_subscription){
                fragment = new SubscriptionFragment();
                replaceFragment(fragment);
                item.setChecked(true);
            }

            return false;
        });

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

}

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home_screen, fragment);
        fragmentTransaction.commit();
    }
//
//    private void locationClickListener() {
//
//        mInterstitialAd.show(this);
//
//        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                super.onAdDismissedFullScreenContent();
//
//                setAd();
//
//
//                //setting the background color of location layout to grey
//                binding.consLocationHomeScreen.setBackgroundResource(R.drawable.drawable_grey_bottom);
//                //setting the icon and text color of location
//                binding.imgViewLocation.setImageResource(R.drawable.ic_loc_orange);
//                binding.imgViewLocation.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
//                binding.textViewLocation.setTextColor(getResources().getColor(R.color.orange));
//
//
//                //setting the background of safety layout to white
//                binding.consSafetyHomeScreen.setBackgroundResource(R.drawable.drawable_white_bottom);
//                //setting the icon and text color of safety to grey
//                binding.imgViewSafety.setImageResource(R.drawable.ic_safety);
//                binding.imgViewSafety.setImageTintList(ColorStateList.valueOf(getColor(R.color.darkGrey)));
//                binding.textViewSafety.setTextColor(getResources().getColor(R.color.darkGrey));
//
//                //navigating to fragment location
//                replaceFragment(new LocationFragment());
//
//            }
//        });
//
//    }
//
//    private void safetyClickListener() {
//
//        mInterstitialAd.show(this);
//
//        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                super.onAdDismissedFullScreenContent();
//
//                setAd();
//
//                //setting the background of safety layout to grey
//                binding.consSafetyHomeScreen.setBackgroundResource(R.drawable.drawable_grey_bottom);
//                //setting the icon and text color of safety to orange
//                binding.imgViewSafety.setImageResource(R.drawable.ic_safety_orange);
//                binding.imgViewSafety.setImageTintList(ColorStateList.valueOf(getColor(R.color.orange)));
//                binding.textViewSafety.setTextColor(getResources().getColor(R.color.orange));
//
//
//                //setting the background color of location layout to white
//                binding.consLocationHomeScreen.setBackgroundResource(R.drawable.drawable_white_bottom);
//                //setting the icon and text color of location
//                binding.imgViewLocation.setImageResource(R.drawable.ic_loc);
//                binding.imgViewLocation.setImageTintList(ColorStateList.valueOf(getColor(R.color.darkGrey)));
//                binding.textViewLocation.setTextColor(getResources().getColor(R.color.darkGrey));
//
//                //navigating to fragment safety
//                replaceFragment(new FragmentSafety());
//            }
//        });
//    }

    private void setAd() {

        InterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/1033173712",
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {

                        Log.d("AdError", adError.toString());
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        Log.d("AdError", "Ad was loaded.");
                        mInterstitialAd = interstitialAd;
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//        switch (item.getItemId()) {
//            case R.id.navigation_location:
//                fragment = new LocationFragment();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_safety:
//                fragment = new FragmentSafety();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_driving:
//                fragment = new DrivingFragment();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_subscription:
//                fragment = new SubscriptionFragment();
//                replaceFragment(fragment);
//                break;
//            default:
//                return false;
//        }
        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.navigation_location:
//                fragment = new LocationFragment();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_safety:
//                fragment = new FragmentSafety();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_driving:
//                fragment = new DrivingFragment();
//                replaceFragment(fragment);
//                break;
//            case R.id.navigation_subscription:
//                fragment = new SubscriptionFragment();
//                replaceFragment(fragment);
//                break;
//            default:
//                break;
//        }
    }
}