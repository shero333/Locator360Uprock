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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.AddMember.AddMemberActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.CreateCircle.CreateCircleMainActivity;
import com.hammad.findmyfamily.HomeScreen.FragmentLocation.JoinCircle.JoinCircleMainActivity;
import com.hammad.findmyfamily.Permission.Permissions;
import com.hammad.findmyfamily.R;
import com.hammad.findmyfamily.SharedPreference.SharedPreference;
import com.hammad.findmyfamily.Util.Commons;
import com.hammad.findmyfamily.Util.Constants;
import com.hammad.findmyfamily.databinding.FragmentLocationBinding;
import com.hammad.findmyfamily.databinding.LayoutBottomSheetMapTypeBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class LocationFragment extends Fragment implements OnMapReadyCallback, CircleToolbarAdapter.OnToolbarCircleClickListener, LocationListener, BottomSheetMemberAdapter.OnAddedMemberClickInterface, BottomSheetMemberAdapter.OnAddNewMemberInterface {

    private static final String TAG = "FRAG_LOCATION";

    //circle list
    private final List<String> circleStringList = new ArrayList<>();

    //show and hide extended toolbar view animations
    Animation showToolbarExtAnim, hideToolbarExtAnim;

    private FragmentLocationBinding binding;

    private FusedLocationProviderClient mLocationClient;
    private GoogleMap mGoogleMap;
    private Location location;

    //string for saving location address
    private String locationAddress;

    //recyclerview of extended toolbar
    private RecyclerView circleSelectionRecyclerView;

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

        PowerManager powerManager = (PowerManager) requireActivity().getSystemService(Context.POWER_SERVICE);
        if (powerManager.isPowerSaveMode()) {
            Toast.makeText(getContext(), "ON", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "power saving on");
        }
        else if(!powerManager.isPowerSaveMode()) {
            Toast.makeText(getContext(), "OFF", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "power saving off");
        }

        return view;
    }

    private void initializeMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());

        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        mLocationClient = new FusedLocationProviderClient(requireContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;

        //when this listener is called, the live location icon visibility will be set to VISIBLE
        mGoogleMap.setOnCameraMoveListener(() -> binding.consLiveLoc.setVisibility(View.VISIBLE));
    }

    @SuppressLint("MissingPermission")
    private void checkLocationPermission() {

        Log.i(TAG, "checkLocationPermission: ");

        if (Permissions.hasLocationPermission(requireContext())) {

            if (Commons.isGpsEnabled(requireActivity(), intent -> gpsActivityResultLauncher.launch(intent))) {

                Log.i(TAG, "gps permission allowed: ");

                //get current location
                getLocationThroughLastKnownApproach();

            } else {
                Log.i(TAG, "Location Frag: has location permission but no GPS");
            }

        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, Constants.REQUEST_CODE_LOCATION);
        }
    }

    ActivityResultLauncher<Intent> gpsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {

            //get location
            getLocationThroughLastKnownApproach();
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //getting the current location
                checkLocationPermission();

            } else {

                //navigate to app settings screen
                Commons.locationPermissionDialog(requireActivity());
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void getLocationThroughLastKnownApproach() {

        Log.i(TAG, "getLocationThroughLastKnownApproach: ");

        // this function will call LocationListener overridden method when location changes
        startLocationUpdates();

        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                Log.i(TAG, "task successful: ");

                location = task.getResult();

                if (location != null) {

                    Log.i(TAG, "location != null");

                    updateMapMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                }
                else {
                    Log.i(TAG, "location == null");

                    /*
                        if task result returned has null location (case like when gps is turned on, it will first return null and after sometime location won't be null)
                        if that's the case, then will get location through mLocationClient.getCurrentLocation (currentLocationRequest, cancellationToken)
                    */
                    getLocationThroughCurrentLocationApproach();
                }
            }

        });
    }

    @SuppressLint("MissingPermission")
    private void getLocationThroughCurrentLocationApproach() {

        Log.i(TAG, "getLocationThroughCurrentLocationApproach: ");

        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder().build();

        CancellationToken cancellationToken = new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        };

        mLocationClient.getCurrentLocation(currentLocationRequest, cancellationToken).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Log.i(TAG, "task successful 2: ");

                location = task.getResult();

                if (location != null) {
                    Log.i(TAG, "location != null : 2nd");

                    // moves marker to the location
                    updateMapMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        //location manager method
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // 60000 milliseconds = 1 minute
        // 1000 milliseconds = 1 second
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 100, this::onLocationChanged);
    }

    // LocationListener overridden method
    @Override
    public void onLocationChanged(@NonNull Location location) {

        // saves the location in firebase firestore
        saveLocationInFirebase(location);

        //update the location on map
        updateMapMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void saveLocationInFirebase(Location location) {

        String currentUserEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        BatteryStatusModelClass batteryStatus = new BatteryStatusModelClass();
        batteryStatus = Commons.getCurrentBatteryStatus(requireContext());

        // location data
        Map<String,Object> locData = new HashMap<>();

        locData.put(Constants.LAT,location.getLatitude());
        locData.put(Constants.LNG,location.getLongitude());
        locData.put(Constants.LOC_ADDRESS,locationAddress);
        locData.put(Constants.IS_PHONE_CHARGING,batteryStatus.isCharging());
        locData.put(Constants.BATTERY_PERCENTAGE,batteryStatus.getBatteryPercentage());
        locData.put(Constants.IS_POWER_SAVING_ON,batteryStatus.isPowerSavingOn());

        FirebaseFirestore.getInstance().collection(Constants.USERS_COLLECTION)
                .document(currentUserEmail)
                .collection(Constants.LOCATION_COLLECTION)
                .document(String.valueOf(System.currentTimeMillis()))
                .set(locData)
                .addOnSuccessListener(unused -> Log.i(TAG, "Firestore location update successful"))
                .addOnFailureListener(e -> Log.e(TAG, "Error. Firestore location update: "+e.getMessage()));

    }

    //function for updating the the map marker to new position when location is changed
    private void updateMapMarker(LatLng latLng) {

        if (mGoogleMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mGoogleMap.moveCamera(cameraUpdate);

            //setting the map type from preference
            int mapTypePreference = SharedPreference.getMapType();

            switch (mapTypePreference) {

                case 0:
                    //map type is default/normal
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;

                case 1:
                    //map type is satellite
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;

                case 2:
                    //map type is hybrid
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;

                case 3:
                    //map type is terrain
                    if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;
            }

            if(mGoogleMap != null) {
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
            }

            //getting the address of current location
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null) {
                locationAddress = addresses.get(0).getAddressLine(0);
            }

        }
        else {
            Log.i(TAG, "updateMapMarker: map is null");
        }
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
        binding.consMapType.setOnClickListener(v -> bottomSheetMapType());

        //navigate to live location
        binding.consLiveLoc.setOnClickListener(v -> {

            //moves to current location
            checkLocationPermission();

            //after clicking and moving to current location, hides the live location view
            binding.consLiveLoc.setVisibility(View.GONE);
        });

        //click listener of the extended toolbar view (circle selection view)
        extendedToolbarViewClickListeners();

        //bottom sheet containing info about the current circle members
        bottomSheetMembers();
    }

    private void toolbarSettings() {

        Toast.makeText(requireContext(), "Settings", Toast.LENGTH_SHORT).show();
    }

    private void toolbarChat() {
        //Toast.makeText(requireContext(), "Chat", Toast.LENGTH_SHORT).show();

        Commons.signOut(requireActivity());
    }

    private void extendedToolbarViewClickListeners() {

        //add member click listener
        binding.toolbarExtendedView.imgViewAddCircleMembers.setOnClickListener(v -> addCircleMember(true));

        //circle name click listener
        binding.toolbarExtendedView.consCircleName.setOnClickListener(v -> circleShrunkView());

        //button create circle click listener
        binding.toolbarExtendedView.btnCreateCircleToolbar.setOnClickListener(v -> createNewCircle());

        //button join circle click listener
        binding.toolbarExtendedView.btnJoinCircleToolbar.setOnClickListener(v -> joinCircle());

        // if this view is clicked, the extended view visibility will be set to gone
        binding.toolbarExtendedView.backgroundOpaqueView.setOnClickListener(v -> circleShrunkView());
    }

    //we have add member button in extended toolbar view and bottom navigation as well
    private void addCircleMember(boolean isToolbarAddMemberBtnClicked) {

        Intent intent = new Intent(getActivity(), AddMemberActivity.class);
        intent.putExtra(Constants.ADD_MEMBER_BUTTON_CLICKED,isToolbarAddMemberBtnClicked);
        addMemberResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> addMemberResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == Activity.RESULT_OK) {

            Intent intent = result.getData();

            if(intent != null) {

                boolean isToolbarAddMemberBtnClicked = intent.getBooleanExtra(Constants.ADD_MEMBER_BUTTON_CLICKED,false);

                if (isToolbarAddMemberBtnClicked) {

                    //delay the shrunk to give an animation type look
                    delayCircleShrunkView();
                }
                else {

                    //collapse the bottom navigation
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            View bottomSheet = binding.bottomSheetMembers.consBottomSheet;
                            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    },300);

                }

            }
        }

    });

    private void createNewCircle() {
        createNewCircleResultLauncher.launch(new Intent(requireActivity(), CreateCircleMainActivity.class));
    }

    ActivityResultLauncher<Intent> createNewCircleResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == Activity.RESULT_OK) {

            //delay the shrunk to give an animation type look
            delayCircleShrunkView();

            Intent intent = result.getData();

            if(intent != null) {

                boolean isCircleCreated = intent.getBooleanExtra(Constants.IS_CIRCLE_CREATED,false);

                if(isCircleCreated) {

                    Toast.makeText(getContext(), "Circle Created Successfully.", Toast.LENGTH_SHORT).show();

                    //get the latest circle related data and set the newly created circle as selected
                }
            }

        }
    });

    private void joinCircle() {
        joinCircleResultLauncher.launch(new Intent(requireActivity(), JoinCircleMainActivity.class));
    }

    ActivityResultLauncher<Intent> joinCircleResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if(result.getResultCode() == Activity.RESULT_OK) {

            Intent intent = result.getData();

            if(intent != null) {

                //delay the shrunk to give an animation type look
                delayCircleShrunkView();

                Toast.makeText(getContext(), intent.getStringExtra(Constants.RETURNED_CIRCLE_NAME) + " joined.", Toast.LENGTH_LONG).show();

                //selects the currently joined circle and display related data
            }
        }
        else if(result.getResultCode() == Activity.RESULT_CANCELED) {
            //when back pressed is called from 'JoinCircleMainActivity'
            delayCircleShrunkView();
        }

    });

    //delays the extended toolbar view to give it an animation like flow when Activity Result Launchers are called
    private void delayCircleShrunkView() {

        new Handler().postDelayed(this::circleShrunkView,300);
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

        CircleToolbarAdapter adapterToolbar = new CircleToolbarAdapter(requireContext(), circleStringList, this);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.bottomSheetMembers.recyclerBottomSheetMember.setLayoutManager(layoutManager);
        binding.bottomSheetMembers.recyclerBottomSheetMember.setAdapter(new BottomSheetMemberAdapter(requireContext(), 5, this, this));
    }

    // recyclerview bottom sheet member 'Add new member' click listener
    @Override
    public void onAddNewMemberClicked() {
        addCircleMember(false);
    }

    // recyclerview bottom sheet member click listener
    @Override
    public void onAddedMemberClicked(int position) {
        Toast.makeText(requireContext(), "Member: " + position, Toast.LENGTH_SHORT).show();
    }

    private void bottomSheetMapType() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), com.google.android.material.R.style.Theme_Design_BottomSheetDialog);

        LayoutBottomSheetMapTypeBinding mapTypeBinding = LayoutBottomSheetMapTypeBinding.inflate(LayoutInflater.from(getContext()));

        bottomSheetDialog.setContentView(mapTypeBinding.getRoot());

        bottomSheetDialog.show();

        mapTypeBinding.consMapDefault.setOnClickListener(v -> setDefaultMapType(mapTypeBinding));

        mapTypeBinding.consMapSatellite.setOnClickListener(v -> setSatelliteMapType(mapTypeBinding));

        mapTypeBinding.consMapHybrid.setOnClickListener(v -> setHybridMapType(mapTypeBinding));

        mapTypeBinding.consMapTerrain.setOnClickListener(v -> setTerrainMapType(mapTypeBinding));

        // cancel click listener
        mapTypeBinding.imgViewCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // getting the preference value and setting value to map type
        int mapTypePref = SharedPreference.getMapType();

        switch (mapTypePref) {

            case 0:
                //map type is default/normal
                setDefaultMapType(mapTypeBinding);
                break;

            case 1:
                //map type is satellite
                setSatelliteMapType(mapTypeBinding);
                break;

            case 2:
                //map type is hybrid
                setHybridMapType(mapTypeBinding);
                break;

            case 3:
                //map type is terrain
                setTerrainMapType(mapTypeBinding);
                break;
        }

    }

    private void setDefaultMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(0);

        //change the map type
        if (mGoogleMap != null) mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setSatelliteMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(1);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        //change the rest of views
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setHybridMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(2);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));

    }

    private void setTerrainMapType(LayoutBottomSheetMapTypeBinding mapTypeBinding) {

        //highlight the selected view
        mapTypeBinding.cardTerrain.setCardBackgroundColor(requireContext().getColor(R.color.orange));
        mapTypeBinding.txtTerrain.setTextColor(requireContext().getColor(R.color.orange));

        //setting the value in preference
        SharedPreference.setMapType(3);

        //change the map type
        if (mGoogleMap != null) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }

        //change the rest of views
        mapTypeBinding.cardSatellite.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardDefault.setCardBackgroundColor(requireContext().getColor(R.color.white));
        mapTypeBinding.cardHybrid.setCardBackgroundColor(requireContext().getColor(R.color.white));

        //changing text color of rest of text views
        mapTypeBinding.txtSatellite.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtDefault.setTextColor(requireContext().getColor(R.color.black));
        mapTypeBinding.txtHybrid.setTextColor(requireContext().getColor(R.color.black));

    }

    //nullifying binding object
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}