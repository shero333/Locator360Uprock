package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencySOS;

import static com.hammad.findmyfamily.Util.Constants.USERS_COLLECTION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.hammad.findmyfamily.HomeScreen.HomeActivity;
import com.hammad.findmyfamily.StartScreen.StartScreenActivity;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.ActivityEmergencyLocationBinding;

public class EmergencyLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityEmergencyLocationBinding binding;

    private GoogleMap mGoogleMap;

    //boolean for checking whether this activity was opened when app is in foreground or background
    boolean isAppInForeground;

    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initial binding
        binding = ActivityEmergencyLocationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mGoogleMap.moveCamera(cameraUpdate);

                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
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
}