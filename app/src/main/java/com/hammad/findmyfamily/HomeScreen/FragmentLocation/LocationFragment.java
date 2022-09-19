package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.FragmentLocationBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    private FragmentLocationBinding binding;

    private FusedLocationProviderClient mLocationClient;

    private GoogleMap mGoogleMap;

    private Location location;

    //string for saving location address
    private String locationAddress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing view binding
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //initializing map
        initializeMap();

        //checking location permission
        checkLocationPermission();

        return view;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mLocationClient = new FusedLocationProviderClient(requireContext());
    }

    private void checkLocationPermission() {

        if (Permissions.hasLocationPermission(requireContext())) {

            if (Commons.isGpsEnabled(requireContext(), intent -> startActivityForResult(intent, Constants.GPS_REQUEST_CODE))) {

                //get current location
                getCurrentLocation();
            }

        } else {
            Permissions.getLocationPermission(requireActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("HELLO_123", "onRequestPermissionsResult: ");

        if(requestCode == Constants.REQUEST_CODE_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            getCurrentLocation();
            Log.i("HELLO_123", "onRequestPermissionsResult: if called");
        }
        else
        {
            Toast.makeText(requireContext(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.GPS_REQUEST_CODE) {

            //get current location after delay of 2 seconds
            new Handler().postDelayed(this::getCurrentLocation,2000);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            Log.i("TEST1", "complete listener");

            if(task.isSuccessful()){

                Log.i("TEST1", "task successful");

                if(location != null) {

                    Log.i("TEST1", "location not null");

                    location = task.getResult();

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                    mGoogleMap.moveCamera(cameraUpdate);
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    mGoogleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker()));

                    //getting the address of current location
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    locationAddress = addresses.get(0).getAddressLine(0);

                    Toast.makeText(requireContext(), locationAddress, Toast.LENGTH_LONG).show();
                }

                /*location = task.getResult();

                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                mGoogleMap.moveCamera(cameraUpdate);
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker()));

                //getting the address of current location
                Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                locationAddress = addresses.get(0).getAddressLine(0);

                Toast.makeText(requireContext(), locationAddress, Toast.LENGTH_LONG).show();*/
            }

        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.i("TEST1", "on map ready");
    }

    //nullifying binding object
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}