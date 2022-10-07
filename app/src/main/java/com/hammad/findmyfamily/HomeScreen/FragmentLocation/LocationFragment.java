package com.hammad.findmyfamily.HomeScreen.FragmentLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hammad.findmyfamily.HomeScreen.CustomToolbar.CircleAdapterToolbar;
import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.FragmentLocationBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationFragment extends Fragment implements OnMapReadyCallback, CircleAdapterToolbar.OnToolbarCircleClickListener/*, LocationListener*/, BottomSheetMemberAdapter.OnAddedMemberClickInterface, BottomSheetMemberAdapter.OnAddNewMemberInterface {

    private static final String TAG = "FRAG_LOCATION";

    private FragmentLocationBinding binding;

    private FusedLocationProviderClient mLocationClient;
    private GoogleMap mGoogleMap;
    private Location location;

    //string for saving location address
    private String locationAddress;

    //recyclerview of extended toolbar
    private RecyclerView circleSelectionRecyclerView;

    //circle list
    private final List<String> circleStringList = new ArrayList<>();

    //show and hide extended toolbar view animations
    Animation showToolbarExtAnim, hideToolbarExtAnim;

    //for location updates
    LocationRequest locationRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //initializing view binding
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //initializing map
        initializeMap();

        //checking location permission
        checkLocationPermission();

        //recyclerview initialization
        circleSelectionRecyclerView = binding.toolbarExtendedView.recyclerViewCircle;

        //items click listener
        clickListeners();

        //setting the toolbar circle recyclerview
        selectCircleRecyclerview();

        return view;
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());

        //assert keyword is used like 'if'. Like if(mapFragment != null){ mapFragment.getMapAsync(this); }
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        mLocationClient = new FusedLocationProviderClient(requireContext());
    }

    @SuppressLint("MissingPermission")
    private void checkLocationPermission() {

        if (Permissions.hasLocationPermission(requireContext())) {

            if (Commons.isGpsEnabled(requireActivity(), intent -> gpsActivityResultLauncher.launch(intent))) {
                Log.i(TAG, "checkLocationPermission: gps enabled");
                //get current location
                getCurrentLocation();
            } else {
                Log.i(TAG, "Location Frag: has location permission but no GPS");
                startLocationUpdates();
            }

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.REQUEST_CODE_LOCATION);
        }
    }

    ActivityResultLauncher<Intent> gpsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Log.i(TAG, "gpsActivityResultLauncher called: ");

                        //get current location
                        startLocationUpdates();
                        //getCurrentLocation();
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQUEST_CODE_LOCATION)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG, "location permissions allowed");

                //getting the current location
                checkLocationPermission();
            }
            else {
                Log.i(TAG, "location permission denied");

                //navigate to app settings screen
                Commons.locationPermissionDialog(requireActivity());
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                location = task.getResult();

                if(location != null) {

                    Log.i(TAG, "getCurrentLocation: if called 'location not null'");

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

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

                    //assert is used to check expected boolean condition
                    assert addresses != null;
                    locationAddress = addresses.get(0).getAddressLine(0);

                    Toast.makeText(requireContext(), locationAddress, Toast.LENGTH_LONG).show();

                }
                else {
                    Log.i(TAG, "getCurrentLocation: else called 'location is null'");
                }
            }

        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        /*Log.i(TAG, "startLocationUpdates: ");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        LocationCallback locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                for(Location loc : locationResult.getLocations()) {
                    location = loc;
                    Log.i(TAG, "lat: "+location.getLatitude());
                    Log.i(TAG, "lng: "+location.getLongitude());
                }
            }
        };

        mLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());*/

        Log.i(TAG, "startLocationUpdates: ");

        //location manager method
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationProvider locationProvider = locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);

        LocationListener locationListener = location -> {

            location = location;

            Log.i(TAG, "location listener: lat = "+location.getLatitude());
            Log.i(TAG, "location listener: lng = "+location.getLongitude());

        };

        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,5000,500,locationListener);

    }

    private void loadAnimations() {

        //show toolbar extended view animation
        showToolbarExtAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.top_anim);

        //hide toolbar extended view animation
        hideToolbarExtAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bottom_anim);
    }

    private void clickListeners() {

        //toolbar settings click listener
        binding.toolbar.consSettingToolbar.setOnClickListener(v -> toolbarSettings());

        //toolbar messages click listener
        binding.toolbar.consChatToolbar.setOnClickListener(v -> toolbarChat());

        //toolbar circle name click listener
        binding.toolbar.consCircle.setOnClickListener(v -> circleExtendedView());

        //check-in click listener
        binding.consCheckIn.setOnClickListener(v -> Toast.makeText(getContext(), "Check In", Toast.LENGTH_SHORT).show());

        //map type click listener
        binding.consMapType.setOnClickListener(v -> Toast.makeText(getContext(), "Map-Type", Toast.LENGTH_SHORT).show());

        //navigate to live location
        binding.consLiveLoc.setOnClickListener(v -> Toast.makeText(getContext(), "Live Location", Toast.LENGTH_SHORT).show());

        //click listener of the extended toolbar view (circle selection view)
        extendedToolbarViewClickListeners();

        //bottom sheet
        bottomSheetMembers();
    }

    private void toolbarSettings() {
        Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
    }

    private void toolbarChat() {
        Toast.makeText(requireContext(), "Chat", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(requireContext(), "Add Member", Toast.LENGTH_SHORT).show();
    }

    private void circleShrunkView() {

        //loading the toolbar extended view animations
        loadAnimations();

        binding.toolbarExtendedView.consCircleSelection.setAnimation(hideToolbarExtAnim);

        //the background of extended view visibility is set to gone to set the hide extended toolbar animation
        binding.toolbarExtendedView.backgroundOpaqueView.setVisibility(View.GONE);

        //setting visibility to gone after 300 millisecond
        new Handler().postDelayed(() -> binding.toolbarExtendedView.consCircleSelection.setVisibility(View.GONE), 300);

    }

    private void createNewCircle() {
        Toast.makeText(requireContext(), "Create Circle", Toast.LENGTH_SHORT).show();
    }

    private void joinCircle() {
        Toast.makeText(requireContext(), "Join Circle", Toast.LENGTH_SHORT).show();
    }

    public void circleExtendedView() {

        //loading the toolbar extended view animations
        loadAnimations();

        //setting the visibility of extended view to visible
        binding.toolbarExtendedView.backgroundOpaqueView.setVisibility(View.VISIBLE);

        //setting the background of extended view visibility to visible
        binding.toolbarExtendedView.consCircleSelection.setVisibility(View.VISIBLE);

        //setting the animation
        binding.toolbarExtendedView.consCircleSelection.setAnimation(showToolbarExtAnim);

    }

    private void selectCircleRecyclerview() {

        circleStringList.add("Circle 1");
        circleStringList.add("Circle 2");
        circleStringList.add("Circle 3");

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        circleSelectionRecyclerView.setLayoutManager(layoutManager);

        CircleAdapterToolbar adapterToolbar = new CircleAdapterToolbar(requireContext(), circleStringList, this);
        circleSelectionRecyclerView.setAdapter(adapterToolbar);
    }

    //toolbar circle name recyclerview click listener
    @Override
    public void onCircleSelected(int position) {

        Toast.makeText(requireContext(), circleStringList.get(position), Toast.LENGTH_SHORT).show();
    }

    private void bottomSheetMembers() {

        setBottomSheetRecyclerView();

        binding.bottomSheetMembers.consPlaces.setOnClickListener(v -> Toast.makeText(getContext(), "Places", Toast.LENGTH_SHORT).show());
    }

    private void setBottomSheetRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false);
        binding.bottomSheetMembers.recyclerBottomSheetMember.setLayoutManager(layoutManager);
        binding.bottomSheetMembers.recyclerBottomSheetMember.setAdapter(new BottomSheetMemberAdapter(requireContext(),5, this, this));
    }

    // recyclerview bottom sheet member 'Add new member' click listener
    @Override
    public void onAddNewMemberClicked() {
        Toast.makeText(requireContext(), "Add new member", Toast.LENGTH_SHORT).show();
    }

    // recyclerview bottom sheet member click listener
    @Override
    public void onAddedMemberClicked(int position) {
        Toast.makeText(requireContext(), "Member: " + position, Toast.LENGTH_SHORT).show();
    }

    //nullifying binding object
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /*@Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "onLocationChanged: ");
    }*/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

}