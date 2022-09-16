package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.databinding.FragmentLocationBinding;

public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private FragmentLocationBinding binding;

    private FusedLocationProviderClient mLocationClient;

    private GoogleMap mGoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing view binding
        binding = FragmentLocationBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        //initializing map
        initializeMap();


        return view;
    }

    private void initializeMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());

        if(mapFragment != null)
        {
            mapFragment.getMapAsync(this);
        }

        mLocationClient = new FusedLocationProviderClient(requireContext());
    }

    private void checkLocationPermission(){
        //TODO: an alert dialog for location permission
        if(Permissions.hasLocationPermission(requireContext())){
            getCurrentLocation();
        }
        else {
            Permissions.getLocationPermission(requireActivity());
        }
    }

    private void getCurrentLocation() {
        Toast.makeText(requireContext(), "Current Location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //nullifying binding object
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}