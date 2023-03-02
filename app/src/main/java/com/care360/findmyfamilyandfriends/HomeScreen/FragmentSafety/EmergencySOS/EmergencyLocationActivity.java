package com.care360.findmyfamilyandfriends.HomeScreen.FragmentSafety.EmergencySOS;

import static com.care360.findmyfamilyandfriends.Util.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.care360.findmyfamilyandfriends.HomeScreen.HomeActivity;
import com.care360.findmyfamilyandfriends.R;
import com.care360.findmyfamilyandfriends.StartScreen.StartScreenActivity;
import com.care360.findmyfamilyandfriends.Util.Commons;
import com.care360.findmyfamilyandfriends.Util.Constants;
import com.care360.findmyfamilyandfriends.databinding.ActivityEmergencyLocationBinding;

public class EmergencyLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityEmergencyLocationBinding binding;

    private GoogleMap mGoogleMap;

    //boolean for checking whether this activity was opened when app is in foreground or background
    boolean isAppInForeground;

    private FirebaseFirestore fStore;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initial binding
        binding = ActivityEmergencyLocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MobileAds.initialize(this);

        //banner
        adRequest = new AdRequest.Builder().build();

        binding.bannerAd.loadAd(adRequest);
        setAd();

        // map initialization
        initializeMap();

        //initializing Firebase firestore
        fStore = FirebaseFirestore.getInstance();

        // get boolean from intent
        Intent intent = getIntent();

        isAppInForeground = intent.getExtras().getBoolean(Constants.IS_APP_IN_FOREGROUND,true);

        if(isAppInForeground) {
            binding.toolbarEmergLocation.setNavigationOnClickListener(v -> checkCurrentLoginStatus());
        }
        else if(!isAppInForeground) {
            binding.toolbarEmergLocation.setNavigationOnClickListener(v -> navigateToStartScreen());
        }

    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(binding.mapSos.getId());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // gets the location from Intent and display it on map
        setLocationCoordinates();
    }

    private void setLocationCoordinates() {

        double lat,lng;

        Intent intent = getIntent();

        if(intent.getExtras() != null) {

            lat = Double.parseDouble(intent.getExtras().getString(Constants.FCM_LAT));
            lng = Double.parseDouble(intent.getExtras().getString(Constants.FCM_LNG));

            //sets the lat lng on map
            if(mGoogleMap != null) {

                LatLng latLng = new LatLng(lat,lng);

                //animate to the current lat lng
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mGoogleMap.moveCamera(cameraUpdate);

                MarkerOptions markerOptions = new MarkerOptions();

                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(lat);
                location.setLongitude(lng);

                markerOptions.position(latLng)
                             .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                             .anchor((float) 0.5,(float) 0.5);

                mGoogleMap.addMarker(markerOptions);

                mGoogleMap.setOnMarkerClickListener(marker -> {

                    //setting the address to textview
                    binding.txtLocAddress.setVisibility(View.VISIBLE);
                    binding.txtLocAddress.setText(Commons.getLocationAddress(this,location));
                    return false;
                });

            }
        }
    }

    @Override
    public void onBackPressed() {

        if(isAppInForeground) {
            checkCurrentLoginStatus();
        }
        else if(!isAppInForeground) {
            navigateToStartScreen();
        }

    }

    private void navigateToStartScreen() {
        startActivity(new Intent(this, StartScreenActivity.class));
        finish();
    }

    private void checkCurrentLoginStatus() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            DocumentReference documentReference = fStore.collection(USERS_COLLECTION).document(email);

            documentReference.get().addOnSuccessListener(documentSnapshot -> {

                startActivity(new Intent(this, HomeActivity.class));
                finish();
            });
        }
        else if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this,StartScreenActivity.class));
            finish();
        }

    }

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

}